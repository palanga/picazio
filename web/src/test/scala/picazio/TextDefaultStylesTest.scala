package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.ZIO
import zio.stream.SubscriptionRef

class TextDefaultStylesTest extends WebInterpreterSpec with Matchers {

  testRenderZIOSafe("static text default style") { (render, select) =>
    for {
      _    <- render(Shape.text("hola"))
      html <- select.renderedHtml
    } yield html shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 4px;">hola</span>"""
  }

  testRenderZIOSafe("static text with on click should have the same styles but a pointer cursor.") { (render, select) =>
    for {
      _    <- render(Shape.text("hola").onClick(ZIO.unit))
      html <- select.renderedHtml
    } yield html shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 4px; cursor: pointer;">hola</span>"""
  }

  testRenderZIOSafe("dynamic text default style") { (render, select) =>
    for {
      ref  <- SubscriptionRef.make("hola")
      _    <- render(Shape.text(ref.signal))
      html <- select.renderedHtml
    } yield html shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 4px;">hola</span>"""
  }

  testRenderZIOSafe("text input default styles") { (render, select) =>
    for {
      _    <- render(Shape.textInput("hola..."))
      html <- select.renderedHtml
    } yield html shouldBe """<input placeholder="hola..." style="width: 100%; font-family: system-ui; padding: 4px 0px 0px; border-bottom-style: solid; outline: none; border-width: 0px 0px 1px; font-size: 16px;">"""
  }

  testRenderZIOSafe("button default styles") { (render, select) =>
    for {
      _    <- render(Shape.button("HOLA"))
      html <- select.renderedHtml
    } yield html shouldBe """<button style="font-family: system-ui; padding-top: 2px; border-radius: 6px; border-style: none; padding-bottom: 2px; cursor: pointer; font-size: 16px;">HOLA</button>"""
  }

  testRenderZIOSafe("text, input and button should have the same height by default") { (render, select) =>
    val elementsInARow =
      Shape.row(
        Shape.text("hola"),
        Shape.textInput("hola..."),
        Shape.button("HOLA"),
      )
    for {
      _    <- render(elementsInARow)
      html <- select.renderedHtml
    } yield html shouldBe """<div style="display: flex; flex-direction: row; align-items: flex-start; justify-content: flex-start; width: 100%;"><span style="font-family: system-ui; font-size: 16px; padding-top: 4px;">hola</span><input placeholder="hola..." style="width: 100%; font-family: system-ui; padding: 4px 0px 0px; border-bottom-style: solid; outline: none; border-width: 0px 0px 1px; font-size: 16px;"><button style="font-family: system-ui; padding-top: 2px; border-radius: 6px; border-style: none; padding-bottom: 2px; cursor: pointer; font-size: 16px;">HOLA</button></div>"""
  }

}
