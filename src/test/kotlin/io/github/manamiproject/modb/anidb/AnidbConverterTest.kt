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
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC

internal class AnidbConverterTest {

    @Nested
    inner class TitleTests {

        @Test
        fun `title containing special chars`() {
            runBlocking {
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
    }

    @Nested
    inner class EpisodesTests {

        @Test
        fun `1 episode, although more entries are listed - the selector returns null`() {
            runBlocking {
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
        }

        @Test
        fun `1 episode -  the selector returns null`() {
            runBlocking {
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
        }

        @Test
        fun `10 episodes`() {
            runBlocking {
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
        }

        @Test
        fun `100 episodes`() {
            runBlocking {
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
        }

        @Test
        fun `1818 episodes`() {
            runBlocking {
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
        }

        @Test
        fun `unknown number of episodes`() {
            runBlocking {
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
    }

    @Nested
    inner class TypeTests {

        @Test
        fun `type is Movie`() {
            runBlocking {
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
        }

        @Test
        fun `type 'Music Video' is mapped to 'Special'`() {
            runBlocking {
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
        }

        @Test
        fun `type 'Other' is mapped to 'Special'`() {
            runBlocking {
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
        }

        @Test
        fun `type is OVA`() {
            runBlocking {
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
        }

        @Test
        fun `type 'TV Series' is mapped to 'TV'`() {
            runBlocking {
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
        }

        @Test
        fun `type 'TV Special' is mapped to 'Special'`() {
            runBlocking {
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
        }

        @Test
        fun `type 'Unknown' is mapped to 'UNKNOWN'`() {
            runBlocking {
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
        }

        @Test
        fun `type 'Web' is mapped to 'ONA'`() {
            runBlocking {
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
    }

    @Nested
    inner class PictureAndThumbnailTests {

        @Test
        fun `neither picture nor thumbnail`() {
            runBlocking {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.picture).isEqualTo(URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/pics/no_pic.png"))
                assertThat(result.thumbnail).isEqualTo(URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/pics/no_pic_thumbnail.png"))
            }
        }

        @Test
        fun `picture and thumbnail`() {
            runBlocking {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.picture).isEqualTo(URI("https://cdn.anidb.net/images/main/221838.jpg"))
                assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anidb.net/images/main/221838.jpg-thumb.jpg"))
            }
        }

        @Test
        fun `eu cdn is replaced by default cdn for both picture and thumbnail`() {
            runBlocking {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/picture_and_thumbnail/eu_cdn_replaced_by_default_cdn.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.picture).isEqualTo(URI("https://cdn.anidb.net/images/main/257581.jpg"))
                assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anidb.net/images/main/257581.jpg-thumb.jpg"))
            }
        }

        @Test
        fun `us cdn is replaced by default cdn for both picture and thumbnail`() {
            runBlocking {
                // given
                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/picture_and_thumbnail/us_cdn_replaced_by_default_cdn.html")

                val converter = AnidbConverter(testAnidbConfig)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.picture).isEqualTo(URI("https://cdn.anidb.net/images/main/257581.jpg"))
                assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anidb.net/images/main/257581.jpg-thumb.jpg"))
            }
        }
    }

    @Nested
    inner class SourcesTests {

        @Test
        fun `extract id 11221`() {
            runBlocking {
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
    }

    @Nested
    inner class RelatedAnimeTests {

        @Test
        fun `no relations`() {
            runBlocking {
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
        }

        @Test
        fun `multiple relations`() {
            runBlocking {
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
    }

    @Nested
    inner class SynonymsTests {

        @Test
        fun `gather all possible synonyms from info and titles tab`() {
            runBlocking {
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
                    "دفتر الموت",
                    "دفترچه مرگ",
                    "دفترچه یادداشت مرگ",
                    "ديث نوت",
                    "كـتـاب الـموت",
                    "مدونة الموت",
                    "مذكرة المـوت",
                    "مذكرة الموت",
                    "موت نوٹ",
                    "डेथ नोट",
                    "デスノート",
                    "死亡笔记",
                    "데스노트",
                )
            }
        }
    }

    @Nested
    inner class StatusTests {

        @Test
        fun `FINISHED by date published`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2019-11-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/date_published.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(FINISHED)
            }
        }

        @Test
        fun `ONGOING by date published`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2013-06-13T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/date_published.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(ONGOING)
            }
        }

        @Test
        fun `UPCOMING by date published`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2012-11-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/date_published.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(UPCOMING)
            }
        }

        @Test
        fun `FINISHED by start to end`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2019-11-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/start_to_end.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(FINISHED)
            }
        }

        @Test
        fun `ONGOING by start to end`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2006-05-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/start_to_end.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(ONGOING)
            }
        }

        @Test
        fun `UPCOMING by start to end`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2005-11-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/start_to_end.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(UPCOMING)
            }
        }

        @Test
        fun `ONGOING by start to unknown`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2023-10-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/start_to_unknown.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(ONGOING)
            }
        }

        @Test
        fun `UPCOMING by start to unknown`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2022-10-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/start_to_unknown.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(UPCOMING)
            }
        }

        @Test
        fun `Neither time period nor date published is mapped to UNKNOWN`() {
            runBlocking {
                // given
                val fixedClock = Clock.fixed(Instant.parse("2019-11-17T15:00:00.00Z"), UTC)

                val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                }

                val testFile =
                    loadTestResource("file_converter_tests/status/unknown.html")

                val converter = AnidbConverter(testAnidbConfig, fixedClock)

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.status).isEqualTo(Anime.Status.UNKNOWN)
            }
        }
    }

    @Nested
    inner class TagsTests {

        @Test
        fun `extract multiple ignorig the link to find similar anime`() {
            runBlocking {
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
                    "thriller",
                )
            }
        }

        @Test
        fun `empty list of no tags are available`() {
            runBlocking {
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
        }

        @Test
        fun `extract multiple titles if the link to search for similar anime is not present`() {
            runBlocking {
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
                    "short movie",
                )
            }
        }

        @Test
        fun `extract exactly one tag`() {
            runBlocking {
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
    }

    @Nested
    inner class DurationTests {

        @Test
        fun `null results in 0 seconds`() {
            runBlocking {
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
        }

        @Test
        fun `0 minutes - anidb does not provide seconds`() {
            runBlocking {
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
        }

        @Test
        fun `1 minute`() {
            runBlocking {
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
        }

        @Test
        fun `25 minutes`() {
            runBlocking {
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
        }

        @Test
        fun `1 hour`() {
            runBlocking {
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
        }

        @Test
        fun `2 hours`() {
            runBlocking {
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
    }

    @Nested
    inner class AnimeSeasonTests {

        @Nested
        inner class YearOfPremiereTests {

            @Test
            fun `2017-10-03 - unknown`() {
                runBlocking {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile =
                        loadTestResource("file_converter_tests/anime_season/year_of_premiere/2017-10-03_-_unknown.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2017)
                }
            }

            @Test
            fun `2019-07-07 - 2019-09-22`() {
                runBlocking {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile =
                        loadTestResource("file_converter_tests/anime_season/year_of_premiere/2019-07-07_-_2019-09-22.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2019)
                }
            }

            @Test
            fun `2019-08-23`() {
                runBlocking {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile =
                        loadTestResource("file_converter_tests/anime_season/year_of_premiere/2019-08-23.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2019)
                }
            }

            @Test
            fun `2017-10-14 - 2020`() {
                runBlocking {
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
            }

            @Test
            fun `2019-10-05 - 2020-03`() {
                runBlocking {
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
            }

            @Test
            fun `2004`() {
                runBlocking {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2004.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2004)
                }
            }

            @Test
            fun `1986-06`() {
                runBlocking {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/1986-06.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(1986)
                }
            }

            @Test
            fun `2020 - unknown`() {
                runBlocking {
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
            }

            @Test
            fun `not available`() {
                runBlocking {
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
            }

            @Test
            fun `unknown date with year - unknown`() {
                runBlocking {
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

            @Test
            fun `time period but using date published`() {
                runBlocking {
                    // given
                    val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/date_published_but_with_time_period.html")

                    val converter = AnidbConverter(testAnidbConfig)

                    // when
                    val result = converter.convert(testFile)

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2014)
                }
            }
        }

        @Nested
        inner class SeasonTests {

            @Nested
            inner class SeasonCellTests {

                @Test
                fun `season is 'spring'`() {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/season_cell_spring.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(SPRING)
                    }
                }

                @Test
                fun `season is 'summer'`() {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/season_cell_summer.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(SUMMER)
                    }
                }

                @Test
                fun `season is 'fall'`() {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/season_cell_autumn.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(FALL)
                    }
                }

                @Test
                fun `season is 'winter'`() {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/season_cell_winter.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(WINTER)
                    }
                }
            }

            @Nested
            inner class DatePublishedCellTests {

                @ParameterizedTest
                @ValueSource(strings = ["date_published_cell_apr", "date_published_cell_may", "date_published_cell_jun"])
                fun `season is 'spring'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(SPRING)
                    }
                }

                @ParameterizedTest
                @ValueSource(strings = ["date_published_cell_jul", "date_published_cell_aug", "date_published_cell_sep"])
                fun `season is 'summer'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(SUMMER)
                    }
                }

                @ParameterizedTest
                @ValueSource(strings = ["date_published_cell_oct", "date_published_cell_nov", "date_published_cell_dec"])
                fun `season is 'fall'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile = loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(FALL)
                    }
                }

                @ParameterizedTest
                @ValueSource(strings = ["date_published_cell_jan", "date_published_cell_feb", "date_published_cell_mar"])
                fun `season is 'winter'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(WINTER)
                    }
                }
            }

            @Nested
            inner class StartDateCellTests {

                @ParameterizedTest
                @ValueSource(strings = ["start_date_cell_apr", "start_date_cell_may", "start_date_cell_jun"])
                fun `season is 'spring'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(SPRING)
                    }
                }

                @ParameterizedTest
                @ValueSource(strings = ["start_date_cell_jul", "start_date_cell_aug", "start_date_cell_sep"])
                fun `season is 'summer'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(SUMMER)
                    }
                }

                @ParameterizedTest
                @ValueSource(strings = ["start_date_cell_oct", "start_date_cell_nov", "start_date_cell_dec"])
                fun `season is 'fall'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile = loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(FALL)
                    }
                }

                @ParameterizedTest
                @ValueSource(strings = ["start_date_cell_jan", "start_date_cell_feb", "start_date_cell_mar"])
                fun `season is 'winter'`(file: String) {
                    runBlocking {
                        // given
                        val testAnidbConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                            override fun buildDataDownloadLink(id: String): URI = AnidbConfig.buildDataDownloadLink(id)
                            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
                        }

                        val testFile =
                            loadTestResource("file_converter_tests/anime_season/season/$file.html")

                        val converter = AnidbConverter(testAnidbConfig)

                        // when
                        val result = converter.convert(testFile)

                        // then
                        assertThat(result.animeSeason.season).isEqualTo(WINTER)
                    }
                }
            }

            @Test
            fun `neither startDate nor datePublished exist therefore season is 'undefined'`() {
                runBlocking {
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
}