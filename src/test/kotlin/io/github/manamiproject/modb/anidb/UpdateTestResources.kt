package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.coroutines.CoroutineManager.runCoroutine
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import java.nio.file.Path
import java.nio.file.Paths

internal fun main() {
    val downloader = AnidbDownloader(AnidbConfig)
    
    runCoroutine {
        downloader.download("14779").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_fall.html"))
        downloader.download("14397").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_spring.html"))
        downloader.download("14534").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_summer.html"))
        downloader.download("14430").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_winter.html"))
        downloader.download("14453").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_fall.html"))
        downloader.download("14107").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_spring.html"))
        downloader.download("14491").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_summer.html"))
        downloader.download("14602").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_winter.html"))
        downloader.download("10075").writeToFile(resourceFile("file_converter_tests/anime_season/season/undefined.html"))
    
        downloader.download("12665").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2017-10-03_-_unknown.html"))
        downloader.download("10755").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2017-10-14_-_2020.html"))
        downloader.download("14591").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2019-07-07_-_2019-09-22.html"))
        downloader.download("14679").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2019-08-23.html"))
        downloader.download("14238").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2019-10-05_-_2020-03.html"))
        downloader.download("12676").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/1986-06.html"))
        downloader.download("14988").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2020_-_unknown.html"))
        downloader.download("10077").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2004.html"))
        downloader.download("9987").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/date_published_but_with_time_period.html"))
        downloader.download("10075").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/not_available.html"))
        downloader.download("15911").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/unknown_date_of_year_-_unknown.html"))
    
        downloader.download("10052").writeToFile(resourceFile("file_converter_tests/duration/0_minutes.html"))
        downloader.download("10150").writeToFile(resourceFile("file_converter_tests/duration/1_hour.html"))
        downloader.download("10284").writeToFile(resourceFile("file_converter_tests/duration/1_minute.html"))
        downloader.download("10008").writeToFile(resourceFile("file_converter_tests/duration/2_hours.html"))
        downloader.download("1").writeToFile(resourceFile("file_converter_tests/duration/25_minutes.html"))
        downloader.download("10060").writeToFile(resourceFile("file_converter_tests/duration/missing.html"))
    
        downloader.download("5391").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
        downloader.download("7").writeToFile(resourceFile("file_converter_tests/episodes/1_but_more_entries.html"))
        downloader.download("1560").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
        downloader.download("5049").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
        downloader.download("6657").writeToFile(resourceFile("file_converter_tests/episodes/1818.html"))
        downloader.download("15625").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))
    
        downloader.download("14785").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/eu_cdn_replaced_by_default_cdn.html"))
        downloader.download("10137").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
        downloader.download("10538").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))
        downloader.download("14785").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/us_cdn_replaced_by_default_cdn.html"))
    
        downloader.download("193").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_related_anime.html"))
        downloader.download("14453").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime.html"))
    
        downloader.download("11221").writeToFile(resourceFile("file_converter_tests/sources/11221.html"))
    
        downloader.download("3348").writeToFile(resourceFile("file_converter_tests/status/start_to_end.html"))
        downloader.download("15625").writeToFile(resourceFile("file_converter_tests/status/start_to_unknown.html"))
        downloader.download("8857").writeToFile(resourceFile("file_converter_tests/status/date_published.html.html"))
        downloader.download("17862").writeToFile(resourceFile("file_converter_tests/status/unknown.html"))

        downloader.download("4563").writeToFile(resourceFile("file_converter_tests/synonyms/all_types.html"))
    
        downloader.download("4563").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags_with_similar.html"))
        downloader.download("15085").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags_without_similar.html"))
        downloader.download("15458").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
        downloader.download("12876").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))
    
        downloader.download("5459").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))
    
        downloader.download("112").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
        downloader.download("13077").writeToFile(resourceFile("file_converter_tests/type/music_video.html"))
        downloader.download("9907").writeToFile(resourceFile("file_converter_tests/type/other.html"))
        downloader.download("13248").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
        downloader.download("13246").writeToFile(resourceFile("file_converter_tests/type/tv_series.html"))
        downloader.download("12519").writeToFile(resourceFile("file_converter_tests/type/tv_special.html"))
        downloader.download("7608").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
        downloader.download("11788").writeToFile(resourceFile("file_converter_tests/type/web.html"))
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}