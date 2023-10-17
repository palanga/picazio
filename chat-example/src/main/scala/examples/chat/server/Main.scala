package examples.chat.server

import examples.chat.common.InMemoryChatRoom
import zio.*
import zio.http.*

object Main extends ZIOAppDefault {

  override def run =
    app.provide(
      Server.default,
      InMemoryChatRoom.buildLayer,
    )

  private val app =
    for {
      chatRoomHttp <- ChatRoomHttpApp.build
      _            <- Console.printLine("listening on port 8080")
      _            <- Server.serve(chatRoomHttp.app)
    } yield ()

}
