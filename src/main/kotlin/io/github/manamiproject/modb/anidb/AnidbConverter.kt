package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.ExtractionResult
import io.github.manamiproject.modb.core.extractor.XmlDataExtractor
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Companion.NO_PICTURE
import io.github.manamiproject.modb.core.models.Anime.Companion.NO_PICTURE_THUMBNAIL
import io.github.manamiproject.modb.core.models.Anime.Status
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.Anime.Type.UNKNOWN
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import kotlinx.coroutines.withContext
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
    private val extractor: DataExtractor = XmlDataExtractor,
    clock: Clock = Clock.systemDefaultZone(),
) : AnimeConverter {

    private val currentDate = LocalDate.now(clock)

    override suspend fun convert(rawContent: String): Anime = withContext(LIMITED_CPU) {
        val data = extractor.extract(rawContent, mapOf(
            "title" to "//h1[contains(@class, 'anime')]/text()",
            "episodesString" to "//span[contains(@itemprop, 'numberOfEpisodes')]/text()",
            "episodesTypeCell" to "//tr[contains(@class, 'type')]/td[contains(@class, 'value')]/text()",
            "tags" to "//span[contains(@itemprop, 'genre')]/text()",
            "image" to "//img[contains(@itemprop, 'image')]/@src",
            "source" to "//input[contains(@type, 'hidden')][contains(@name, 'aid')]/@value",
            "type" to "//tr[contains(@class, 'type')]//th[contains(text(), 'Type')]/following-sibling::*/text()",
            "duration" to "//table[contains(@id, 'eplist')]/tbody/tr//td[contains(@class, 'duration')]/text()",
            "season" to "//tr[contains(@class, 'season')]//td[contains(@class, 'value')]/text()",
            "startDate" to "//tr[contains(@class, 'year')]//td[contains(@class, 'value')]//span[contains(@itemprop, 'startDate')]/@content",
            "datePublished" to "//tr[contains(@class, 'year')]//td[contains(@class, 'value')]//span[contains(@itemprop, 'datePublished')]/@content",
            "relatedAnime" to "//div[contains(@class, 'directly_related')]//a/@href",
            "alternateNames" to "//label[contains(@itemprop, 'alternateName')]/text()",
            "synonymsList" to "//div[contains(@class, 'titles')]//tr[contains(@class, 'syn')]/td/text()",
            "shortNames" to "//div[contains(@class, 'titles')]//tr[contains(@class, 'short')]/td/text()",
            "startDateAttr" to "//span[contains(@itemprop, 'startDate')]/@content",
            "endDateAttr" to "//span[contains(@itemprop, 'endDate')]/@content",
            "isTimePeriod" to "//tr[contains(@class, 'year')]/td[contains(@class, 'value')]/text()",
            "datePublishedAttr" to "//span[contains(@itemprop, 'datePublished')]/@content",
        ))

        val picture = extractPicture(data)

        return@withContext Anime(
            _title = extractTitle(data),
            episodes = extractEpisodes(data),
            type = extractType(data),
            picture = picture,
            thumbnail = extractThumbnail(picture),
            status = extractStatus(data),
            duration = extractDuration(data),
            animeSeason = extractAnimeSeason(data),
            sources = extractSourcesEntry(data),
            synonyms = extractSynonyms(data),
            relatedAnime = extractRelatedAnime(data),
            tags = extractTags(data),
        )
    }

    private fun extractTitle(data: ExtractionResult) = data.string("title").replace("Anime: ", EMPTY)

    private fun extractEpisodes(data: ExtractionResult): Int {
        val episodeString = data.stringOrDefault("episodesString").ifBlank {
            data.stringOrDefault("episodesTypeCell")
        }

        return if (episodeString.isBlank() || episodeString.contains("unknown number of episodes")) {
            1
        } else {
            episodeString.toIntOrNull() ?: 1
        }
    }

    private fun extractType(data: ExtractionResult): Type {
        val typeCellContent = data.stringOrDefault("type")
        val type = if (typeCellContent.contains(',')) {
            typeCellContent.split(',')[0].trim()
        } else {
            typeCellContent.trim()
        }

        return when(type.trimStart('[').trim().lowercase()) {
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

    private fun extractPicture(data: ExtractionResult): URI {
        val src = data.stringOrDefault("image")

        return if (src.isNotBlank()) {
            val uri = when {
                src.startsWith(EU_CDN) -> src.replace(EU_CDN, CDN)
                src.startsWith(US_CDN) -> src.replace(US_CDN, CDN)
                else -> src
            }

            URI(uri)
        } else {
            NO_PICTURE
        }
    }

    private fun extractThumbnail(picture: URI): URI {
        return if (picture == NO_PICTURE) {
            NO_PICTURE_THUMBNAIL
        } else {
            URI("$picture-thumb.jpg")
        }
    }

    private fun extractSynonyms(data: ExtractionResult): HashSet<Title> {
        val synonyms = hashSetOf<Title>()

        if (!data.notFound("alternateNames")) {
            data.listNotNull<Title>("alternateNames").forEach { synonyms.add(it) }
        }

        data.stringOrDefault("synonymsList").split(",").forEach { synonyms.add(it) }

        if (data.isOfType("shortNames", ArrayList::class) && !data.notFound("shortNames")) {
            data.listNotNull<Title>("shortNames").forEach { synonyms.add(it) }
        }

        if (data.isOfType("shortNames", String::class)) {
            data.stringOrDefault("shortNames").split(",").forEach { synonyms.add(it) }
        }

        return synonyms
    }

    private fun extractSourcesEntry(data: ExtractionResult): HashSet<URI> {
        val id = data.listNotNull<String>("source").first().trim()

        check(id.isNotBlank()) { "Sources link must not be blank" }

        return hashSetOf(config.buildAnimeLink(id))
    }

    private fun extractRelatedAnime(data: ExtractionResult): HashSet<URI> {
        if (data.notFound("relatedAnime")) {
            return hashSetOf()
        }

        return data.listNotNull<String>("relatedAnime")
            .asSequence()
            .filterNot { it.contains("relation") }
            .map { it.replace("/anime/", EMPTY) }
            .distinct()
            .map { config.buildAnimeLink(it) }
            .toHashSet()
    }

    private fun extractStatus(data: ExtractionResult): Status {
        val startDateAttr = data.stringOrDefault("startDateAttr")
        val endDateAttr = data.stringOrDefault("endDateAttr")
        val isTimePeriod = data.stringOrDefault("isTimePeriod").contains("until")

        if (isTimePeriod && startDateAttr.isNotBlank()) {
            val startDateMatch = DATEFORMAT.find(startDateAttr)!!
            val startDate = LocalDate.of(
                startDateMatch.groups["year"]!!.value.toInt(),
                startDateMatch.groups["month"]!!.value.toInt(),
                startDateMatch.groups["day"]!!.value.toInt(),
            )

            val endDate = if (endDateAttr.isNotBlank()) {
                val endDateMatch = DATEFORMAT.find(endDateAttr)!!
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

        val datePublishedAttr = data.stringOrDefault("datePublishedAttr").trim()
        val isDatePublished = datePublishedAttr.isNotBlank()

        if (isDatePublished) {
            val startDateMatch = DATEFORMAT.find(datePublishedAttr)!!
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

    private fun extractDuration(data: ExtractionResult): Duration {
        if (data.notFound("duration")) {
            return Duration.UNKNOWN
        }

        val duration = data.listNotNull<String>("duration")
            .firstOrNull()
            ?.replace("m", EMPTY)
            ?.toIntOrNull() ?: 0

        return Duration(duration, MINUTES)
    }

    private fun extractAnimeSeason(data: ExtractionResult): AnimeSeason {
        val seasonCell = data.stringOrDefault("season")
        var year = 0
        var season = AnimeSeason.Season.UNDEFINED

        if (seasonCell.isNotBlank()) {
            val seasonNameFormat = Regex("[aA-zZ]+")
            season = when (seasonNameFormat.find(seasonCell)?.value?.lowercase() ?: EMPTY) {
                "winter" -> WINTER
                "spring" -> SPRING
                "summer" -> SUMMER
                "autumn" -> FALL
                else -> UNDEFINED
            }

            val winterSeasonYearFormat = Regex("\\d+/\\d+")
            val defaultSeasonYearFormat = Regex("\\d{4}")

            year = if (winterSeasonYearFormat.containsMatchIn(seasonCell)) {
                winterSeasonYearFormat.find(seasonCell)?.value?.trim()?.split('/')?.get(0)?.toInt()?.plus(1) ?: 0
            } else {
                defaultSeasonYearFormat.find(seasonCell)?.value?.trim()?.toInt() ?: 0
            }
        }

        // startDate
        val startDate = data.stringOrDefault("startDate")

        if (startDate.isNotBlank() && (year == 0 || season == UNDEFINED)) {
            val startDateMatch = DATEFORMAT.find(startDate)!!
            val date = LocalDate.of(
                startDateMatch.groups["year"]!!.value.toInt(),
                startDateMatch.groups["month"]!!.value.toInt(),
                startDateMatch.groups["day"]!!.value.toInt(),
            )

            if (year == 0) {
                year = date.year
            }

            if (season == UNDEFINED) {
                season = when(date.month.value) {
                    1, 2, 3 -> WINTER
                    4, 5, 6 -> SPRING
                    7, 8, 9 -> SUMMER
                    10, 11, 12 -> FALL
                    else -> UNDEFINED
                }
            }
        }

        // date published
        val datePublished = data.stringOrDefault("datePublished")

        if (datePublished.isNotBlank() && (year == 0 || season == UNDEFINED)) {
            val datePublishedMatch = DATEFORMAT.find(datePublished)!!
            val date = LocalDate.of(
                datePublishedMatch.groups["year"]!!.value.toInt(),
                datePublishedMatch.groups["month"]!!.value.toInt(),
                datePublishedMatch.groups["day"]!!.value.toInt(),
            )

            if (year == 0) {
                year = date.year
            }

            if (season == UNDEFINED) {
                season = when(date.month.value) {
                    1, 2, 3 -> WINTER
                    4, 5, 6 -> SPRING
                    7, 8, 9 -> SUMMER
                    10, 11, 12 -> FALL
                    else -> UNDEFINED
                }
            }
        }

        return AnimeSeason(
            season = season,
            year = year,
        )
    }

    private fun extractTags(data: ExtractionResult): HashSet<Tag> {
        return if (data.notFound("tags")) {
            hashSetOf()
        } else {
            data.listNotNull<Tag>("tags").toHashSet()
        }
    }

    private companion object {
        private const val EU_CDN = "https://cdn-eu.anidb.net"
        private const val US_CDN = "https://cdn-us.anidb.net"
        private const val CDN = "https://cdn.anidb.net"
        private val DATEFORMAT = Regex("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})")
    }
}
