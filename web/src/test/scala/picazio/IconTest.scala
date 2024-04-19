package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*

class IconTest extends WebInterpreterSpec with Matchers {

  testShape("a single icon from material symbols renders correctly") { render =>
    for {
      root <- render(Shape.icon(Icon.heart))
    } yield {
      root.tag shouldBe "span"
      root.`class` should contain("material-symbols-outlined")
      root.text shouldBe "favorite"
    }
  }

}
