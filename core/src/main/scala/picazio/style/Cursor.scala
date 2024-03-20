package picazio.style

final class Cursor private (val self: String) extends AnyVal {
  override def toString: String = self
}

object Cursor {
  val default = new Cursor("default")
  val pointer = new Cursor("pointer")
}
