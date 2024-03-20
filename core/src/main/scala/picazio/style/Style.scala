package picazio.style

import picazio.Signal

sealed private[picazio] trait Style

private[picazio] object Style {

  case class MarginTop(size: Size)    extends Style
  case class MarginBottom(size: Size) extends Style
  case class MarginStart(size: Size)  extends Style
  case class MarginEnd(size: Size)    extends Style

  case class PaddingTop(size: Size)    extends Style
  case class PaddingBottom(size: Size) extends Style
  case class PaddingStart(size: Size)  extends Style
  case class PaddingEnd(size: Size)    extends Style

  case class SelfAlignment(alignment: Alignment) extends Style

  case class Width(percentage: Int) extends Style

  case class JustifyContent(justification: Justification) extends Style

  case class CursorStyle(cursor: Cursor) extends Style

  case class FontSize(size: Size) extends Style

  case class BorderTopWidth(size: Size)    extends Style
  case class BorderBottomWidth(size: Size) extends Style
  case class BorderStartWidth(size: Size)  extends Style
  case class BorderEndWidth(size: Size)    extends Style

  case class BorderTopStyle(line: Line)    extends Style
  case class BorderBottomStyle(line: Line) extends Style
  case class BorderStartStyle(line: Line)  extends Style
  case class BorderEndStyle(line: Line)    extends Style

  case class BorderRadius(size: Size) extends Style

  case class ColorStyle(color: Color)           extends Style
  case class BackgroundColorStyle(color: Color) extends Style

  case class DynamicPaddingTop(size: Signal[Size]) extends Style

  case class Outline(line: Line) extends Style

  case class Overflowing(overflow: Overflow) extends Style

  case class Wrapping(wrap: Wrap) extends Style

}
