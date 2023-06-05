package picazio

import org.scalatest.matchers.should.Matchers
import picazio.style.Size
import picazio.test.*

class CustomStylesTest extends WebInterpreterSpec with Matchers {

  testRenderZIOSafe("TODO") { (render, select) =>
    for {
      _    <- render(Shape.text("hola").paddingTop(Size.large))
      html <- select.renderedHtml
    } yield html shouldBe """<span style="padding-top: 16px;">hola</span>"""
  }

}
