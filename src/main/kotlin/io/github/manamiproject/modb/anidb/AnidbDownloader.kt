package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.withContext

/**
 * Downloads anime data from anidb.net
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class AnidbDownloader(
    private val config: MetaDataProviderConfig,
    private val httpClient: HttpClient = DefaultHttpClient(isTestContext = config.isTestContext()),
) : Downloader {

    override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String = withContext(LIMITED_NETWORK) {
        val response = httpClient.get(config.buildDataDownloadLink(id).toURL())

        check(response.bodyAsText.isNotBlank()) { "Response body was blank for [anidbId=$id] with response code [${response.code}]" }

        val responseChecker = AnidbResponseChecker(response.bodyAsText).apply {
            checkIfCrawlerIsDetected()
        }

        check(response.isOk() || response.code == 404) { "Unexpected response code [anidbId=$id], [responseCode=${response.code}]" }

        return@withContext when {
            responseChecker.isHentai() || responseChecker.isRemovedFromAnidb() -> {
                log.info { "Adding [anidbId=$id] to dead-entries list" }
                onDeadEntry.invoke(id)
                EMPTY
            }
            responseChecker.isAdditionPending() -> EMPTY
            else -> response.bodyAsText
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}