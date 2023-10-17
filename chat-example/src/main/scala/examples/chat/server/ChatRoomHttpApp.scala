package examples.chat.server

import examples.chat.common.*
import zio.*
import zio.http.*

final class ChatRoomHttpApp(chatRoom: ChatRoom) {

  val app = Http.collectZIO[Request] { case Method.GET -> Root / "chatroom" / "connect" =>
    openSocketChatRoom.toResponse
  }

  private def openSocketChatRoom: SocketApp[Any] =
    Handler.webSocket { channel =>
      for {
        scope <- Scope.make.tap(_.addFinalizer(channel.shutdown))
        _     <- writeToChannel(channel, scope) `zipPar` readFromChannel(channel, scope)
      } yield ()
    }

  private def writeToChannel(channel: WebSocketChannel, scope: Scope.Closeable) =
    for {
      stream <- chatRoom.readMessages.provide(ZLayer.succeed(scope))
      _      <- stream
                  .mapZIO(text => channel.send(ChannelEvent.read(WebSocketFrame.text(text))))
                  .runDrain
      _      <- scope.close(Exit.unit)
    } yield ()

  private def readFromChannel(channel: WebSocketChannel, scope: Scope.Closeable) =
    channel.receiveAll {
      case ChannelEvent.Unregistered                    => scope.close(Exit.unit)
      case ChannelEvent.Read(WebSocketFrame.Text(text)) => chatRoom.sendMessage(text)
      case _                                            => ZIO.unit
    }

}

object ChatRoomHttpApp {
  def build: ZIO[ChatRoom, Nothing, ChatRoomHttpApp]         = ZIO.serviceWith[ChatRoom](new ChatRoomHttpApp(_))
  def buildLayer: ZLayer[ChatRoom, Nothing, ChatRoomHttpApp] = ZLayer.fromZIO(build)
}
