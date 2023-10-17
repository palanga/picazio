package examples.chat.ui

import examples.chat.common.*
import org.scalajs.dom.*
import zio.*
import zio.stream.*

object ChatRoomWebSocketClient {

  def build(host: String, port: Int): ZIO[Any, ChatRoomInitializationException, ChatRoom] =
    openWebSocket(host, port).map(new ChatRoomWebSocketClient(_)) <*
      Console.printLine("Web socket connection to chat room server established").ignore

  def buildLayer(host: String, port: Int): ZLayer[Any, ChatRoomInitializationException, ChatRoom] =
    ZLayer.fromZIO(build(host, port))

  private def openWebSocket(host: String, port: Int): ZIO[Any, ChatRoomInitializationException, WebSocket] =
    ZIO.async { callback =>
      val webSocket = new WebSocket(s"""ws://$host:$port/chatroom/connect""")

      webSocket.onopen = _ => callback(ZIO.succeed(webSocket))
      webSocket.onerror = event => callback(ZIO.fail(ChatRoomInitializationException.fromEvent(event)))

      ()
    }

}

final class ChatRoomWebSocketClient(webSocket: WebSocket) extends ChatRoom {

  override def sendMessage(message: String): ZIO[Any, Nothing, Unit] =
    ZIO.attempt(webSocket.send(message)).logError.ignore

  override def readMessages: ZIO[Any, Nothing, ZStream[Any, Nothing, String]] =
    ZIO.succeed(
      ZStream.async[Any, Nothing, String] { callback =>
        webSocket.onmessage = messageEvent => callback(ZIO.succeed(Chunk(messageEvent.data.toString)))
        ()
      }
    )

}

final class ChatRoomInitializationException private (message: String) extends Exception(message)

object ChatRoomInitializationException {
  private[ui] def fromEvent(event: org.scalajs.dom.Event): ChatRoomInitializationException =
    new ChatRoomInitializationException(
      s"Web socket connection to chat room server couldn't be established: ${event.`type`}"
    )
}
