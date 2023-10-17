package examples.chat.ui

import examples.chat.common.*
import picazio.*
import zio.*
import zio.stream.*

object Main extends ZIOWebApp {

  val scope = ZLayer.succeed(Scope.global)

  override def root =
    (for {
      chatRoom       <- ZIO.service[ChatRoom]
      currentMessage <- SubscriptionRef.make("")
    } yield drawChatRoom(chatRoom, currentMessage))
      .provide(ChatRoomWebSocketClient.buildLayer("localhost", 8080))
//      .provide(InMemoryChatRoom.buildLayer) // TODO: hangs

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
      currentMessage.get.flatMap(chatRoom.sendMessage) *> currentMessage.set("")

    Shape.row(
      Shape.textInput("write a message...", currentMessage)
        .onKeyPressed(key => sendCurrentMessageAndEraseInput.when(key == 13).unit),
      Shape.button("SEND").onClick(sendCurrentMessageAndEraseInput),
    )

  }

}
