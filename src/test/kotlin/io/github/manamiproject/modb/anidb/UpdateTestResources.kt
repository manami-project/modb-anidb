package io.github.manamiproject.modb.anidb

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = AnidbDownloader(AnidbConfig)
    
    runBlocking {
        downloader.downloadSuspendable("14779").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_fall.html"))
        downloader.downloadSuspendable("14397").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_spring.html"))
        downloader.downloadSuspendable("14534").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_summer.html"))
        downloader.downloadSuspendable("15448").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_undefined.html"))
        downloader.downloadSuspendable("14430").writeToFile(resourceFile("file_converter_tests/anime_season/season/datePublished_winter.html"))
        downloader.downloadSuspendable("14453").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_fall.html"))
        downloader.downloadSuspendable("14107").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_spring.html"))
        downloader.downloadSuspendable("14491").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_summer.html"))
        downloader.downloadSuspendable("15441").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_undefined.html"))
        downloader.downloadSuspendable("14602").writeToFile(resourceFile("file_converter_tests/anime_season/season/startDate_winter.html"))
        downloader.downloadSuspendable("10075").writeToFile(resourceFile("file_converter_tests/anime_season/season/undefined.html"))
    
        downloader.downloadSuspendable("12665").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2017-10-03_-_unknown.html"))
        downloader.downloadSuspendable("10755").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2017-10-14_-_2020.html"))
        downloader.downloadSuspendable("14591").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2019-07-07_-_2019-09-22.html"))
        downloader.downloadSuspendable("14679").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2019-08-23.html"))
        downloader.downloadSuspendable("14238").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2019-10-05_-_2020-03.html"))
        downloader.downloadSuspendable("8895").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2020-06.html"))
        downloader.downloadSuspendable("14988").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2020_-_unknown.html"))
        downloader.downloadSuspendable("10060").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2022.html"))
        downloader.downloadSuspendable("10075").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/not_available.html"))
        downloader.downloadSuspendable("15911").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/unknown_date_of_year_-_unknown.html"))
    
        downloader.downloadSuspendable("10052").writeToFile(resourceFile("file_converter_tests/duration/0_minutes.html"))
        downloader.downloadSuspendable("10150").writeToFile(resourceFile("file_converter_tests/duration/1_hour.html"))
        downloader.downloadSuspendable("10284").writeToFile(resourceFile("file_converter_tests/duration/1_minute.html"))
        downloader.downloadSuspendable("10008").writeToFile(resourceFile("file_converter_tests/duration/2_hours.html"))
        downloader.downloadSuspendable("1").writeToFile(resourceFile("file_converter_tests/duration/25_minutes.html"))
        downloader.downloadSuspendable("10060").writeToFile(resourceFile("file_converter_tests/duration/missing.html"))
    
        downloader.downloadSuspendable("5391").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
        downloader.downloadSuspendable("7").writeToFile(resourceFile("file_converter_tests/episodes/1_but_more_entries.html"))
        downloader.downloadSuspendable("1560").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
        downloader.downloadSuspendable("5049").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
        downloader.downloadSuspendable("6657").writeToFile(resourceFile("file_converter_tests/episodes/1818.html"))
        downloader.downloadSuspendable("16505").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))
    
        downloader.downloadSuspendable("14785").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/eu_cdn_replaced_by_default_cdn.html"))
        downloader.downloadSuspendable("10137").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
        downloader.downloadSuspendable("10538").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))
        downloader.downloadSuspendable("14785").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/us_cdn_replaced_by_default_cdn.html"))
    
        downloader.downloadSuspendable("193").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_related_anime.html"))
        downloader.downloadSuspendable("14453").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime.html"))
    
        downloader.downloadSuspendable("11221").writeToFile(resourceFile("file_converter_tests/sources/11221.html"))
    
        downloader.downloadSuspendable("14918").writeToFile(resourceFile("file_converter_tests/status/currently_endDate_01-11-2019_till_03-04-2020.html"))
        downloader.downloadSuspendable("10311").writeToFile(resourceFile("file_converter_tests/status/currently_endDate_07-12-2013_till_unknown.html"))
        downloader.downloadSuspendable("13441").writeToFile(resourceFile("file_converter_tests/status/currently_endDate_08-10-2019_till_24-12-2019.html"))
        downloader.downloadSuspendable("15067").writeToFile(resourceFile("file_converter_tests/status/currently_startDate_17-11-2019_till_unknown.html"))
        downloader.downloadSuspendable("14683").writeToFile(resourceFile("file_converter_tests/status/finished_datePublished_08-11-2019.html"))
        downloader.downloadSuspendable("10000").writeToFile(resourceFile("file_converter_tests/status/finished_datePublished_29-06-1990.html"))
        downloader.downloadSuspendable("10546").writeToFile(resourceFile("file_converter_tests/status/finished_datePublished_29-06-2019.html"))
        downloader.downloadSuspendable("1").writeToFile(resourceFile("file_converter_tests/status/finished_endDate_03-01-1999_till_28-03-1999.html"))
        downloader.downloadSuspendable("10761").writeToFile(resourceFile("file_converter_tests/status/finished_endDate_16-05-2014_till_16-08-2019.html"))
        downloader.downloadSuspendable("12407").writeToFile(resourceFile("file_converter_tests/status/finished_endDate_17-11-2016_till_03-11-2019.html"))
        downloader.downloadSuspendable("11216").writeToFile(resourceFile("file_converter_tests/status/finished_endDate_31-07-2019.html"))
        downloader.downloadSuspendable("10075").writeToFile(resourceFile("file_converter_tests/status/unknown.html"))
        downloader.downloadSuspendable("14071").writeToFile(resourceFile("file_converter_tests/status/upcoming_datePublished_13-12-2019.html"))
        downloader.downloadSuspendable("12710").writeToFile(resourceFile("file_converter_tests/status/upcoming_datePublished_2019.html"))
        downloader.downloadSuspendable("10060").writeToFile(resourceFile("file_converter_tests/status/upcoming_datePublished_2022.html"))
        downloader.downloadSuspendable("14259").writeToFile(resourceFile("file_converter_tests/status/upcoming_startDate_23-11-2019_till_unknown.html"))
        downloader.downloadSuspendable("13108").writeToFile(resourceFile("file_converter_tests/status/upcoming_startDate_29-11-2019_till_unknown.html"))
        downloader.downloadSuspendable("13018").writeToFile(resourceFile("file_converter_tests/status/upcoming_startDate_2019.html"))
        downloader.downloadSuspendable("12525").writeToFile(resourceFile("file_converter_tests/status/upcoming_startDate_2021_till_unknown.html"))
    
        downloader.downloadSuspendable("4563").writeToFile(resourceFile("file_converter_tests/synonyms/all_types.html"))
    
        downloader.downloadSuspendable("4563").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags_with_similar.html"))
        downloader.downloadSuspendable("15085").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags_without_similar.html"))
        downloader.downloadSuspendable("15458").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
        downloader.downloadSuspendable("12876").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))
    
        downloader.downloadSuspendable("5459").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))
    
        downloader.downloadSuspendable("112").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
        downloader.downloadSuspendable("13077").writeToFile(resourceFile("file_converter_tests/type/music_video.html"))
        downloader.downloadSuspendable("9907").writeToFile(resourceFile("file_converter_tests/type/other.html"))
        downloader.downloadSuspendable("13248").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
        downloader.downloadSuspendable("13246").writeToFile(resourceFile("file_converter_tests/type/tv_series.html"))
        downloader.downloadSuspendable("12519").writeToFile(resourceFile("file_converter_tests/type/tv_special.html"))
        downloader.downloadSuspendable("14935").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
        downloader.downloadSuspendable("11788").writeToFile(resourceFile("file_converter_tests/type/web.html"))
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}