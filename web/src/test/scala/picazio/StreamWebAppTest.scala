package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class StreamWebAppTest extends WebInterpreterSpec with Matchers {

  testShape("a column can be dynamically built from a stream") { render =>

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
      chatRoom      <- ChatRoom.init
      messageStream <- chatRoom.subscribe
      root          <- render(drawChatMessages(messageStream))
      _             <- debounce(root.tag shouldBe "div")
      _             <- debounce(root shouldBe empty)
      _             <- chatRoom.publish("hola Nube")
      _             <- debounce(root should have size 1)
      _             <- debounce(root.head.tag shouldBe "span")
      _             <- debounce(root.head.text shouldBe "hola Nube")
      _             <- chatRoom.publish("cita bebecita")
      _             <- debounce(root should have size 2)
      _             <- debounce(root.tail.head.tag shouldBe "span")
      result        <- debounce(root.tail.head.text shouldBe "cita bebecita")
    } yield result).provide(scope)

  }

  testShape("an eventual stream column") { render =>

    def EventualStreamColumn(eventualMessageStream: Task[Stream[Throwable, String]]) =
      Shape.column(eventualMessageStream.map(_.map(Shape.text)))

    for {
      queue         <- Queue.sliding[String](2)
      stream         = ZStream.fromQueue(queue)
      finishLoading <- SubscriptionRef.make(false)
      eventualStream = ZIO.suspendSucceed(finishLoading.changes.takeUntil(identity).runDrain.as(stream))
      root          <- render(Shape.column(EventualStreamColumn(eventualStream)))
      _             <- debounce {
                         root should have size 1
                         root should have size 1
                         root.head.tag shouldBe "span"
                         root.head.text shouldBe "loading..."
                         root.head.styles should contain("display" -> "none")
                       }
      _             <- finishLoading.set(true)
      _             <- debounce
      _             <- queue.offer("first")
      _             <- debounce
      _             <- debounce
      _             <- debounce {
                         root should have size 1
                         root.head.tag shouldBe "div"
                         root.head should have size 1
                         root.head.head.text shouldBe "first"
                       }
      _             <- queue.offer("second")
      result        <- debounce(root.head.tail.head.text shouldBe "second")
    } yield result

  }

}
