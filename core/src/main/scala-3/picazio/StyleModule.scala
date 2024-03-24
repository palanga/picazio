package picazio

import picazio.syntax.StyleOps

import scala.language.implicitConversions

implicit def styleShape(shape: Shape): StyleOps = new StyleOps(shape)
