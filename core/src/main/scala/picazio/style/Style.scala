package picazio.style

import picazio.Signal

sealed trait Style

object Style {

  case class MarginTop(size: Size)         extends Style
  case class PaddingTop(size: Size)        extends Style
  case class PaddingBottom(size: Size)     extends Style
  case class Cursor(cursor: CursorVariant) extends Style

  case class DynamicPaddingTop(size: Signal[Size]) extends Style

}
