package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.parseHtml
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

public object CrawlerDetectedException : RuntimeException("Crawler has been detected")

public class AnidbResponseChecker(response: String) {

    private val document by lazy { runBlocking { parseHtml(response) } }
    private val contentContainer by lazy { document.select("div#layout-content").select("div.container").text() }

    public suspend fun isAdditionPending(): Boolean = withContext(LIMITED_CPU) {
        contentContainer.startsWith("A request for the addition of this title to AniDB is currently")
    }

    public suspend fun isHentai(): Boolean = withContext(LIMITED_CPU) {
        contentContainer.startsWith("This anime is marked as 18+ restricted")
    }

    public suspend fun isRemovedFromAnidb(): Boolean = withContext(LIMITED_CPU) {
        contentContainer.startsWith("Unknown anime id.")
    }

    public suspend fun checkIfCrawlerIsDetected(): Unit = withContext(LIMITED_CPU) {
        val isAntiLeechPage = document.select("title").text() == "AniDB AntiLeech - AniDB"
        val isNginxPage = document.select("html > head > title").text() == "403 Forbidden"

        if (isAntiLeechPage || isNginxPage) {
            throw CrawlerDetectedException
        }
    }
}