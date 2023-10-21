package picazio.style

final class CursorVariant private (val self: String) extends AnyVal {
  override def toString: String = self
}

object CursorVariant {
  val default = new CursorVariant("default")
  val pointer = new CursorVariant("pointer")
}
