package picazio

import org.scalajs.dom
import picazio.test.*
import picazio.test.utils.*
import zio.*
import zio.stream.*

class WebAppTest extends WebInterpreterSpec {

  testRender("a text should render as only one span with the provided content") { (render, selector) =>
    render(Shape.text("hola"))
    val allSpans = selector.selectAll("span")

    selector.childrenCount == 1
    && allSpans.nonEmpty
    && allSpans.exists(_.textContent == "hola")
  }

  testRender("a button should render as only one button with the provided content") { (render, selector) =>
    render(Shape.button("sos un botón"))
    val allButtons = selector.selectAll("button")

    selector.childrenCount == 1
    && allButtons.nonEmpty
    && allButtons.exists(_.textContent == "sos un botón")
  }

  testRender("a clickable text should be rendered as only one span and can change a clicks counter state") {
    (render, selector) =>

      object Counter {
        private var value = 0
        def increment     = ZIO.attempt(value += 1)
        def get = {
          val result = value
          result
        }
      }

      render(Shape.text("a clickable text").onClick(Counter.increment))

      val node =
        selector.selectAll("span").filter(_.textContent == "a clickable text").head.asInstanceOf[dom.HTMLElement]

      (for (c <- 1 to 5) yield {
        node.click()
        Counter.get == c
      }).forall(identity)
      && selector.childrenCount == 1
      && selector.selectAll("span").nonEmpty

  }

  testRender("a button should be rendered as only one button and can change a clicks counter state") {
    (render, selector) =>

      object Counter {
        private var value = 0

        def increment = ZIO.attempt(value += 1)

        def get = {
          val result = value
          result
        }
      }

      render(Shape.button("botón").onClick(Counter.increment))

      val node = selector.selectAll("button").filter(_.textContent == "botón").head.asInstanceOf[dom.HTMLElement]

      (for (c <- 1 to 5) yield {
        node.click()
        Counter.get == c
      }).forall(identity)
      && selector.childrenCount == 1
      && selector.selectAll("button").nonEmpty

  }

  testRender("a column with a button and a text") { (render, selector) =>

    render(
      Shape.column(
        Shape.text("vos sos"),
        Shape.button("un botón"),
      )
    )

    val column = selector.selectAll("div").head
    val text   = column.querySelector("span")
    val button = column.querySelector("button")

    selector.childrenCount == 1
    && column.childElementCount == 2
    && text.textContent == "vos sos"
    && button.textContent == "un botón"

  }

  testRenderZIO("a text can be dynamically changed based on a subscription ref being updated by clicking a button") {
    (render, selector) =>

      def shape(counterRef: SubscriptionRef[Int]) =
        Shape.column(
          Shape.button("-").onClick(counterRef.update(_ - 1)),
          Shape.text(counterRef.signal.map(_.toString)),
          Shape.button("+").onClick(counterRef.update(_ + 1)),
        )

      for {
        counterRef       <- SubscriptionRef.make(0)
        _                <- ZIO.attempt(render(shape(counterRef)))
        minusButton      <- ZIO.attempt(selector.selectButtonWithText("-"))
        plusButton       <- ZIO.attempt(selector.selectButtonWithText("+"))
        counter0         <- ZIO.attempt(selector.selectAllSpans.head.textContent)
        _                <- clickAndWait(plusButton, counterRef)
        counter1         <- ZIO.attempt(selector.selectAllSpans.head.textContent)
        _                <- clickAndWait(minusButton, counterRef)
        _                <- clickAndWait(minusButton, counterRef)
        counterNegative1 <- ZIO.attempt(selector.selectAllSpans.head.textContent)
      } yield counter0.toInt == 0 && counter1.toInt == 1 && counterNegative1.toInt == -1

  }

}
