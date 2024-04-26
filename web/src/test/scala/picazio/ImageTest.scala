package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*

class ImageTest extends WebInterpreterSpec with Matchers {

  testShape("a simple image") { render =>
    render(Shape.image("https://upload.wikimedia.org/wikipedia/en/7/74/PicassoGuernica.jpg"))
      .map { image =>
        image.tag shouldBe "img"
        image.src shouldBe "https://upload.wikimedia.org/wikipedia/en/7/74/PicassoGuernica.jpg"
      }
  }

}
