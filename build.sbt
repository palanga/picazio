name := "picazio"

val SCALA_2                 = "2.13.10"
val SCALA_3                 = "3.3.0"
val MAIN_SCALA              = SCALA_3
val ALL_SCALA               = Seq(SCALA_3, SCALA_2)
val ZIO_VERSION             = "2.0.14"
val LAMINAR_VERSION         = "15.0.0"
val SCALA_JAVA_TIME_VERSION = "2.5.0"
val SCALA_TEST_VERSION      = "3.2.16"

import org.scalajs.jsenv.selenium.SeleniumJSEnv
import org.openqa.selenium.chrome.ChromeOptions

inThisBuild(
  List(
    scalaVersion       := MAIN_SCALA,
    crossScalaVersions := ALL_SCALA,
    organization       := "io.github.palanga",
    homepage           := Some(url("https://github.com/palanga/picazio")),
    licenses           := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    resolvers += "Sonatype OSS Releases" at "https://s01.oss.sonatype.org/content/repositories/releases",
    resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots",
    developers := List(
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

lazy val root =
  (project in file("."))
    .settings(
      publish / skip := true
    )
    .aggregate(
      core,
      web,
      examples,
    )

lazy val core =
  (project in file("core"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name        := "picazio-core",
      description := "A web UI library made with ZIO and Laminar",
      libraryDependencies ++= Seq(
        "dev.zio" %%% "zio"         % ZIO_VERSION,
        "dev.zio" %%% "zio-streams" % ZIO_VERSION,
      ),
      commonSettings,
    )

lazy val web =
  (project in file("web"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name        := "picazio-web",
      description := "Web interpreter for PicaZIO made with Laminar",
      libraryDependencies ++= Seq(
        "com.raquo"         %%% "laminar"              % LAMINAR_VERSION,
        "io.github.cquiroz" %%% "scala-java-time"      % SCALA_JAVA_TIME_VERSION,
        "io.github.cquiroz" %%% "scala-java-time-tzdb" % SCALA_JAVA_TIME_VERSION,
        "org.scalatest"     %%% "scalatest"            % SCALA_TEST_VERSION % Test,
      ),
      Test / parallelExecution := false,
      Test / jsEnv             := seleniumJSEnv(keepTestBrowserAlive = false),
      commonSettings,
    ).dependsOn(core)

def seleniumJSEnv(keepTestBrowserAlive: Boolean) =
  new SeleniumJSEnv(new ChromeOptions(), SeleniumJSEnv.Config().withKeepAlive(keepTestBrowserAlive))

lazy val examples =
  (project in file("examples"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name                            := "examples",
      publish / skip                  := true,
      scalaJSUseMainModuleInitializer := true,
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
  "-Wconf:msg=package object inheritance is deprecated:info-summary",
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
  "-Xsource:3"
)
