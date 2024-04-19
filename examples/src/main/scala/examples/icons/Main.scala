package examples.icons

import picazio.*

object Main extends WebApp {
  override def root: Shape[Any] =
    Shape.column(
      Shape.row(Shape.icon(Icon.heart), Shape.icon(Icon.flag)),
      Shape.row(Shape.icon(Icon.mine), Shape.icon(Icon.question)),
    )
}
