package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.ZIO
import zio.stream.SubscriptionRef

class TextDefaultStylesTest extends WebInterpreterSpec with Matchers {

  testShape("static text default style") { render =>
    render(Shape.text("hola")).map(_.styles should contain theSameElementsAs textDefaultStyles)
  }

  testShape("static text with on click should have the same styles but a pointer cursor.") { render =>
    render(Shape.text("hola").onClick(ZIO.unit))
      .map(_.styles should contain theSameElementsAs textDefaultStyles + ("cursor" -> "pointer"))
  }

  testShape("dynamic text default style") { render =>
    for {
      ref  <- SubscriptionRef.make("hola")
      root <- render(Shape.text(ref.signal))
    } yield root.styles should contain theSameElementsAs textDefaultStyles
  }

  testShape("text input default styles") { render =>
    render(Shape.textInput("hola...")).map(_.styles should contain theSameElementsAs textInputDefaultStyles)
  }

  testShape("button default styles") { render =>
    render(Shape.button("HOLA")).map(_.styles should contain theSameElementsAs buttonDefaultStyles)
  }

  testShape("text, input and button should have the same height by default") { render =>

    val elementsInARow =
      Shape.row(
        Shape.text("hola"),
        Shape.textInput("hola..."),
        Shape.button("HOLA"),
      )

    render(elementsInARow)
      .map { root =>

        val span   = root.head
        val input  = root.tail.head
        val button = root.tail.tail.head

        span.styles should contain allElementsOf RenderedStyleSet(
          "font-family" -> "system-ui",
          "font-size"   -> "16px",
          "padding-top" -> "4px",
        )

        input.styles should contain allElementsOf RenderedStyleSet(
          "border-bottom-style" -> "solid",
          "font-family"         -> "system-ui",
          "border-width"        -> "0px 0px 1px",
          "font-size"           -> "16px",
          "padding"             -> "4px 0px 0px",
          "outline"             -> "none",
        )

        button.styles should contain allElementsOf RenderedStyleSet(
          "font-family"    -> "system-ui",
          "padding-bottom" -> "2px",
          "border-style"   -> "none",
          "padding-top"    -> "2px",
          "font-size"      -> "16px",
        )

      }

  }

  private val textDefaultStyles = RenderedStyleSet(
    "font-family" -> "system-ui",
    "font-size"   -> "16px",
    "padding-top" -> "4px",
  )

  private val textInputDefaultStyles = RenderedStyleSet(
    "width"               -> "100%",
    "padding"             -> "4px 0px 0px",
    "outline"             -> "none",
    "font-family"         -> "system-ui",
    "border-width"        -> "0px 0px 1px",
    "font-size"           -> "16px",
    "border-bottom-style" -> "solid",
  )

  private val buttonDefaultStyles = RenderedStyleSet(
    "width"          -> "fit-content",
    "cursor"         -> "pointer",
    "font-family"    -> "system-ui",
    "padding-bottom" -> "2px",
    "border-style"   -> "none",
    "padding-top"    -> "2px",
    "font-size"      -> "16px",
    "border-radius"  -> "6px",
  )

}
