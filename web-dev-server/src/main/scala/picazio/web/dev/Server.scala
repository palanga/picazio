package picazio.web.dev

import zio.http.Header.AccessControlAllowOrigin
import zio.http.Middleware.CorsConfig
import zio.http.template.Html
import zio.http.{ Server as ZIOServer, * }
import zio.stream.ZStream
import zio.{ Config as _, * }

object Server {

  def start: Task[Unit] =
    start(Config.default)

  def start(uiSubModuleName: String = "ui", port: Int = 9000): Task[Unit] =
    start(Config.fromUISubModuleName(uiSubModuleName).withPort(port))

  def startWithMinimizedJS: Task[Unit] =
    start(Config.default.withMinimizedJS)

  def startWithMinimizedJS(uiSubModuleName: String = "ui", port: Int = 9000): Task[Unit] =
    start(Config.fromUISubModuleName(uiSubModuleName).withPort(port).withMinimizedJS)

  def start(config: Config) = {

    val serverConfig =
      ZIOServer.Config.default
        .port(config.port)
        .copy(responseCompression = Some(ZIOServer.Config.ResponseCompressionConfig.default))

    for {
      _              <- SplashScreen.print
      projectManager <- ProjectManager.withConfig(config)
      _              <- projectManager.buildJs.fork
      _              <- printInfo(config.host, config.port)
      _              <- ZIOServer.serve(routes.sandbox.toHttpApp @@ Middleware.cors(corsConfig)).provide(
                          ZLayer.succeed(projectManager),
                          ZIOServer.live,
                          ZLayer.succeed(serverConfig),
                        )
    } yield ()

  }

  private def routes: Routes[ProjectManager, Throwable] =
    Routes(
      // index html
      Method.GET / "" -> handler(
        ZIO.serviceWithZIO[ProjectManager](_.getIndexHTML)
          .map(Html.raw)
          .map(Response.html(_))
      ),

      // main js file
      Method.GET / "main.js" -> Handler.fromFileZIO(
        ZIO.serviceWithZIO[ProjectManager](_.getMainJSFile)
      ),

      // main js map file
      Method.GET / "main.js.map" -> Handler.fromFileZIO(
        ZIO.serviceWithZIO[ProjectManager](_.getMainJSMapFile)
      ),

      // watch main js updates to trigger a refresh
      Method.GET / "refresh" -> handler(
        Handler.webSocket { channel =>
          for {
            scope   <- Scope.make.tap(_.addFinalizer(channel.shutdown))
            project <- ZIO.service[ProjectManager]
            stream  <- project.watchMainJS.provide(ZLayer.succeed(scope))
            _       <- write(channel, stream, scope) `zipPar` read(channel, scope)
          } yield ()
        }.toResponse
      ),

      // health check
      Method.GET / "health" -> handler(Response.ok),
    )

  private def write(channel: WebSocketChannel, stream: ZStream[Any, Throwable, String], scope: Scope.Closeable) =
    stream
      .mapZIO(text => channel.send(ChannelEvent.read(WebSocketFrame.text(text))))
      .take(1)
      .ensuring(scope.close(Exit.unit))
      .runDrain

  private def read(channel: WebSocketChannel, scope: Scope.Closeable) =
    channel.receiveAll {
      case ChannelEvent.Unregistered => scope.close(Exit.unit)
      case _                         => ZIO.unit
    }

  private def printInfo(localhost: String, port: Int): Task[Unit] =
    for {
      _ <- Console.printLine(s"Starting dev server on http://localhost:$port in your computer")
      _ <- Console.printLine(s"Starting dev server on http://$localhost:$port in your local network")
    } yield ()

  private val corsConfig = CorsConfig(allowedOrigin = _ => Some(AccessControlAllowOrigin.All))

}
