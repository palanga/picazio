package examples.reactivity

import picazio.*
import zio.stream.*

object Main extends WebApp {
  override def root: Shape[Any] =
    Shape.eventual(
      for {
        aRef     <- SubscriptionRef.make(0L)
        bRef     <- SubscriptionRef.make(0L)
        a         = Signal.fromRef(aRef)
        b         = Signal.fromRef(bRef)
        aTimesTen = a.map(_ * 10)
        bTimesTen = b.map(_ * 10)
        sum       = aTimesTen.zipWith(bTimesTen)(_ + _)
      } yield Shape.column(
        Shape.row(
          Shape.text(s"a = "),
          Shape.textInput(a.map(_.toString)).onInput(input => aRef.set(input.toLong)),
          Shape.text(s"b = "),
          Shape.textInput(b.map(_.toString)).onInput(input => bRef.set(input.toLong)),
        ),
        Shape.row(
          Shape.text(aTimesTen.map(value => s"b = $value")),
          Shape.text(bTimesTen.map(value => s"c = $value")),
        ),
        Shape.text(sum.map(value => s"d = $value")),
      )
    )
}
