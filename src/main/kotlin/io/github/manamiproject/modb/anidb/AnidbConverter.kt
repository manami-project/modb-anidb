package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.Anime.Type.UNKNOWN
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URI
import java.time.Clock
import java.time.LocalDate

/**
 * Converts raw data to an [Anime]
 * @since 1.0.0
 * @param config Configuration for converting data.
 * @param clock Used to determine the current date. **Default:** `Clock.systemDefaultZone()`
 */
public class AnidbConverter(
    private val config: MetaDataProviderConfig = AnidbConfig,
    clock: Clock = Clock.systemDefaultZone()
) : AnimeConverter {

    private val currentDay = LocalDate.now(clock).dayOfMonth
    private val currentMonth = LocalDate.now(clock).monthValue
    private val currentYear = LocalDate.now(clock).year

    override fun convert(rawContent: String): Anime {
        val document = Jsoup.parse(rawContent)

        val picture = extractPicture(document)

        return Anime(
            _title = extractTitle(document),
            episodes = extractEpisodes(document),
            type = extractType(document),
            picture = picture,
            thumbnail = extractThumbnail(picture),
            status = extractStatus(document),
            duration = extractDuration(document),
            animeSeason = extractAnimeSeason(document)
        ).apply {
            addSources(extractSourcesEntry(document))
            addSynonyms(extractSynonyms(document))
            addRelations(extractRelatedAnime(document))
            addTags(extractTags(document))
        }
    }

    private fun extractTitle(document: Document) = document.select("h1.anime").text().replace("Anime: ", EMPTY)

    private fun extractEpisodes(document: Document): Int {
        val episodesString = document.select("span[itemprop=numberOfEpisodes]")?.text()?.trim()

        if (episodesString != null && episodesString.isNotBlank()) {
            return episodesString.toInt()
        }

        val typeCell = document.select("tr.type > td.value")?.text()?.trim()

        return if (typeCell != null && typeCell.lowercase().contains("unknown number of episodes")) {
            0
        } else {
            1
        }
    }

    private fun extractType(document: Document): Type {
        val typeCellContent = document.selectFirst("tr.type > td.value")?.text()?.trim() ?: EMPTY
        val type = if (typeCellContent.contains(',')) {
            typeCellContent.split(',')[0].trim()
        } else {
            typeCellContent.trim()
        }

        return when(type.lowercase()) {
            "movie" -> MOVIE
            "ova" -> OVA
            "web" -> ONA
            "tv special" -> SPECIAL
            "music video" -> SPECIAL
            "other" -> SPECIAL
            "tv series" -> TV
            "unknown" -> UNKNOWN
            else -> throw IllegalStateException("Unknown type [$type]")
        }
    }

    private fun extractPicture(document: Document): URI {
        val src = document.select("img[itemprop=image]").attr("src").trim()

        return if (src.isNotBlank()) {
            val uri = when {
                src.startsWith(EU_CDN) -> src.replace(EU_CDN, CDN)
                src.startsWith(US_CDN) -> src.replace(US_CDN, CDN)
                else -> src
            }

            URI(uri)
        } else {
            NO_PIC
        }
    }

    private fun extractThumbnail(picture: URI): URI {
        return if (picture == NO_PIC) {
            NO_PIC
        } else {
            URI("$picture-thumb.jpg")
        }
    }

    private fun extractSynonyms(document: Document): List<Title> {
        val synonyms = mutableListOf<String>()

        val tableFromTitlesTab = document.select("div.titles").select("table")

        tableFromTitlesTab.select("label[itemprop=alternateName]").map { it.text() }
            .map { it.trim() }
            .forEach { synonyms.add(it) }

        tableFromTitlesTab.select("tr.syn > td")?.text()?.trim()?.split(", ")
            ?.map { it.trim() }
            ?.forEach { synonyms.add(it) }

        tableFromTitlesTab.select("tr.short > td")?.text()?.trim()?.split(", ")
            ?.map { it.trim() }
            ?.forEach { synonyms.add(it) }

        return synonyms.distinct()
    }

    private fun extractSourcesEntry(document: Document): List<URI> {
        val hrefValue= document.select("link[property=og:url]").attr("href")?.trim()

        check(hrefValue != null) { "Sources link must not be null" }
        check(hrefValue.isNotBlank()) { "Sources link must not be blank" }

        return listOf(URI(hrefValue))
    }

    private fun extractRelatedAnime(document: Document): List<URI> {
        val linkElements = document.select("div#tab_main_1_1_pane[class=pane directly_related]")
            ?.select("a")
            ?.select("a:has(picture)")
            ?: emptyList<Element>()

        return linkElements.asSequence()
            .map { it.attr("href") }
            .map { it.replace("/anime/", EMPTY) }
            .distinct()
            .map { config.buildAnimeLink(it) }
            .toList()
    }

    private fun extractStatus(document: Document): Status {
        val releaseCell = document.selectFirst("tr.year > td.value")
        val startDate = releaseCell.select("span[itemprop=startDate]").text()
        val endDate = releaseCell.select("span[itemprop=endDate]").text()

        if ((startDate.isNotBlank() && endDate.isNotBlank()) || (startDate.isNotBlank() && releaseCell.text().contains("till"))) {
            var hasEnded = false
            var hasStarted = false

            val splitStartDate = startDate.split('.')

            when {
                splitStartDate[YEAR_ELEMENT].toInt() > currentYear -> hasStarted = false
                splitStartDate[YEAR_ELEMENT].toInt() < currentYear -> hasStarted = true
                splitStartDate[YEAR_ELEMENT].toInt() == currentYear -> {
                    when {
                        splitStartDate[MONTH_ELEMENT] == "??" -> hasStarted = false
                        splitStartDate[MONTH_ELEMENT].toInt() > currentMonth -> hasStarted = false
                        splitStartDate[MONTH_ELEMENT].toInt() < currentMonth -> hasStarted = true
                        splitStartDate[MONTH_ELEMENT].toInt() == currentMonth -> {
                            when {
                                splitStartDate[DAY_ELEMENT] == "??" -> hasStarted = false
                                splitStartDate[DAY_ELEMENT].toInt() > currentDay -> hasStarted = false
                                splitStartDate[DAY_ELEMENT].toInt() < currentDay -> hasStarted = true
                                splitStartDate[DAY_ELEMENT].toInt() == currentDay -> hasStarted = true
                            }
                        }
                    }
                }
            }

            val splitEndDate = endDate.split('.')

            if (splitEndDate.size == 3) {
                when {
                    splitEndDate[YEAR_ELEMENT].toInt() > currentYear -> hasEnded = false
                    splitEndDate[YEAR_ELEMENT].toInt() < currentYear -> hasEnded = true
                    splitEndDate[YEAR_ELEMENT].toInt() == currentYear -> {
                        when {
                            splitEndDate[MONTH_ELEMENT] == "??" -> hasEnded = false
                            splitEndDate[MONTH_ELEMENT].toInt() > currentMonth -> hasEnded = false
                            splitEndDate[MONTH_ELEMENT].toInt() < currentMonth -> hasEnded = true
                            splitEndDate[MONTH_ELEMENT].toInt() == currentMonth -> {
                                when {
                                    splitEndDate[DAY_ELEMENT] == "??" -> hasEnded = false
                                    splitEndDate[DAY_ELEMENT].toInt() > currentDay -> hasEnded = false
                                    splitEndDate[DAY_ELEMENT].toInt() < currentDay -> hasEnded = true
                                    splitEndDate[DAY_ELEMENT].toInt() == currentDay -> hasEnded = true
                                }
                            }
                        }
                    }
                }
            }

            return when {
                hasEnded -> FINISHED
                !hasStarted -> UPCOMING
                hasStarted && !hasEnded -> ONGOING
                else -> throw IllegalStateException("Unable to determine correct case for [startdate=$startDate, endDate=$endDate]")
            }
        }

        val datePublished = releaseCell.select("span[itemprop=datePublished]").text()
        val splitDatePublished = datePublished.split('.')

        if (datePublished.isNotBlank()) {
            return mapStatusToSimpleReleaseDate(
                day = splitDatePublished[DAY_ELEMENT],
                month = splitDatePublished[MONTH_ELEMENT],
                year = splitDatePublished[YEAR_ELEMENT]
            )
        }

        val pureText = releaseCell.text().trim()

        if (pureText.isNotBlank()) {
            return when {
                pureText == "?" -> Status.UNKNOWN
                Regex(".{2}\\..{2}\\..{4}").containsMatchIn(pureText) -> {
                    val splitPureText = pureText.split('.')
                    mapStatusToSimpleReleaseDate(
                        day = splitPureText[DAY_ELEMENT],
                        month = splitPureText[MONTH_ELEMENT],
                        year = splitPureText[YEAR_ELEMENT]
                    )
                }
                else -> throw IllegalStateException("Unable to determine correct case for [pureText=$pureText]")
            }
        }

        throw IllegalStateException("Unknown status [endDate=$endDate, datePublished=$datePublished, pureText=$pureText]")
    }

    private fun mapStatusToSimpleReleaseDate(day: String, month: String, year: String): Status {
        val exceptionMessage = "Unable to determine correct case for [day=$day, month=$month, year=$year]"

        return when {
            year.toInt() > currentYear -> UPCOMING
            year.toInt() < currentYear -> FINISHED
            year.toInt() == currentYear -> {
                when {
                    month == "??" -> UPCOMING
                    month.toInt() > currentMonth -> UPCOMING
                    month.toInt() < currentMonth -> FINISHED
                    month.toInt() == currentMonth -> {
                        when {
                            day == "??" -> UPCOMING
                            day.toInt() > currentDay -> UPCOMING
                            day.toInt() < currentDay -> FINISHED
                            day.toInt() == currentDay -> ONGOING
                            else -> throw IllegalStateException(exceptionMessage)
                        }
                    }
                    else -> throw IllegalStateException(exceptionMessage)
                }
            }
            else -> throw IllegalStateException(exceptionMessage)
        }
    }

    private fun extractDuration(document: Document): Duration {
        val duration = document.select("table#eplist")
            ?.select("tbody")
            ?.select("tr")
            ?.first()
            ?.select("td.duration")
            ?.text()
            ?.replace("m", EMPTY)
            ?.toInt() ?: 0

        return Duration(duration, MINUTES)
    }

    private fun extractAnimeSeason(document: Document): AnimeSeason {
        var cellTextContainingYear = document.select("span[itemprop=startDate]").text()

        if (cellTextContainingYear == null || cellTextContainingYear.isBlank()) {
           cellTextContainingYear = document.select("span[itemprop=datePublished]").text()
        }

        val month = Regex("\\.\\d{2}\\.").find(cellTextContainingYear)?.value
            ?.replace(Regex("\\."), EMPTY)
            ?.let {
                return@let if (it.startsWith('0')) {
                    it.trimStart('0').toInt()
                } else {
                    it.toInt()
                }
            } ?: 0
        val year = Regex("\\d{4}").find(cellTextContainingYear)?.value?.toInt() ?: 0

        val season = when(month) {
            1, 2, 3 -> WINTER
            4, 5, 6 -> SPRING
            7, 8, 9 -> SUMMER
            10, 11, 12 -> FALL
            else -> UNDEFINED
        }

        return AnimeSeason(
            season = season,
            year = year
        )
    }

    private fun extractTags(document: Document): List<Tag> = document.select("span[itemprop=genre]").map { it.text() }

    private companion object {
        private val NO_PIC = URI("https://cdn.myanimelist.net/images/qm_50.gif")
        private const val EU_CDN = "https://cdn-eu.anidb.net"
        private const val US_CDN = "https://cdn-us.anidb.net"
        private const val CDN = "https://cdn.anidb.net"
        private const val DAY_ELEMENT = 0
        private const val MONTH_ELEMENT = 1
        private const val YEAR_ELEMENT = 2
    }
}
