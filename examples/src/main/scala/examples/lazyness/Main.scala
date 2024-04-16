package examples.lazyness

import picazio.*
import zio.*
import zio.stream.*

object Main extends WebApp {
  override def root: Shape[Any] =
    Shape.eventual(
      ZStream.repeatZIO(Random.nextFloatBetween(0, 100).delay(1.second))
        .map(_.toString)
        .makeSignal
        .map(Shape.text)
    ).onLoading(Shape.text("Please wait..."))
}
