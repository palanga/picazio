package examples.chat.ui

import examples.chat.common.*
import picazio.*
import zio.*
import zio.stream.*

object Main extends ZIOWebApp {

  private val scope = ZLayer.succeed(Scope.global)

  override def root: Task[Shape] =
    (for {
      chatRoom       <- ZIO.service[ChatRoom]
      currentMessage <- SubscriptionRef.make("")
    } yield drawChatRoom(chatRoom, currentMessage))
//      .provide(InMemoryChatRoom.buildLayer) // backendless version, runs the chat server in the browser
      .provide(ChatRoomWebSocketClient.buildLayer("localhost", 8080)) // needs chat server running

  private def drawChatRoom(chatRoom: ChatRoom, currentMessage: SubscriptionRef[String]): Shape =
    Shape.column(
      drawMessageStream(chatRoom),
      drawMessageInput(chatRoom, currentMessage: SubscriptionRef[String]),
    )

  private def drawMessageStream(chatRoom: ChatRoom): Shape =
    Shape.column(chatRoom.readMessages.map(_.map(drawMessage)).provide(scope))

  private def drawMessage(message: String) = Shape.text(message)

  private def drawMessageInput(chatRoom: ChatRoom, currentMessage: SubscriptionRef[String]): Shape = {

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
