package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.Anime.Type.UNKNOWN
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import io.github.manamiproject.modb.core.parseHtml
import kotlinx.coroutines.withContext
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
    clock: Clock = Clock.systemDefaultZone(),
) : AnimeConverter {

    private val currentDate = LocalDate.now(clock)

    override suspend fun convert(rawContent: String): Anime = withContext(LIMITED_CPU) {
        val document = parseHtml(rawContent)

        val picture = extractPicture(document)

        return@withContext Anime(
            _title = extractTitle(document),
            episodes = extractEpisodes(document),
            type = extractType(document),
            picture = picture,
            thumbnail = extractThumbnail(picture),
            status = extractStatus(document),
            duration = extractDuration(document),
            animeSeason = extractAnimeSeason(document),
        ).apply {
            addSources(extractSourcesEntry(document))
            addSynonyms(extractSynonyms(document))
            addRelations(extractRelatedAnime(document))
            addTags(extractTags(document))
        }
    }

    private fun extractTitle(document: Document) = document.select("h1.anime").text().replace("Anime: ", EMPTY)

    private fun extractEpisodes(document: Document): Int {
        val episodesString = document.select("span[itemprop=numberOfEpisodes]").text().trim()

        if (episodesString.isNotBlank()) {
            return episodesString.toInt()
        }

        val typeCell = document.select("tr.type > td.value").text().trim()

        return if (typeCell.lowercase().contains("unknown number of episodes")) {
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

        tableFromTitlesTab.select("tr.syn > td").text().trim().split(", ")
            .map { it.trim() }
            .forEach { synonyms.add(it) }

        tableFromTitlesTab.select("tr.short > td").text().trim().split(", ")
            .map { it.trim() }
            .forEach { synonyms.add(it) }

        return synonyms.distinct()
    }

    private fun extractSourcesEntry(document: Document): List<URI> {
        val id = document.select("input[type=hidden][name=aid]").first()!!.attr("value").trim()

        check(id.isNotBlank()) { "Sources link must not be blank" }

        return listOf(config.buildAnimeLink(id))
    }

    private fun extractRelatedAnime(document: Document): List<URI> {
        val linkElements = document.select("div#tab_main_1_1_pane[class=pane directly_related]")
            .select("a")
            .select("a:has(picture)")
            ?: emptyList<Element>()

        return linkElements.asSequence()
            .map { it.attr("href") }
            .map { it.replace("/anime/", EMPTY) }
            .distinct()
            .map { config.buildAnimeLink(it) }
            .toList()
    }

    private fun extractStatus(document: Document): Status {
        val releaseCell = document.selectFirst("tr.year > td.value")!!
        val startDateAttr = releaseCell.select("span[itemprop=startDate]").attr("content").trim()
        val endDateAttr = releaseCell.select("span[itemprop=endDate]").attr("content").trim()
        val isTimePeriod = releaseCell.text().trim().contains("until")

        val dateFormat = Regex("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})")

        if (isTimePeriod && startDateAttr.isNotBlank()) {
            val startDateMatch = dateFormat.find(startDateAttr)!!
            val startDate = LocalDate.of(
                startDateMatch.groups["year"]!!.value.toInt(),
                startDateMatch.groups["month"]!!.value.toInt(),
                startDateMatch.groups["day"]!!.value.toInt(),
            )

            val endDate = if (endDateAttr.isNotBlank()) {
                val endDateMatch = dateFormat.find(endDateAttr)!!
                LocalDate.of(
                    endDateMatch.groups["year"]!!.value.toInt(),
                    endDateMatch.groups["month"]!!.value.toInt(),
                    endDateMatch.groups["day"]!!.value.toInt(),
                )
            } else {
                currentDate.plusMonths(1)
            }

            return releaseDateToStatus(startDate, endDate)
        }

        val datePublishedAttr = releaseCell.select("span[itemprop=datePublished]").attr("content").trim()
        val isDatePublished = datePublishedAttr.isNotBlank()

        if (isDatePublished) {
            val startDateMatch = dateFormat.find(datePublishedAttr)!!
            val startDate = LocalDate.of(
                startDateMatch.groups["year"]!!.value.toInt(),
                startDateMatch.groups["month"]!!.value.toInt(),
                startDateMatch.groups["day"]!!.value.toInt(),
            )
            return releaseDateToStatus(startDate)
        }

        return Status.UNKNOWN
    }

    private fun releaseDateToStatus(startDate: LocalDate, endDate: LocalDate = startDate): Status {
        if (startDate != endDate) {
            return when {
                endDate.isBefore(currentDate) -> FINISHED
                startDate.isBefore(currentDate) && endDate.isAfter(currentDate) -> ONGOING
                else -> UPCOMING
            }
        }

        return when {
            startDate.isBefore(currentDate) -> FINISHED
            startDate.isAfter(currentDate) -> UPCOMING
            else -> ONGOING
        }
    }

    private fun extractDuration(document: Document): Duration {
        val duration = document.select("table#eplist")
            .select("tbody")
            .select("tr")
            .first()
            ?.select("td.duration")
            ?.text()
            ?.replace("m", EMPTY)
            ?.toInt() ?: 0

        return Duration(duration, MINUTES)
    }

    private fun extractAnimeSeason(document: Document): AnimeSeason {
        var cellTextContainingYear = document.select("span[itemprop=startDate]").text()

        if (cellTextContainingYear.isBlank()) {
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
    }
}
