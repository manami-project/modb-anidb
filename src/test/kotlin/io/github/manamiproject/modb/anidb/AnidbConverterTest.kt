package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.*
import io.github.manamiproject.modb.test.loadTestResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC

internal class AnidbConverterTest {

    @Nested
    inner class TitleTests {

        @Test
        fun `title containing special chars`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/title/special_chars.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.title).isEqualTo(".hack//G.U. Trilogy")
        }
    }

    @Nested
    inner class EpisodesTests {

        @Test
        fun `1 episode, although more entries are listed - the selector returns null`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/1_but_more_entries.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isOne()
        }

        @Test
        fun `1 episode -  the selector returns null`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/1.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isOne()
        }

        @Test
        fun `10 episodes`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/10.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(10)
        }

        @Test
        fun `100 episodes`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/100.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(100)
        }

        @Test
        fun `1818 episodes`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/1818.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(1818)
        }

        @Test
        fun `unknown number of episodes`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/unknown.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(0)
        }
    }

    @Nested
    inner class TypeTests {

        @Test
        fun `type is Movie`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/movie.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(MOVIE)
        }

        @Test
        fun `type 'Music Video' is mapped to 'Special'`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/music_video.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(SPECIAL)
        }

        @Test
        fun `type 'Other' is mapped to 'Special'`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/other.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(SPECIAL)
        }

        @Test
        fun `type is OVA`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/ova.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(OVA)
        }

        @Test
        fun `type 'TV Series' is mapped to 'TV'`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/tv_series.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(TV)
        }

        @Test
        fun `type 'TV Special' is mapped to 'Special'`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/tv_special.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(SPECIAL)
        }

        @Test
        fun `type 'Unknown' is mapped to 'UNKNOWN'`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/unknown.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(Anime.Type.UNKNOWN)
        }

        @Test
        fun `type 'Web' is mapped to 'ONA'`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/web.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(ONA)
        }
    }

    @Nested
    inner class PictureAndThumbnailTests {

        @Test
        fun `neither picture nor thumbnail`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.picture).isEqualTo(URI("https://cdn.myanimelist.net/images/qm_50.gif"))
            assertThat(result.thumbnail).isEqualTo(URI("https://cdn.myanimelist.net/images/qm_50.gif"))
        }

        @Test
        fun `picture and thumbnail`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.picture).isEqualTo(URI("https://cdn.anidb.net/images/main/221838.jpg"))
            assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anidb.net/images/main/221838.jpg-thumb.jpg"))
        }

        @Test
        fun `eu cdn is replaced by default cdn for both picture and thumbnail`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/eu_cdn_replaced_by_default_cdn.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.picture).isEqualTo(URI("https://cdn.anidb.net/images/main/237686.jpg"))
            assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anidb.net/images/main/237686.jpg-thumb.jpg"))
        }

        @Test
        fun `us cdn is replaced by default cdn for both picture and thumbnail`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/us_cdn_replaced_by_default_cdn.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.picture).isEqualTo(URI("https://cdn.anidb.net/images/main/237686.jpg"))
            assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anidb.net/images/main/237686.jpg-thumb.jpg"))
        }
    }

    @Nested
    inner class SourcesTests {

        @Test
        fun `extract id 11221`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/sources/11221.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.sources).containsExactly(URI("https://anidb.net/anime/11221"))
        }
    }

    @Nested
    inner class RelatedAnimeTests {

        @Test
        fun `no relations`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/related_anime/no_related_anime.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.relatedAnime).isEmpty()
        }

        @Test
        fun `multiple relations`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/related_anime/multiple_related_anime.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.relatedAnime).containsExactly(
                URI("https://anidb.net/anime/367"),
                URI("https://anidb.net/anime/368"),
                URI("https://anidb.net/anime/405"),
                URI("https://anidb.net/anime/4576"),
                URI("https://anidb.net/anime/6141"),
                URI("https://anidb.net/anime/6393")
            )
        }
    }

    @Nested
    inner class SynonymsTests {

        @Test
        fun `gather all possible synonyms from info and titles tab`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/synonyms/all_types.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.synonyms).containsExactly(
                "Bilježnica smrti",
                "Caderno da Morte",
                "DEATH NOTE",
                "DN",
                "Death Note - A halállista",
                "Death Note - Carnetul morţii",
                "Death Note - Zápisník smrti",
                "Mirties Užrašai",
                "Notatnik śmierci",
                "Notes Śmierci",
                "Quaderno della Morte",
                "Sveska Smrti",
                "Ölüm Defteri",
                "Τετράδιο Θανάτου",
                "Бележник на Смъртта",
                "Записник Смерті",
                "Свеска Смрти",
                "Тетрадка на Смъртта",
                "Тетрадь cмерти",
                "Үхлийн Тэмдэглэл",
                "מחברת המוות",
                "دفترچه مرگ",
                "دفترچه یادداشت مرگ",
                "كـتـاب الـموت",
                "مدونة الموت",
                "مذكرة الموت",
                "موت نوٹ",
                "डेथ नोट",
                "デスノート",
                "死亡笔记",
                "데스노트"
            )
        }
    }

    @Nested
    inner class StatusTests {

        private val fixedClock = Clock.fixed(Instant.parse("2019-11-17T15:00:00.00Z"), UTC)

        @Test
        fun `'03-01-1999 till 28-03-1999' on startDate and endDate is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_endDate_03-01-1999_till_28-03-1999.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'29-06-1990' on datePublished is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_datePublished_29-06-1990.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'29-06-2019' on datePublished is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_datePublished_29-06-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'31-07-2019' on endDate is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_endDate_31-07-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'08-11-2019' on datePublished is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_datePublished_08-11-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'16-05-2014 till 16-08-2019' on endDate is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_endDate_16-05-2014_till_16-08-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'17-11-2016 till 03-11-2019' on endDate is mapped to FINISHED`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished_endDate_17-11-2016_till_03-11-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }

        @Test
        fun `'2022' on datePublished is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_datePublished_2022.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'2021 till unknown' on startDate and endDate is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_startDate_2021_till_unknown.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'2019' on datePublished is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_datePublished_2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'2019' on startDate is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_startDate_2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'29-11-2019 till unknown' on startDate is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_startDate_29-11-2019_till_unknown.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'13-12-2019' on datePublished is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_datePublished_13-12-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'23-11-2019 till unknown' on startDate is mapped to UPCOMING`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_startDate_23-11-2019_till_unknown.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `'01-11-2019 till 03-04-2020' on endDate is mapped to CURRENTLY`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/currently_endDate_01-11-2019_till_03-04-2020.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(ONGOING)
        }

        @Test
        fun `'17-11-2019 till unknown' on startDate is mapped to CURRENTLY`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/currently_startDate_17-11-2019_till_unknown.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(ONGOING)
        }

        @Test
        fun `'08-10-2019 till 24-12-2019' on endDate is mapped to CURRENTLY`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/currently_endDate_08-10-2019_till_24-12-2019.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(ONGOING)
        }

        @Test
        fun `'07-12-2013 till unknown' on endDate is mapped to CURRENTLY`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/currently_endDate_07-12-2013_till_unknown.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(ONGOING)
        }

        @Test
        fun `question mark is mapped to UNKNOWN`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/unknown.html")

            val converter = AnidbConverter(testAnidbConfig, fixedClock)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(Anime.Status.UNKNOWN)
        }
    }

    @Nested
    inner class TagsTests {

        @Test
        fun `extract multiple ignorig the link to find similar anime`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/multiple_tags_with_similar.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).containsExactly(
                "contemporary fantasy",
                "detective",
                "manga",
                "shounen",
                "thriller"
            )
        }

        @Test
        fun `empty list of no tags are available`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/no_tags.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).isEmpty()
        }

        @Test
        fun `extract multiple titles if the link to search for similar anime is not present`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/multiple_tags_without_similar.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).containsExactly(
                "new",
                "short movie"
            )
        }

        @Test
        fun `extract exactly one tag`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/one_tag.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).containsExactly(
                "new"
            )
        }
    }

    @Nested
    inner class DurationTests {

        @Test
        fun `null results in 0 seconds`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/missing.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(0, SECONDS))
        }

        @Test
        fun `0 minutes - anidb does not provide seconds`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/0_minutes.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(0, MINUTES))
        }

        @Test
        fun `1 minute`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/1_minute.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(1, MINUTES))
        }

        @Test
        fun `25 minutes`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/25_minutes.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(25, MINUTES))
        }

        @Test
        fun `1 hour`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/1_hour.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(1, HOURS))
        }

        @Test
        fun `2 hours`() {
            // given
            val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/2_hours.html")

            val converter = AnidbConverter(testAnidbConfig)

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(2, HOURS))
        }
    }

    @Nested
    inner class AnimeSeasonTests {

        @Nested
        inner class YearOfPremiereTests {

            @Test
            fun `2017-10-03 - unknown`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2017-10-03_-_unknown.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2017)
            }

            @Test
            fun `2019-07-07 - 2019-09-22`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2019-07-07_-_2019-09-22.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2019)
            }

            @Test
            fun `2019-08-23`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2019-08-23.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2019)
            }

            @Test
            fun `2017-10-14 - 2020`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2017-10-14_-_2020.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2017)
            }

            @Test
            fun `2019-10-05 - 2020-03`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2019-10-05_-_2020-03.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2019)
            }

            @Test
            fun `2022`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2022.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2022)
            }

            @Test
            fun `2020-06`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2020-06.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2020)
            }

            @Test
            fun `2020 - unknown`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2020_-_unknown.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2020)
            }

            @Test
            fun `not available`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/not_available.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(0)
            }

            @Test
            fun `unknown date with year - unknown`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/unknown_date_of_year_-_unknown.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2021)
            }
        }

        @Nested
        inner class SeasonTests {

            @Nested
            inner class DatePublishedTests {

                @Test
                fun `season is 'spring'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/datePublished_spring.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(SPRING)
                }

                @Test
                fun `season is 'summer'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/datePublished_summer.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(SUMMER)
                }

                @Test
                fun `season is 'fall'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/datePublished_fall.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(FALL)
                }

                @Test
                fun `season is 'winter'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/datePublished_winter.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(WINTER)
                }

                @Test
                fun `season is 'undefined'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/datePublished_undefined.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(UNDEFINED)
                }
            }

            @Nested
            inner class StartDateTests {

                @Test
                fun `season is 'spring'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/startDate_spring.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(SPRING)
                }

                @Test
                fun `season is 'summer'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/startDate_summer.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(SUMMER)
                }

                @Test
                fun `season is 'fall'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/startDate_fall.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(FALL)
                }

                @Test
                fun `season is 'winter'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/startDate_winter.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(WINTER)
                }

                @Test
                fun `season is 'undefined'`() {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/startDate_undefined.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(UNDEFINED)
                }
            }

            @Test
            fun `neither startDate nor datePublished exist therefore season is 'undefined'`() {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/season/undefined.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.season).isEqualTo(UNDEFINED)
            }
        }
    }
}