package picazio.syntax

import picazio.*
import picazio.style.*

final class StyleOps(val self: Shape) extends AnyVal {

  def padding(size: Size): Shape =
    addStyles(
      Style.PaddingTop(size),
      Style.PaddingBottom(size),
      Style.PaddingStart(size),
      Style.PaddingEnd(size),
    )

  def paddingTop(size: Size): Shape         = addStyle(Style.PaddingTop(size))
  def paddingTop(size: Signal[Size]): Shape = addStyle(Style.DynamicPaddingTop(size))
  def paddingBottom(size: Size): Shape      = addStyle(Style.PaddingBottom(size))

  def margin(size: Size): Shape =
    addStyles(
      Style.MarginTop(size),
      Style.MarginBottom(size),
      Style.MarginStart(size),
      Style.MarginEnd(size),
    )

  def marginInline(size: Size): Shape = addStyles(Style.MarginStart(size), Style.MarginEnd(size))

  def centered: Shape    = alignCenter
  def alignCenter: Shape = addStyle(Style.SelfAlignment(Alignment.center))
  def alignStart: Shape  = addStyle(Style.SelfAlignment(Alignment.start))
  def alignEnd: Shape    = addStyle(Style.SelfAlignment(Alignment.end))

  def spaceBetween: Shape = addStyle(Style.JustifyContent(Justification.spaceBetween))

  def fullWidth: Shape = addStyle(Style.Width(100))

  def cursor(cursor: Cursor): Shape = addStyle(Style.CursorStyle(cursor))

  def fontSize(size: Size): Shape = addStyle(Style.FontSize(size))

  def borderRadius(size: Size): Shape = addStyle(Style.BorderRadius(size))

  def color(hexString: String): Shape           = addStyle(Style.ColorStyle(Color.custom(hexString)))
  def color(color: Color): Shape                = addStyle(Style.ColorStyle(color))
  def backgroundColor(hexString: String): Shape = addStyle(Style.BackgroundColorStyle(Color.custom(hexString)))
  def backgroundColor(color: Color): Shape      = addStyle(Style.BackgroundColorStyle(color))

  def overflow(overflow: Overflow): Shape = addStyle(Style.Overflowing(overflow))
  def overflowEllipsis: Shape             = addStyle(Style.Overflowing(Overflow.Ellipsis))
  def overflowScroll: Shape               = addStyle(Style.Overflowing(Overflow.Scroll))
  def noWrap: Shape                       = addStyle(Style.Wrapping(Wrap.NoWrap))

  private def addStyle(style: Style): Shape =
    self match {
      case Shape.Styled(styles, inner) => Shape.Styled(styles + style, inner)
      case _                           => Shape.Styled(StyleSheet.fromStyle(style), self)
    }

  private def addStyles(styles: Style*): Shape =
    self match {
      case Shape.Styled(selfStyles, inner) => Shape.Styled(selfStyles ++ StyleSheet.fromStyles(styles*), inner)
      case _                               => Shape.Styled(StyleSheet.fromStyles(styles*), self)
    }

}
