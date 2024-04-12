package examples.chat.ui

import examples.chat.common.*
import picazio.*
import zio.*
import zio.stream.*

object Main extends ZIOWebApp {

  private val scope = ZLayer.succeed(Scope.global)

  override def root: Task[Shape[Any]] =
    (for {
      chatRoom       <- ZIO.service[ChatRoom]
      currentMessage <- SubscriptionRef.make("")
    } yield drawChatRoom(chatRoom, currentMessage))
      .provide(InMemoryChatRoom.buildLayer) // backendless version, runs the chat server within the browser
//      .provide(ChatRoomWebSocketClient.buildLayer(java.net.InetAddress.getLocalHost.getHostAddress, 8080))

  private def drawChatRoom(chatRoom: ChatRoom, currentMessage: SubscriptionRef[String]) =
    Shape.column(
      MessageStream(chatRoom),
      MessageInput(chatRoom, currentMessage: SubscriptionRef[String]),
    )

  private def MessageStream(chatRoom: ChatRoom) =
    Shape.column(chatRoom.readMessages.map(_.map(Message)).provide(scope))

  private def Message(message: String) = Shape.text(message)

  private def MessageInput(chatRoom: ChatRoom, currentMessage: SubscriptionRef[String]) = {

    val sendCurrentMessageAndEraseInput: ZIO[Any, Nothing, Unit] =
      currentMessage.get.flatMap(message => chatRoom.sendMessage(message).when(message.nonEmpty)) *>
        currentMessage.set("")

    Shape.row(
      Shape
        .textInput("write a message...", currentMessage)
        .onKeyPressed(key => sendCurrentMessageAndEraseInput.when(key == 13).unit)
        .focused,
      Shape.button("SEND").onClick(sendCurrentMessageAndEraseInput),
    )

  }

}
