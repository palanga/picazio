package picazio

import picazio.syntax.StyleOps

import scala.language.implicitConversions

implicit def styleShape[R](shape: Shape[R]): StyleOps[R] = new StyleOps(shape)
