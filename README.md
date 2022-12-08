PicaZIO
=======

[![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases]
[![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots]

A web UI library made with ZIO and Laminar
------------------------------------------

Installation
------------

We publish to maven central so you just have to add this to your `build.sbt` file:

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
* Create an `html` file and a scala entry point (see `examples`).
* Compile and link with `sbt fastLinkJS`.
* Open the html file in a browser.
* Make changes and link again with `sbt fastLinkJS`.

[//]: # (* For a production build run `TODO`)


[Link-SonatypeReleases]: https://s01.oss.sonatype.org/content/repositories/releases/io/github/palanga/picazio-core_sjs1_3/ "Sonatype Releases"
[Link-SonatypeSnapshots]: https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/palanga/picazio-core_sjs1_3/ "Sonatype Snapshots"

[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/s01.oss.sonatype.org/io.github.palanga/picazio-core_sjs1_3.svg "Sonatype Releases"
[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.github.palanga/picazio-core_sjs1_3.svg "Sonatype Snapshots"
