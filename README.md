PicaZIO
=======

[![CI][Badge-CI]][Link-CI]
[![Release Artifacts][Badge-SonatypeReleases]][Link-SonatypeReleases]
[![Snapshot Artifacts][Badge-SonatypeSnapshots]][Link-SonatypeSnapshots]

A ScalaJS web UI library made with ZIO and Laminar
--------------------------------------------------

```scala
import picazio._

object Main extends WebApp {
  override def root = Shape.text("hola")
}
```

Installation
------------

We publish to maven central, so you can add this to your `build.sbt` file:

```scala
libraryDependencies += "io.github.palanga" %%% "picazio-web" % "version"
```

To get snapshot releases:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
```

Development
-----------

* Create a Scala JS project (see `build.sbt` `examples` sub project)
* Create an `html` file and a scala entry point like in any other Scala JS project (see `examples`).
* Compile and link with `sbt fastLinkJS`.
* Open the html file in a browser.
* Make changes and link again with `sbt fastLinkJS`.

Testing
-------

We suggest using Selenium and installing browser drivers via npm. The following uses Chrome as the driver.

* Add the following line to `project/plugins.sbt`:

```scala
libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % version
```

* And the following line to `build.sbt` (possibly in the `settings/jsSettings` of Scala JS projects):

```scala
jsEnv in Test := new org.scalajs.jsenv.selenium.SeleniumJSEnv(new org.openqa.selenium.chrome.ChromeOptions())
```

* Make sure that `chromedriver` is available in your project. You can install it
  with `npm install chromedriver --save-dev`
* Run tests with `sbt test`

Build
-----

* For a production build run `sbt fullLinkJS`

[//]: # (links)

[Link-CI]: https://github.com/palanga/picazio/actions/workflows/ci.yml "CI"

[Badge-CI]: https://github.com/palanga/picazio/actions/workflows/ci.yml/badge.svg "CI"

[Link-SonatypeReleases]: https://s01.oss.sonatype.org/content/repositories/releases/io/github/palanga/picazio-core_sjs1_3/ "Sonatype Releases"

[Badge-SonatypeReleases]: https://img.shields.io/nexus/r/https/s01.oss.sonatype.org/io.github.palanga/picazio-core_sjs1_3.svg "Sonatype Releases"

[Link-SonatypeSnapshots]: https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/palanga/picazio-core_sjs1_3/ "Sonatype Snapshots"

[Badge-SonatypeSnapshots]: https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.github.palanga/picazio-core_sjs1_3.svg "Sonatype Snapshots"
