package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.parseHtml
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Thrown in case a the crawler has been detected.
 * @since 2.1.0
 */
public object CrawlerDetectedException : RuntimeException("Crawler has been detected")

/**
 * Checks the response for indicators of special cases.
 * @since 2.1.0
 * @param response raw HTML
 */
public class AnidbResponseChecker(response: String) {

    private val document by lazy { runBlocking { parseHtml(response) } }
    private val contentContainer by lazy { document.select("div#layout-content").select("div.container").text() }

    /**
     * Checks if an entry's addition is still pending. This means that the page doesn't contain any data.
     * @since 4.0.0
     * @return `true` if the entry is still pending addition.
     */
    public suspend fun isAdditionPending(): Boolean = withContext(LIMITED_CPU) {
        contentContainer.startsWith("A request for the addition of this title to AniDB is currently")
    }

    /**
     * Checks if an entry is only visible with an account, because it is an anime for adults only. In this case
     * the page doesn't contain any data.
     * @since 4.0.0
     * @return `true` if the entry is marked as for adults only.
     */
    public suspend fun isHentai(): Boolean = withContext(LIMITED_CPU) {
        contentContainer.startsWith("This anime is marked as 18+ restricted")
    }

    /**
     * Checks if an entry has been removed.
     * @since 4.0.0
     * @return `true` if the entry has been removed.
     */
    public suspend fun isRemovedFromAnidb(): Boolean = withContext(LIMITED_CPU) {
        contentContainer.startsWith("Unknown anime id.")
    }

    /**
     * Checks if crawler has been detected.
     * @since 4.0.0
     * @return `true` if the antileech page is shown.
     */
    public suspend fun checkIfCrawlerIsDetected(): Unit = withContext(LIMITED_CPU) {
        val isAntiLeechPage = document.select("title").text() == "AniDB AntiLeech - AniDB"
        val isNginxPage = document.select("html > head > title").text() == "403 Forbidden"

        if (isAntiLeechPage || isNginxPage) {
            throw CrawlerDetectedException
        }
    }
}