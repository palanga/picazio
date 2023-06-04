package picazio.style

sealed trait Cursor

object Cursor {
  case object Default extends Cursor
  case object Pointer extends Cursor
}
