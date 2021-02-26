package io.github.manamiproject.modb.anidb

import org.jsoup.Jsoup

public object CrawlerDetectedException : RuntimeException("Crawler has been detected")

public class AnidbResponseChecker(response: String) {

    private val document = Jsoup.parse(response)
    private val contentContainer = document.select("div#layout-content")?.select("div.container")?.text()

    public val isAdditionPending: Boolean = contentContainer?.startsWith("A request for the addition of this title to AniDB is currently") ?: false
    public val isHentai: Boolean = contentContainer?.startsWith("This anime is marked as 18+ restricted") ?: false
    public val isRemovedFromAnidb: Boolean = contentContainer?.startsWith("Unknown anime id.") ?: false

    public fun checkIfCrawlerIsDetected() {
        if (document.select("title")?.text() == "AniDB AntiLeech - AniDB") {
            throw CrawlerDetectedException
        }
    }
}