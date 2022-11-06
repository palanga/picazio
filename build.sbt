name := "picazio"

val MAIN_SCALA            = "3.2.0"
val ALL_SCALA             = Seq(MAIN_SCALA)
val ZIO_CASSANDRA_VERSION = "0.9.0"
val ZIO_JSON_VERSION      = "0.3.0"
val ZIO_VERSION           = "2.0.3"
val LAMINAR_VERSION       = "0.14.2"

inThisBuild(
  List(
    organization := "io.github.palanga",
    homepage     := Some(url("https://github.com/palanga/picazio")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
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
    .enablePlugins(ScalaJSPlugin)
    .settings(
      publish / skip := true
    )
    .aggregate(
      core
    )

lazy val core =
  (project in file("mira"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name           := "picazio-core",
      description    := "A web UI library made with ZIO and Laminar",
      Test / fork    := true,
      run / fork     := true,
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      libraryDependencies ++= Seq(
        "dev.zio"   %%% "zio"          % ZIO_VERSION,
        "dev.zio"    %% "zio-test"     % ZIO_VERSION % Test,
        "dev.zio"    %% "zio-test-sbt" % ZIO_VERSION % Test,
        "com.raquo" %%% "laminar"      % LAMINAR_VERSION,
      ),
      commonSettings,
    )

val commonSettings = Def.settings(
  scalaVersion       := MAIN_SCALA,
  crossScalaVersions := ALL_SCALA,
  versionScheme      := Some("strict"),
  scalacOptions ++= Seq(
    "-source:future",
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-explaintypes",
    "-feature",
    "-language:higherKinds",
    "-language:existentials",
    "-unchecked",
  ),
)
