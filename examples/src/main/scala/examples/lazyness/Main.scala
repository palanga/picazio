package examples.lazyness

import picazio.*
import zio.*

object Main extends WebApp {
  override def root: Shape[Any] =
    Shape.eventual(
      Random.nextFloatBetween(100, 1000)
        .delay(1.seconds)
        .map(number => f"$number%.2f")
        .map(Shape.text)
    )
}
