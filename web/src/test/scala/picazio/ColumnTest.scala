package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*

class ColumnTest extends WebInterpreterSpec with Matchers {

  testShape("a row and a column should stretch its items to use all available horizontal space") { render =>
    for {
      root <- render(
                Shape.column(
                  Shape.row(
                    Shape.column(Shape.text("top left"), Shape.text("bottom left")),
                    Shape.text("right"),
                  )
                )
              )
    } yield {

      val outerColumn = root
      val row         = root.head
      val leftColumn  = root.head.head

      outerColumn.tag shouldBe "div"
      row.tag shouldBe "div"
      leftColumn.tag shouldBe "div"

      outerColumn.styles should contain theSameElementsAs columnDefaultStyles
      row.styles should contain theSameElementsAs rowDefaultStyles

    }
  }

  private val columnDefaultStyles = RenderedStyleSet(
    "display"        -> "flex",
    "flex-direction" -> "column",
    "align-items"    -> "stretch",
    "flex-basis"     -> "100%",
  )

  private val rowDefaultStyles = RenderedStyleSet(
    "display"        -> "flex",
    "flex-direction" -> "row",
    "align-items"    -> "stretch",
    "flex-basis"     -> "100%",
  )

}
