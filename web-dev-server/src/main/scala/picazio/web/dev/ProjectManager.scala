package picazio.web.dev

import zio.stream.*
import zio.{ Config as _, * }

import java.io.File
import scala.language.postfixOps
import scala.sys.process.*

trait ProjectManager {
  def buildJs: Task[Unit]
  def getMainJSFile: Task[File]
  def getIndexHTML: Task[String]
  def watchMainJS: ZIO[Scope, Nothing, ZStream[Any, Nothing, String]]
}

object ProjectManager {

  def default: ZIO[Any, Throwable, ProjectManager] =
    withConfig(Config.default)

  def withConfig(config: Config): ZIO[Any, Throwable, ProjectManager] =
    FileWatcher
      .make(config.targetJsFolder)
      .map(new UsefulProjectManager(config, _))

}

final class UsefulProjectManager(config: Config, fileWatcher: FileWatcher) extends ProjectManager {

  override def getMainJSFile: Task[File] = ZIO.attempt(new File(s"${config.targetJsFolder}/main.js"))

  override def buildJs: Task[Unit] =
    if (config.isDev)
      ZIO.attempt(s"sbt ~$uiProjectName/fastLinkJS" !<)
    else
      ZIO.attempt(s"sbt ~$uiProjectName/fullLinkJS" !<)

  override def getIndexHTML: Task[String] = ZIO.succeed(indexHtmlString)

  override def watchMainJS: ZIO[Scope, Nothing, ZStream[Any, Nothing, String]] = fileWatcher.streamScoped

  private val Config(uiProjectName, _, _, _, host, port, _) = config

  private val indexHtmlString =
    s"""|<!DOCTYPE html>
        |<html>
        |<head>
        |    <meta charset="UTF-8">
        |    <title>$uiProjectName</title>
        |</head>
        |<meta name="viewport" content="width=device-width, initial-scale=1"/>
        |<body style="margin: 0">
        |<!-- Include Scala.js compiled code -->
        |<script type="text/javascript" src="http://$host:$port/js"></script>
        |<script type="text/javascript">
        |    var socket = new WebSocket("ws://$host:$port/refresh");
        |    socket.addEventListener("message", event => location.reload());
        |</script>
        |<div id="picazio-root"></div>
        |</body>
        |</html>
        |""".stripMargin

}
