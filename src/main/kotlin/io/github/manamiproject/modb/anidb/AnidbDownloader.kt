package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.excludeFromTestContext
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.awt.Desktop
import java.net.URL

/**
 * Downloads anime data from anidb.net
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class AnidbDownloader(
    private val config: MetaDataProviderConfig,
    private val httpClient: HttpClient = DefaultHttpClient()
) : Downloader {

    override fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String {
        val response = httpClient.get(config.buildDataDownloadLink(id).toURL())

        check(response.body.isNotBlank()) { "Response body was blank for [anidbId=$id] with response code [${response.code}]" }

        val document = Jsoup.parse(response.body)

        checkIfCrawlerIsDetected(document)

        if (isHentai(document) || isRemovedFromAnidb(document)) {
            log.info("Add [anidbId={}] to dead-entries list", id)
            onDeadEntry.invoke(id)
            return EMPTY
        }

        return when (response.code) {
            200 -> {
                if (isAdditionPending(document)) {
                    EMPTY
                } else {
                    response.body
                }
            }
            else -> throw IllegalStateException("Unable to determine the correct case for [anidbId=$id], [responseCode=${response.code}]")
        }
    }

    private fun isAdditionPending(document: Document): Boolean {
        return document.select("div#layout-content")?.select("div.container")?.text()?.startsWith("A request for the addition of this title to AniDB is currently") ?: false
    }

    private fun isHentai(document: Document): Boolean {
        return document.select("div#layout-content")?.select("div.container")?.text()?.startsWith("This anime is marked as 18+ restricted") ?: false
    }

    private fun isRemovedFromAnidb(document: Document): Boolean {
        return document.select("div#layout-content")?.select("div.container")?.text()?.startsWith("Unknown anime id.") ?: false
    }

    private fun checkIfCrawlerIsDetected(document: Document) {
        if (document.select("title")?.text() == "AniDB AntiLeech - AniDB") {
            throw CrawlerDetectedException
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}