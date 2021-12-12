package io.github.manamiproject.modb.anidb

import org.jsoup.Jsoup

public object CrawlerDetectedException : RuntimeException("Crawler has been detected")

public class AnidbResponseChecker(response: String) {

    private val document = Jsoup.parse(response)
    private val contentContainer = document.select("div#layout-content").select("div.container").text()

    public val isAdditionPending: Boolean = contentContainer.startsWith("A request for the addition of this title to AniDB is currently")
    public val isHentai: Boolean = contentContainer.startsWith("This anime is marked as 18+ restricted")
    public val isRemovedFromAnidb: Boolean = contentContainer.startsWith("Unknown anime id.")

    public fun checkIfCrawlerIsDetected() {
        val isAntiLeechPage = document.select("title").text() == "AniDB AntiLeech - AniDB"
        val isNginxPage = document.select("html > head > title").text() == "403 Forbidden"

        if (isAntiLeechPage || isNginxPage) {
            throw CrawlerDetectedException
        }
    }
}