PicaZIO
=======

[![CI][Badge-CI]][Link-CI]
[![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases]
[![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots]

A web UI library made with ZIO and Laminar
------------------------------------------

Installation
------------

We publish to maven central, so you can add this to your `build.sbt` file:

```sbt
libraryDependencies += "dev.palanga" %% "picazio" % "version"
```

To get snapshot releases:

```sbt
resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
```


Usage
-----

* Create a Scala JS project (see `build.sbt` `examples` sub project)
* Create an `html` file and a scala entry point like in any other Scala JS project (see `examples`).
* Compile and link with `sbt fastLinkJS`.
* Open the html file in a browser.
* Make changes and link again with `sbt fastLinkJS`.

[//]: # (* For a production build run `TODO`)


[Link-CI]: https://github.com/palanga/picazio/actions/workflows/ci.yml "CI"
[Badge-CI]: https://github.com/palanga/picazio/actions/workflows/ci.yml/badge.svg "CI"

[Link-SonatypeReleases]: https://s01.oss.sonatype.org/content/repositories/releases/io/github/palanga/picazio-core_sjs1_3/ "Sonatype Releases"
[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/s01.oss.sonatype.org/io.github.palanga/picazio-core_sjs1_3.svg "Sonatype Releases"

[Link-SonatypeSnapshots]: https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/palanga/picazio-core_sjs1_3/ "Sonatype Snapshots"
[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.github.palanga/picazio-core_sjs1_3.svg "Sonatype Snapshots"
