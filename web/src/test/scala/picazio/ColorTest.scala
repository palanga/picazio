package picazio

import org.scalatest.matchers.should.Matchers
import picazio.style.*
import picazio.test.*

class ColorTest extends WebInterpreterSpec with Matchers {

  testShape("a text can have a custom color") { render =>
    render(Shape.text("grey text").color("888888"))
      .map { column =>
        column.tag shouldBe "span"
        column.styles should contain allElementsOf RenderedStyleSet("color" -> "rgb(136, 136, 136)")
      }
  }

  testShape("a text can have a custom background color") { render =>
    render(Shape.text("black text on grey background").backgroundColor("888888"))
      .map { column =>
        column.tag shouldBe "span"
        column.styles should contain allElementsOf RenderedStyleSet("background-color" -> "rgb(136, 136, 136)")
      }
  }

  testShape("setting some style on a column doesn't interfere with its background color setting") { render =>
    render(Shape.column(Shape.text("item")).backgroundColor("ffffff").borderRadius(Size.medium))
      .map { column =>
        column.tag shouldBe "div"
        column.styles should contain allElementsOf RenderedStyleSet(
          "border-radius"    -> "8px",
          "background-color" -> "rgb(255, 255, 255)",
        )
      }
  }

  testShape("setting a background color on a column doesn't interfere with other style setting") { render =>
    render(Shape.column(Shape.text("item")).borderRadius(Size.medium).backgroundColor("ffffff"))
      .map { column =>
        column.tag shouldBe "div"
        column.styles should contain allElementsOf RenderedStyleSet(
          "border-radius"    -> "8px",
          "background-color" -> "rgb(255, 255, 255)",
        )
      }
  }

}
