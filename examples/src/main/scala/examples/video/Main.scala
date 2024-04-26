package examples.video

import picazio.*

object Main extends WebApp {
  override def root: Shape[Any] =
    Shape.video(
      "https://upload.wikimedia.org/wikipedia/commons/transcoded/4/4d/Wikipedia_Edit_2014.webm/Wikipedia_Edit_2014.webm.480p.vp9.webm"
    )
}
