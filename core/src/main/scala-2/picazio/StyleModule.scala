package picazio

import picazio.syntax.StyleOps

import scala.language.implicitConversions

trait StyleModule {
  implicit def styleShape(shape: Shape): StyleOps = new StyleOps(shape)
}
