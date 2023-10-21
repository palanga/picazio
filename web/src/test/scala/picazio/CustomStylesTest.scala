package picazio

import org.scalatest.matchers.should.Matchers
import picazio.style.*
import picazio.test.*
import zio.stream.*

class CustomStylesTest extends WebInterpreterSpec with Matchers {

  testRenderZIOSafe("custom styles can be added to shapes") { (render, select) =>

    val shape =
      Shape
        .text("hola")
        .paddingTop(Size.large)
        .cursor(CursorVariant.pointer)

    for {
      _    <- render(shape)
      html <- select.renderedHtml
    } yield html shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 16px; cursor: pointer;">hola</span>"""

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
    } yield html shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 4px;">hola</span>"""

  }

  testRenderZIOSafe("dynamic custom styles can be added to shapes") { (render, select) =>

    def shape(padding: Signal[Size]): Shape =
      Shape
        .text("padding changing text")
        .paddingTop(padding)

    for {
      paddingRef      <- SubscriptionRef.make[Size](Size.none)
      _               <- render(shape(paddingRef.signal))
      htmlNoPadding   <- select.renderedHtml
      _               <- paddingRef.set(Size.small)
      _               <- paddingRef.changes.runHead
      htmlWithPadding <- select.renderedHtml
    } yield {
      htmlNoPadding shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 0px;">padding changing text</span>"""
      htmlWithPadding shouldBe """<span style="font-family: system-ui; font-size: 16px; padding-top: 4px;">padding changing text</span>"""
    }

  }

}
