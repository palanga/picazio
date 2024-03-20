package picazio

import org.scalatest.matchers.should.Matchers
import picazio.style.*
import picazio.test.*

class ColumnTest extends WebInterpreterSpec with Matchers {

  testShape("row and column default styles") { render =>
    for {
      root <- render(
                Shape.column(
                  Shape.row(
                    Shape.column(Shape.text("top left"), Shape.text("bottom left")),
                    Shape.text("right").marginInline(Size.largest),
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

  testShape("a row with space between items") { render =>
    render(
      Shape.column(
        Shape.row(Shape.text("left"), Shape.text("center left"), Shape.text("center right"), Shape.text("right"))
          .spaceBetween,
        Shape.column(
          Shape.text("first"),
          Shape.text("second"),
          Shape.text("third"),
          Shape.text("fourth"),
        ),
      ).fullWidth
    ).map { root =>

      root.tag shouldBe "div"
      root.styles should contain theSameElementsAs (columnDefaultStyles + ("width" -> "100%"))

      root.head.styles should contain theSameElementsAs (rowDefaultStyles + ("justify-content" -> "space-between"))

    }
  }

  private val columnDefaultStyles = RenderedStyleSet(
    "display"        -> "flex",
    "flex-direction" -> "column",
    "overflow"       -> "hidden",
  )

  private val rowDefaultStyles = RenderedStyleSet(
    "display"        -> "flex",
    "flex-direction" -> "row",
    "overflow"       -> "hidden",
  )

}
