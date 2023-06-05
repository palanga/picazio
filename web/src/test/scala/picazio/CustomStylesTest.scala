package picazio

import org.scalatest.matchers.should.Matchers
import picazio.style.*
import picazio.test.*

class CustomStylesTest extends WebInterpreterSpec with Matchers {

  testRenderZIOSafe("custom styles can be added to shapes") { (render, select) =>

    val shape =
      Shape
        .text("hola")
        .paddingTop(Size.large)
        .cursor(CursorVariant.Pointer)

    for {
      _    <- render(shape)
      html <- select.renderedHtml
    } yield html shouldBe """<span style="padding-top: 16px; cursor: pointer;">hola</span>"""

  }

  testRenderZIOSafe("the last added style overrides the previous ones") { (render, select) =>

    val shape =
      Shape
        .text("hola")
        .paddingTop(Size.large)
        .paddingTop(Size.small)

    for {
      _    <- render(shape)
      html <- select.renderedHtml
    } yield html shouldBe """<span style="padding-top: 4px;">hola</span>"""

  }

}
