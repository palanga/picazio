package picazio.style

import picazio.Signal

sealed trait Style

object Style {

  case class MarginTop(size: Size) extends Style

  case class PaddingTop(size: Size)    extends Style
  case class PaddingBottom(size: Size) extends Style
  case class PaddingStart(size: Size)  extends Style
  case class PaddingEnd(size: Size)    extends Style

  case class Cursor(cursor: CursorVariant) extends Style

  case class FontSize(size: Size) extends Style

  case class BorderTopWidth(size: Size)    extends Style
  case class BorderBottomWidth(size: Size) extends Style
  case class BorderStartWidth(size: Size)  extends Style
  case class BorderEndWidth(size: Size)    extends Style

  case class BorderTopStyle(line: Line)    extends Style
  case class BorderBottomStyle(line: Line) extends Style
  case class BorderStartStyle(line: Line)  extends Style
  case class BorderEndStyle(line: Line)    extends Style

  case class DynamicPaddingTop(size: Signal[Size]) extends Style

}
