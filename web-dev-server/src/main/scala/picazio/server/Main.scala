package picazio.server

import zio.*

import scala.sys.process.*

object Main extends ZIOAppDefault {

  override def run = program

  private val program = for {
    _ <- SplashScreen.print
    _ <- buildUI("chat_example") zipPar startDevServer
  } yield ()

  private def buildUI(uiSubModuleName: String): Task[Unit] = ZIO.attempt {
    s"sbt ~$uiSubModuleName/fastLinkJS".run(ProcessLogger(line => println(line), error => println("error: " + error)))//lazyLines.foreach(println(_))
  }

  private def startDevServer: Task[Unit] = ZIO.unit

}

object test {

  val sbtCommand = "sbt chat_example/fastLinkJS"

  sbtCommand.lazyLines.foreach(println(_))

  val process = sbtCommand.run(ProcessLogger(line => println(line), error => println("error: " + error)))

  //  // Esperar a que el comando termine (opcional)
  //  val exitCode = process.exitValue()
  //
  //  // Imprimir el código de salida (0 si el comando se ejecutó correctamente)
  //  println(s"Código de salida: $exitCode")

}

//object Main extends ZIOAppDefault {
//
//  override def run =
//    app.provide(
//      Server.default,
//      WebDevServer.buildLayer,
//    )
//
//  private val app =
//    for {
//      webDevServer <- ChatRoomHttpApp.build
//      localhost     = InetAddress.getLocalHost.getHostAddress
//      _            <- Console.printLine("listening on localhost:8080 in your computer")
//      _            <- Console.printLine(s"listening on $localhost:8080 in your local network")
//      _            <- Server.serve(webDevServer.routes.sandbox.toHttpApp)
//    } yield ()
//
//}
