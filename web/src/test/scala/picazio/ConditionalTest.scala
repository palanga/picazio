package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.stream.SubscriptionRef

class ConditionalTest extends WebInterpreterSpec with Matchers {

  testShape("a shape can hide or show depending on a signal") { render =>
    for {
      shouldShow <- SubscriptionRef.make(false)
      container  <- render(Shape.row(Shape.text("hola").showWhen(shouldShow.signal)))
      _          <- debounce(container.head.styles should contain("display" -> "none"))
      _          <- debounce
      _          <- shouldShow.set(true)
      _          <- debounce
      res        <- debounce(container.head.text shouldBe "hola")
    } yield res
  }

}
