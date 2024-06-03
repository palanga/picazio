package picazio.style

import picazio.Signal

sealed private[picazio] trait Style

private[picazio] object Style {

  case class MarginTop(size: Signal[Size])    extends Style
  case class MarginBottom(size: Signal[Size]) extends Style
  case class MarginStart(size: Signal[Size])  extends Style
  case class MarginEnd(size: Signal[Size])    extends Style

  case class PaddingTop(size: Signal[Size])    extends Style
  case class PaddingBottom(size: Signal[Size]) extends Style
  case class PaddingStart(size: Signal[Size])  extends Style
  case class PaddingEnd(size: Signal[Size])    extends Style

  case class SelfAlignment(alignment: Signal[Alignment]) extends Style

  case class Width(percentage: Signal[Int]) extends Style

  case class FixHeight(size: Signal[Size]) extends Style
  case class FixWidth(size: Signal[Size])  extends Style

  case class JustifyContent(justification: Signal[Justification]) extends Style

  case class CursorStyle(cursor: Signal[Cursor]) extends Style

  case class FontSize(size: Signal[Size]) extends Style

  case class BorderTopWidth(size: Signal[Size])    extends Style
  case class BorderBottomWidth(size: Signal[Size]) extends Style
  case class BorderStartWidth(size: Signal[Size])  extends Style
  case class BorderEndWidth(size: Signal[Size])    extends Style

  case class BorderTopStyle(line: Signal[Line])    extends Style
  case class BorderBottomStyle(line: Signal[Line]) extends Style
  case class BorderStartStyle(line: Signal[Line])  extends Style
  case class BorderEndStyle(line: Signal[Line])    extends Style

  case class BorderRadius(size: Signal[Size]) extends Style

  case class ColorStyle(color: Signal[Color])           extends Style
  case class BackgroundColorStyle(color: Signal[Color]) extends Style

  case class Outline(line: Signal[Line]) extends Style

  case class Overflowing(overflow: Signal[Overflow]) extends Style

  case class Wrapping(wrap: Signal[Wrap]) extends Style

}
