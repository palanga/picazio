package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class WebAppTest extends WebInterpreterSpec with Matchers {

  testShape("a text should render as only one span with the provided content") { render =>
    for {
      root <- render(Shape.text("hola"))
    } yield {
      root.tag shouldBe "span"
      root.text shouldBe "hola"
      root.children shouldBe empty
    }
  }

  testShape("a button should render as only one button with the provided content") { render =>
    for {
      root <- render(Shape.button("sos un botón"))
    } yield {
      root.tag shouldBe "button"
      root.text shouldBe "sos un botón"
      root.children shouldBe empty
    }
  }

  testShape("a clickable text should be rendered as only one span and can change a clicks counter state") { render =>

    object Counter {
      private var value = 0
      def increment     = ZIO.attempt(value += 1)
      def get           = {
        val result = value
        result
      }
    }

    for {
      root <- render(Shape.text("a clickable text").onClick(Counter.increment))
      _    <- root.click.repeatN(4)

    } yield {
      Counter.get shouldBe 5
      root.text shouldBe "a clickable text"
      root.children shouldBe empty
    }

  }

  testShape("a button should be rendered as only one button and can change a clicks counter state") { render =>

    object Counter {
      private var value = 0
      def increment     = ZIO.attempt(value += 1)
      def get           = {
        val result = value
        result
      }
    }

    for {
      root <- render(Shape.button("botón").onClick(Counter.increment))
      _    <- root.click.repeatN(4)

    } yield {
      Counter.get shouldBe 5
      root.text shouldBe "botón"
      root.children shouldBe empty
    }

  }

  testShape("a column with a button and a text") { render =>
    for {
      root  <- render(
                 Shape.column(
                   Shape.text("vos sos"),
                   Shape.button("un botón"),
                 )
               )
      text   = root.head
      button = root.last
    } yield {

      root.tag shouldBe "div"
      root should have size 2

      text.tag shouldBe "span"
      text.text shouldBe "vos sos"

      button.tag shouldBe "button"
      button.text shouldBe "un botón"

    }
  }

  testShape("a text can be dynamically changed based on a subscription ref being updated by clicking a button") {
    render =>

      def shape(counterRef: SubscriptionRef[Int]) =
        Shape.column(
          Shape.button("-").onClick(counterRef.update(_ - 1)),
          Shape.text(counterRef.signal.map(_.toString)),
          Shape.button("+").onClick(counterRef.update(_ + 1)),
        )

      for {
        counterRef <- SubscriptionRef.make(0)
        root       <- render(shape(counterRef))
        minusButton = root.head
        counter     = root.tail.head
        plusButton  = root.tail.tail.head
        _          <- debounce(counter.text.toInt shouldBe 0)
        _          <- plusButton.click
        _          <- debounce(counter.text.toInt shouldBe 1)
        _          <- minusButton.click.repeatN(1)
        result     <- debounce(counter.text.toInt shouldBe -1)
      } yield result

  }

}
