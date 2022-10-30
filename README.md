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

[Link-SonatypeReleases]: https://s01.oss.sonatype.org/content/repositories/releases/io/github/palanga/picazio_3/ "Sonatype Releases"
[Link-SonatypeSnapshots]: https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/palanga/picazio_3/ "Sonatype Snapshots"

[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/s01.oss.sonatype.org/io.github.palanga/picazio_3.svg "Sonatype Releases"
[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.github.palanga/picazio_3.svg "Sonatype Snapshots"
