package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*

class VideoTest extends WebInterpreterSpec with Matchers {

  testShape("a simple video") { render =>
    render(
      Shape.video(
        "https://upload.wikimedia.org/wikipedia/commons/transcoded/4/4d/Wikipedia_Edit_2014.webm/Wikipedia_Edit_2014.webm.480p.vp9.webm"
      )
    )
      .map { video =>
        video.tag shouldBe "video"
        video.src shouldBe "https://upload.wikimedia.org/wikipedia/commons/transcoded/4/4d/Wikipedia_Edit_2014.webm/Wikipedia_Edit_2014.webm.480p.vp9.webm"
        video.controls shouldBe "true"
        video.preload shouldBe "auto"
      }
  }

}
