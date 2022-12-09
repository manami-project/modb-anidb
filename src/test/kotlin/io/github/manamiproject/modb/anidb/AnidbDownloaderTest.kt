package io.github.manamiproject.modb.anidb

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.toAnimeId
import io.github.manamiproject.modb.test.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test
import java.net.URI

internal class AnidbDownloaderTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Test
    fun `successfully download an anime`() {
        runBlocking {
            // given
            val id = "11376"

            val testConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun hostname(): Hostname = "localhost"
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            serverInstance.stubFor(
                get(urlPathEqualTo("/anime/$id")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(200)
                        .withBody("<html></html>")
                )
            )

            val downloader = AnidbDownloader(testConfig)

            // when
            val result = downloader.download(id) {
                shouldNotBeInvoked()
            }

            // then
            assertThat(result).isEqualTo("<html></html>")
        }
    }

    @Test
    fun `crawler is detected during download of anime and shows anti leech page`() {
        // given
        val id  = "11376"

        val testConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
        }

        val responseBodyAntiLeech = loadTestResource("downloader_tests/anti_leech_page.html")

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withStatus(200)
                    .withBody(responseBodyAntiLeech)
            )
        )

        val downloader = AnidbDownloader(testConfig)

        // when
        val result = exceptionExpected<RuntimeException> {
            downloader.download(id) { shouldNotBeInvoked() }
        }

        // then
        assertThat(result).hasMessage("Crawler has been detected")
    }

    @Test
    fun `crawler is detected during download of anime and shows nginx error page`() {
        // given
        val id  = "11376"

        val testConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
        }

        val responseBodyAntiLeech = loadTestResource("downloader_tests/nginx_error_page.html")

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withStatus(200)
                    .withBody(responseBodyAntiLeech)
            )
        )

        val downloader = AnidbDownloader(testConfig)

        // when
        val result = exceptionExpected<RuntimeException> {
            downloader.download(id) { shouldNotBeInvoked() }
        }

        // then
        assertThat(result).hasMessage("Crawler has been detected")
    }

    @Test
    fun `throws an exception due to an unknown http response code while trying to download an anime entry`() {
        // given
        val id = "11376"

        val testConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withStatus(502)
                    .withBody("<html></html>")
            )
        )

        val downloader = AnidbDownloader(testConfig)

        // when
        val result = exceptionExpected<IllegalStateException> {
            downloader.download(id) { shouldNotBeInvoked() }
        }

        // then
        assertThat(result).hasMessage("Unexpected response code [anidbId=$id], [responseCode=502]")
    }

    @Test
    fun `invoke onDeadEntry if the response body indicates a hentai`() {
        runBlocking {
            // given
            val id = "11376"
            var deadEntry = EMPTY

            val testConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun hostname(): Hostname = "localhost"
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val responseBody = loadTestResource("downloader_tests/hentai.html")

            serverInstance.stubFor(
                get(urlPathEqualTo("/anime/$id")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(200)
                        .withBody(responseBody)
                )
            )

            val downloader = AnidbDownloader(testConfig)

            // when
            val result = downloader.download(id) {
                deadEntry = it
            }

            // then
            assertThat(result).isEmpty()
            assertThat(deadEntry).isEqualTo(id)
        }
    }

    @Test
    fun `invoke onDeadEntry if the response body indicates a deleted entry`() {
        runBlocking {
            // given
            val id = "11376"
            var deadEntry = EMPTY

            val testConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun hostname(): Hostname = "localhost"
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val responseBody = loadTestResource("downloader_tests/deleted_entry.html")

            serverInstance.stubFor(
                get(urlPathEqualTo("/anime/$id")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(404)
                        .withBody(responseBody)
                )
            )

            val downloader = AnidbDownloader(testConfig)

            // when
            val result = downloader.download(id) {
                deadEntry = it
            }

            // then
            assertThat(result).isEmpty()
            assertThat(deadEntry).isEqualTo(id)
        }
    }

    @Test
    fun `won't invoke onDeadEntry and returns an empty string if the response body indicates that the addition of the anime is pending`() {
        runBlocking {
            // given
            val id = "11376"
            var deadEntry = EMPTY

            val testConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun hostname(): Hostname = "localhost"
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            val responseBody = loadTestResource("downloader_tests/addition_pending.html")

            serverInstance.stubFor(
                get(urlPathEqualTo("/anime/$id")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(200)
                        .withBody(responseBody)
                )
            )

            val downloader = AnidbDownloader(testConfig)

            // when
            val result = downloader.download(id) {
                deadEntry = it
            }

            // then
            assertThat(result).isEmpty()
            assertThat(deadEntry).isEmpty()
        }
    }

    @Test
    fun `return empty string if addition for anime is pending and the response code is 200`() {
        runBlocking {
            // given
            val id = "11376"

            val testConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun hostname(): Hostname = "localhost"
                override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
                override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
            }

            serverInstance.stubFor(
                get(urlPathEqualTo("/anime/$id")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(200)
                        .withBody(loadTestResource("downloader_tests/addition_pending.html"))
                )
            )

            val downloader = AnidbDownloader(testConfig)

            // when
            val result = downloader.download(id) {
                shouldNotBeInvoked()
            }

            // then
            assertThat(result).isEqualTo(EMPTY)
        }
    }

    @Test
    fun `throws an exception if the response body is empty`() {
        // given
        val id = 1535

        val testAnidbConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = AnidbConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
            override fun fileSuffix(): FileSuffix = AnidbConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withStatus(200)
                    .withBody(EMPTY)
            )
        )

        val downloader = AnidbDownloader(testAnidbConfig)

        // when
        val result = exceptionExpected<IllegalStateException> {
            downloader.download(id.toAnimeId()) { shouldNotBeInvoked() }
        }

        // then
        assertThat(result).hasMessage("Response body was blank for [anidbId=1535] with response code [200]")
    }
}