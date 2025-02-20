package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*

class EventualQueuedTextTest extends WebInterpreterSpec with Matchers {

  testShape("an eventual queued text") { render =>

    def EventualText(eventualText: Task[String]) =
      Shape.eventualWith(eventualText)(Shape.text)

    for {
      queue <- Queue.sliding[String](1)
      root  <- render(Shape.column(EventualText(queue.take)))
      _     <- debounce(root.head.text shouldBe "dummy")
      _     <- queue.offer("107")
      _     <- debounce(root.head.text shouldBe "107")
    } yield assert(true)

  }

}
