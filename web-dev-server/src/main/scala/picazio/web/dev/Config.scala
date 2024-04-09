package picazio.web.dev

import java.net.InetAddress

case class Config(
  private[picazio] val uiProjectName: String,
  private[picazio] val uiProjectFolder: String,
  private[picazio] val rootFolder: String,
  private[picazio] val scalaVersion: String,
  private[picazio] val host: String = "localhost",
  private[picazio] val port: Int = 9000,
  private[picazio] val isDev: Boolean = true,
) {

  def withPort(port: Int): Config = copy(port = port)

  def withMinimizedJS: Config = copy(isDev = false)

  private[picazio] def targetJsFolder =
    if (isDev)
      s"$rootFolder/$uiProjectFolder/target/scala-$scalaVersion/$uiProjectFolder-fastopt"
    else
      s"$rootFolder/$uiProjectFolder/target/scala-$scalaVersion/$uiProjectFolder-opt"

}

object Config {

  val default: Config = fromUISubModuleName("ui")

  def fromUISubModuleName(uiSubModuleName: String): Config =
    Config(
      uiSubModuleName,
      uiSubModuleName.replace("_", "-"),
      new java.io.File(".").getCanonicalPath,
      getScalaVersion,
      InetAddress.getLocalHost.getHostAddress,
    )

  private def getScalaVersion =
    util.Properties.versionNumberString.split('.').toList match {
      case "2" :: minor :: _ :: Nil     => s"2.$minor"
      case "3" :: minor :: patch :: Nil => s"3.$minor.$patch"
      case other                        => throw new IllegalArgumentException(s"This scala version <<$other>> is not supported")
    }

}
