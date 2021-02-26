package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.test.loadTestResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class AnidbResponseCheckerTest {

    @Test
    fun `crawler is detected`() {
        // given
        val responseBodyAntiLeech = loadTestResource("downloader_tests/anti_leech_page.html")

        val responseChecker = AnidbResponseChecker(responseBodyAntiLeech)

        // when
        val result = org.junit.jupiter.api.assertThrows<RuntimeException> {
            responseChecker.checkIfCrawlerIsDetected()
        }

        // then
        assertThat(result).hasMessage("Crawler has been detected")
    }

    @Nested
    inner class isHentaiTests {

        @Test
        fun `isHentai returns false on regular entry`() {
            // given
            val responseBody = loadTestResource("file_converter_tests/title/special_chars.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = responseChecker.isHentai

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `isHentai returns true`() {
            // given
            val responseBody = loadTestResource("downloader_tests/hentai.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = responseChecker.isHentai

            // then
            assertThat(result).isTrue()
        }
    }

    @Nested
    inner class isAdditionPendingTests {

        @Test
        fun `isAdditionPending returns false on regular entry`() {
            // given
            val responseBody = loadTestResource("file_converter_tests/title/special_chars.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = responseChecker.isAdditionPending

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `isAdditionPending returns true`() {
            // given
            val responseBody = loadTestResource("downloader_tests/addition_pending.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = responseChecker.isAdditionPending

            // then
            assertThat(result).isTrue()
        }
    }

    @Nested
    inner class isRemovedFromAnidbTests {

        @Test
        fun `isRemovedFromAnidb returns false on regular entry`() {
            // given
            val responseBody = loadTestResource("file_converter_tests/title/special_chars.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = responseChecker.isRemovedFromAnidb

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `isRemovedFromAnidb returns true`() {
            // given
            val responseBody = loadTestResource("downloader_tests/deleted_entry.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = responseChecker.isRemovedFromAnidb

            // then
            assertThat(result).isTrue()
        }
    }
}