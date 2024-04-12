package picazio

import org.scalatest.matchers.should.Matchers
import picazio.style.*
import picazio.test.*
import zio.stream.*

class CustomStylesTest extends WebInterpreterSpec with Matchers {

  testShape("custom styles can be added to shapes") { render =>
    render(
      Shape
        .text("hola")
        .paddingTop(Size.large)
        .cursor(Cursor.pointer)
    )
      .map(_.styles should contain allElementsOf RenderedStyleSet("padding-top" -> "16px", "cursor" -> "pointer"))

  }

  testShape("the last added style overrides the previous ones") { render =>
    render(
      Shape
        .text("hola")
        .paddingTop(Size.large)
        .paddingTop(Size.small)
    )
      .map(_.styles should contain allElementsOf RenderedStyleSet("padding-top" -> "4px"))

  }

  testShape("dynamic custom styles can be added to shapes") { render =>

    def shape(padding: Signal[Size]) =
      Shape
        .text("padding changing text")
        .paddingTop(padding)

    for {
      paddingRef <- SubscriptionRef.make[Size](Size.none)
      root       <- render(shape(paddingRef.signal))
      _          <- debounce(root.styles should contain("padding-top" -> "0px"))
      _          <- paddingRef.set(Size.small)
      result     <- debounce(root.styles should contain("padding-top" -> "4px"))
    } yield result

  }

}
