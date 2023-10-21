package picazio.style

final class Line private (val self: String) extends AnyVal {
  override def toString: String = self
}

object Line {
  val none  = new Line("none")
  val solid = new Line("solid")
}
