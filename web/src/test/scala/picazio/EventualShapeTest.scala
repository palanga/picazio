package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class EventualShapeTest extends WebInterpreterSpec with Matchers {

  testShape("an eventual signaled text") { render =>

    def SignaledNumber(eventualNumberSignal: Task[Signal[Float]]) =
      Shape.eventual(
        for {
          numberSignal <- eventualNumberSignal
        } yield Shape.text(numberSignal.map(number => f"$number%.2f"))
      )

    for {
      queue  <- Queue.sliding[Float](1)
      stream  = ZStream.fromQueue(queue)
      root   <- render(Shape.column(SignaledNumber(Signal.fromStream(stream))))
      _      <- debounce(root.tag shouldBe "div")
      _      <- debounce(root should have size 1)
      _      <- debounce(root.head.tag shouldBe "span")
      _      <- debounce(root.head.text shouldBe "loading...")
      _      <- debounce(root.head.styles should contain("display" -> "none"))
      _      <- queue.offer(107f)
      _      <- debounce
      _      <- debounce(root should have size 1) // the loading element is replaced
      _      <- debounce(root.head.text shouldBe "107.00")
      _      <- queue.offer(53f)
      _      <- debounce
      result <- debounce(root.head.text shouldBe "53.00")
    } yield result

  }

  testShape("a text of a zio of a signal is converted to an eventual signaled text") { render =>

    def SignaledNumber(eventualSignaledText: Task[Signal[String]]) = Shape.text(eventualSignaledText)

    for {
      queue  <- Queue.sliding[String](1)
      stream  = ZStream.fromQueue(queue)
      root   <- render(Shape.column(SignaledNumber(Signal.fromStream(stream))))
      _      <- debounce(root should have size 1)
      _      <- debounce(root.head.tag shouldBe "span")
      _      <- debounce(root.head.text shouldBe "loading...")
      _      <- debounce(root.head.styles should contain("display" -> "none"))
      _      <- queue.offer("que tal")
      _      <- debounce
      _      <- debounce(root should have size 1) // the loading element is replaced
      _      <- debounce(root.head.text shouldBe "que tal")
      _      <- queue.offer("todo bien")
      _      <- debounce
      result <- debounce(root.head.text shouldBe "todo bien")
    } yield result

  }

}
