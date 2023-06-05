package picazio.style

sealed trait CursorVariant

object CursorVariant {
  case object Default extends CursorVariant
  case object Pointer extends CursorVariant
}
