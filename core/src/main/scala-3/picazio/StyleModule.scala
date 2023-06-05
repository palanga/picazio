package picazio

import picazio.StyleOps

import scala.language.implicitConversions

implicit def styleShape(shape: Shape): StyleOps = new StyleOps(shape)
