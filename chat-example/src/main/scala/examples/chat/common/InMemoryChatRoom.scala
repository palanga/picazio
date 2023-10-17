package examples.chat.common

import zio.*
import zio.stream.*

object InMemoryChatRoom {
  def build: IO[Nothing, ChatRoom]         = Hub.sliding[String](8).map(new InMemoryChatRoom(_))
  def buildLayer: Layer[Nothing, ChatRoom] = ZLayer.fromZIO(build)
}

final class InMemoryChatRoom(val messageHub: Hub[String]) extends ChatRoom {

  override def sendMessage(message: String): ZIO[Any, Nothing, Unit] = messageHub.publish(message).unit

  override def readMessages: ZIO[Scope, Nothing, ZStream[Any, Nothing, String]] =
    Console.printLine("new subscriber").ignore *>
      ZStream.fromHubScoped(messageHub).withFinalizer(_ => Console.printLine("subscriber disconnected").ignore)

}
