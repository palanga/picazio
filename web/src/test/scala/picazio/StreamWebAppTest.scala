package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class StreamWebAppTest extends WebInterpreterSpec with Matchers {

  testRenderZIOSafe("a column can be dynamically built from a stream") { (render, select) =>

    object ChatRoom {
      def init: Task[ChatRoom] = Hub.sliding[String](8).map(new ChatRoom(_))
    }

    final class ChatRoom(messageHub: Hub[String]) {
      def subscribe: ZIO[Scope, Nothing, ZStream[Any, Nothing, String]] = ZStream.fromHubScoped(messageHub)
      def publish(message: String): Task[Unit]                          = messageHub.publish(message).unit
    }

    def drawChatMessages(messages: Stream[Nothing, String]) = Shape.column(messages.map(Shape.text))

    val scope = ZLayer.succeed(Scope.global)

    (for {
      chatRoom          <- ChatRoom.init
      messageStream     <- chatRoom.subscribe
      _                 <- render(drawChatMessages(messageStream))
      noMessagesYetHtml <- select.renderedHtml
      _                 <- chatRoom.publish("hola Nube")
      htmlZero          <- select.renderedHtml
      _                 <- chatRoom.publish("cita bebecita")
      htmlOne           <- select.renderedHtml
    } yield {
      noMessagesYetHtml shouldBe """<div style="display: flex; flex-direction: column; align-items: flex-start; justify-content: flex-start;"><!----></div>"""
      htmlZero shouldBe """<div style="display: flex; flex-direction: column; align-items: flex-start; justify-content: flex-start;"><!----><span style="padding-top: 4px;">hola Nube</span></div>"""
      htmlOne shouldBe """<div style="display: flex; flex-direction: column; align-items: flex-start; justify-content: flex-start;"><!----><span style="padding-top: 4px;">hola Nube</span><span style="padding-top: 4px;">cita bebecita</span></div>"""
    }).provide(scope)

  }

}
