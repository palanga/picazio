name := "picazio"

val SCALA_2                 = "2.13.16"
val SCALA_3                 = "3.3.5"
val MAIN_SCALA              = SCALA_2
val ALL_SCALA               = Seq(SCALA_3, SCALA_2)
val ZIO_VERSION             = "2.1.16"
val LAMINAR_VERSION         = "17.2.0"
val SCALA_JAVA_TIME_VERSION = "2.5.0"
val SCALA_TEST_VERSION      = "3.2.19"
val ZIO_HTTP_VERSION        = "3.0.0"

val KEEP_BROWSER_ALIVE = false

import org.openqa.selenium.chrome.ChromeOptions
import org.scalajs.jsenv.selenium.SeleniumJSEnv

inThisBuild(
  List(
    scalaVersion           := MAIN_SCALA,
    crossScalaVersions     := ALL_SCALA,
    versionScheme          := Some("early-semver"),
    organization           := "io.github.palanga",
    homepage               := Some(url("https://github.com/palanga/picazio")),
    licenses               := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    resolvers += "Sonatype OSS Releases" at "https://s01.oss.sonatype.org/content/repositories/releases",
    resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
    developers             := List(
      Developer(
        "palanga",
        "Andrés González",
        "a.gonzalez.terres@gmail.com",
        url("https://github.com/palanga/"),
      )
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
addCommandAlias("testcompile", "Test/compile")
addCommandAlias("crosstestcompile", "+Test/compile")

lazy val root =
  (project in file("."))
    .settings(
      publish / skip := true
    )
    .aggregate(
      core,
      web,
      web_dev_server,
      examples,
      chat_example_common,
      chat_example_server,
      chat_example_ui,
    )

lazy val core =
  (project in file("core"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name        := "picazio-core",
      description := "A web UI library made with ZIO and Laminar",
      libraryDependencies ++= Seq(
        "dev.zio"       %%% "zio"         % ZIO_VERSION,
        "dev.zio"       %%% "zio-streams" % ZIO_VERSION,
        "org.scalatest" %%% "scalatest"   % SCALA_TEST_VERSION % Test,
      ),
      Test / skip := false,
      commonSettings,
    )

lazy val web =
  (project in file("web"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name                     := "picazio-web",
      description              := "Web interpreter for PicaZIO made with Laminar",
      libraryDependencies ++= Seq(
        "com.raquo"         %%% "laminar"              % LAMINAR_VERSION,
        "io.github.cquiroz" %%% "scala-java-time"      % SCALA_JAVA_TIME_VERSION,
        "io.github.cquiroz" %%% "scala-java-time-tzdb" % SCALA_JAVA_TIME_VERSION,
        "org.scalatest"     %%% "scalatest"            % SCALA_TEST_VERSION % Test,
      ),
      Test / parallelExecution := false,
      Test / jsEnv             := seleniumJSEnv(KEEP_BROWSER_ALIVE),
      commonSettings,
    ).dependsOn(core)

def seleniumJSEnv(keepTestBrowserAlive: Boolean) =
  new SeleniumJSEnv(new ChromeOptions(), SeleniumJSEnv.Config().withKeepAlive(keepTestBrowserAlive))

lazy val web_dev_server =
  (project in file("web-dev-server"))
    .settings(
      name                     := "picazio-web-dev-server",
      description              := "Development server for PicaZIO web",
      Test / skip              := true,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio-http" % ZIO_HTTP_VERSION
      ),
      Test / parallelExecution := false,
      commonSettings,
    )

lazy val examples =
  (project in file("examples"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name                            := "examples",
      publish / skip                  := true,
      Test / skip                     := true,
      scalaJSUseMainModuleInitializer := true,
      Compile / mainClass             := Some("examples.minesweeper.Main"),
      commonSettings,
    )
    .dependsOn(
      core,
      web,
    )

lazy val chat_example_common =
  (project in file("chat-example-common"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name           := "chat-example-common",
      description    := "Common chat example things for both frontend and backend",
      publish / skip := true,
      Test / skip    := true,
      commonSettings,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"         % ZIO_VERSION,
        "dev.zio" %% "zio-streams" % ZIO_VERSION,
      ),
    )

lazy val chat_example_server =
  (project in file("chat-example-server"))
    .settings(
      name           := "chat-example-server",
      description    := "Backend only chat example things",
      publish / skip := true,
      Test / skip    := true,
      commonSettings,
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio-http" % ZIO_HTTP_VERSION
      ),
    )
    .dependsOn(
      chat_example_common
    )

lazy val chat_example_ui =
  (project in file("chat-example-ui"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name                            := "chat-example-ui",
      description                     := "Frontend only chat example things",
      publish / skip                  := true,
      Test / skip                     := true,
      scalaJSUseMainModuleInitializer := true,
      Compile / mainClass             := Some("examples.chat.ui.Main"),
      commonSettings,
    )
    .dependsOn(
      chat_example_common,
      core,
      web,
    )

lazy val wallet_example =
  (project in file("wallet-example"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name                            := "wallet-example",
      publish / skip                  := true,
      Test / skip                     := true,
      scalaJSUseMainModuleInitializer := true,
      Compile / mainClass             := Some("examples.wallet.ui.Main"),
      commonSettings,
    )
    .dependsOn(
      core,
      web,
    )

val commonSettings = Def.settings(
  scalacOptions ++= commonOptions ++ versionSpecificOptions(scalaVersion.value)
)

def commonOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:higherKinds",
  "-language:existentials",
  "-unchecked",
)

def versionSpecificOptions(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((3, _))  => scala3Options
    case Some((2, 13)) => scala2Options
    case _             => Seq.empty
  }

val scala3Options = Seq(
  "-rewrite",
  "-source:future-migration",
)

val scala2Options = Seq(
  "-Xsource:3-cross",
  "-Wconf:msg=package object inheritance is deprecated:info-summary",
)
