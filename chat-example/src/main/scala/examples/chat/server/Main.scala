package examples.chat.server

import examples.chat.common.InMemoryChatRoom
import zio.*
import zio.http.*

import java.net.InetAddress

object Main extends ZIOAppDefault {

  override def run =
    app.provide(
      Server.default,
      InMemoryChatRoom.buildLayer,
    )

  private val app =
    for {
      chatRoomHttp <- ChatRoomHttpApp.build
      localhost     = InetAddress.getLocalHost.getHostAddress
      _            <- Console.printLine("listening on localhost:8080 in your computer")
      _            <- Console.printLine(s"listening on $localhost:8080 in your local network")
      _            <- Server.serve(chatRoomHttp.routes.sandbox.toHttpApp)
    } yield ()

}
