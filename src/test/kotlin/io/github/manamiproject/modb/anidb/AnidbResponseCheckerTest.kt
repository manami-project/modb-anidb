package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.loadTestResource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class AnidbResponseCheckerTest {

    @Nested
    inner class CrawlerDetectedTests {

        @Test
        fun `crawler is detected - anti leech page`() {
            // given
            val responseBodyAntiLeech = loadTestResource("downloader_tests/anti_leech_page.html")

            val responseChecker = AnidbResponseChecker(responseBodyAntiLeech)

            // when
            val result = exceptionExpected<RuntimeException> {
                responseChecker.checkIfCrawlerIsDetected()
            }

            // then
            assertThat(result).hasMessage("Crawler has been detected")
        }

        @Test
        fun `crawler is detected - nginx error page`() {
            // given
            val responseBodyAntiLeech = loadTestResource("downloader_tests/nginx_error_page.html")

            val responseChecker = AnidbResponseChecker(responseBodyAntiLeech)

            // when
            val result = exceptionExpected<RuntimeException> {
                responseChecker.checkIfCrawlerIsDetected()
            }

            // then
            assertThat(result).hasMessage("Crawler has been detected")
        }
    }

    @Nested
    inner class IsHentaiTests {

        @Test
        fun `isHentai returns false on regular entry`() {
            // given
            val responseBody = loadTestResource("file_converter_tests/title/special_chars.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = runBlocking {
                responseChecker.isHentai()
            }

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `isHentai returns true`() {
            // given
            val responseBody = loadTestResource("downloader_tests/hentai.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = runBlocking {
                responseChecker.isHentai()
            }

            // then
            assertThat(result).isTrue()
        }
    }

    @Nested
    inner class IsAdditionPendingTests {

        @Test
        fun `isAdditionPending returns false on regular entry`() {
            // given
            val responseBody = loadTestResource("file_converter_tests/title/special_chars.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = runBlocking {
                responseChecker.isAdditionPending()
            }

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `isAdditionPending returns true`() {
            // given
            val responseBody = loadTestResource("downloader_tests/addition_pending.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = runBlocking {
                responseChecker.isAdditionPending()
            }

            // then
            assertThat(result).isTrue()
        }
    }

    @Nested
    inner class IsRemovedFromAnidbTests {

        @Test
        fun `isRemovedFromAnidb returns false on regular entry`() {
            // given
            val responseBody = loadTestResource("file_converter_tests/title/special_chars.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = runBlocking {
                responseChecker.isRemovedFromAnidb()
            }

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `isRemovedFromAnidb returns true`() {
            // given
            val responseBody = loadTestResource("downloader_tests/deleted_entry.html")

            val responseChecker = AnidbResponseChecker(responseBody)

            // when
            val result = runBlocking {
                responseChecker.isRemovedFromAnidb()
            }

            // then
            assertThat(result).isTrue()
        }
    }
}