package picazio

import org.scalajs.dom
import picazio.test.*
import zio.*

class WebAppTest extends WebInterpreterSpec {

  testRendered("hola picazio")(Shape.text("hola")) {
    assertRendered(_.selectAll("p").count(_.textContent == "hola") == 1)
  }

  testRendered("chau picazio")(Shape.text("chau")) {
    assertRendered(_.selectAll("p").count(_.textContent == "chau") == 1)
  }

  testRendered("click")(Shape.text("click").onClick(Console.printLine("clicked"))) {
    assertRendered { selector =>
      val nodes = selector.selectAll("p").filter(_.textContent == "click")

      val node = nodes.head.asInstanceOf[dom.HTMLElement]

      node.click()

      nodes.size == 1
    }
  }

  testRender("click state") { (render, selector) =>

    object Counter {
      private var value = 0
      def increment     = ZIO.attempt(value += 1)
      def get = {
        val result = value
        result
      }
    }

    render(Shape.text("click state").onClick(Counter.increment))

    val node = selector.selectAll("p").filter(_.textContent == "click state").head.asInstanceOf[dom.HTMLElement]

    (for (c <- 1 to 5) yield {
      node.click()
      Counter.get == c
    }).forall(identity)

  }

  testRender("button") { (render, selector) =>

    object Counter {
      private var value = 0

      def increment = ZIO.attempt(value += 1)

      def get = {
        val result = value
        result
      }
    }

    render(Shape.button("click state").onClick(Counter.increment))

    val node = selector.selectAll("button").filter(_.textContent == "click state").head.asInstanceOf[dom.HTMLElement]

    (for (c <- 1 to 5) yield {
      node.click()
      Counter.get == c
    }).forall(identity)

  }

}
