package picazio

import picazio.syntax.StyleOps

import scala.language.implicitConversions

trait StyleModule {
  implicit def styleShape[R](shape: Shape[R]): StyleOps[R] = new StyleOps(shape)
}
