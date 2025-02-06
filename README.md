[![Tests](https://github.com/manami-project/modb-anidb/actions/workflows/tests.yml/badge.svg)](https://github.com/manami-project/modb-anidb/actions/workflows/tests.yml) [![codecov](https://codecov.io/gh/manami-project/modb-anidb/graph/badge.svg?token=YA6S36O4BH)](https://codecov.io/gh/manami-project/modb-anidb) ![jdk21](https://img.shields.io/badge/jdk-21-informational)
# modb-anidb

> [!WARNING]  
> The content of this repository has been moved to [https://github.com/manami-project/modb-app](https://github.com/manami-project/modb-app)

_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

## What does this lib do?
This lib contains downloader and converter for downloading raw data from anidb.net and convert it to an `Anime` object.
Don't use this lib to crawl the website entirely. Instead check whether [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database) already offers the data that you need.

## Configuration

| parameter                                 | type      | default | description                                                                                          |
|-------------------------------------------|-----------|---------|------------------------------------------------------------------------------------------------------|
| `modb.anidb.openBrowserOnCrawlerDetected` | `Boolean` | `false` | If set to `true` anidb.net is opened in the default browser in case of a `CrawlerDetectedException`. |