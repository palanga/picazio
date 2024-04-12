package picazio.syntax

import picazio.*
import picazio.style.*

final class StyleOps[R](val self: Shape[R]) extends AnyVal {

  def padding(size: Size): Shape[R] =
    addStyles(
      Style.PaddingTop(size),
      Style.PaddingBottom(size),
      Style.PaddingStart(size),
      Style.PaddingEnd(size),
    )

  def paddingTop(size: Size): Shape[R]         = addStyle(Style.PaddingTop(size))
  def paddingTop(size: Signal[Size]): Shape[R] = addStyle(Style.DynamicPaddingTop(size))
  def paddingBottom(size: Size): Shape[R]      = addStyle(Style.PaddingBottom(size))

  def margin(size: Size): Shape[R] =
    addStyles(
      Style.MarginTop(size),
      Style.MarginBottom(size),
      Style.MarginStart(size),
      Style.MarginEnd(size),
    )

  def marginInline(size: Size): Shape[R] = addStyles(Style.MarginStart(size), Style.MarginEnd(size))

  def centered: Shape[R]    = alignCenter
  def alignCenter: Shape[R] = addStyle(Style.SelfAlignment(Alignment.center))
  def alignStart: Shape[R]  = addStyle(Style.SelfAlignment(Alignment.start))
  def alignEnd: Shape[R]    = addStyle(Style.SelfAlignment(Alignment.end))

  def spaceBetween: Shape[R] = addStyle(Style.JustifyContent(Justification.spaceBetween))

  def fullWidth: Shape[R] = addStyle(Style.Width(100))

  def cursor(cursor: Cursor): Shape[R] = addStyle(Style.CursorStyle(cursor))

  def fontSize(size: Size): Shape[R] = addStyle(Style.FontSize(size))

  def borderRadius(size: Size): Shape[R] = addStyle(Style.BorderRadius(size))

  def color(hexString: String): Shape[R]           = addStyle(Style.ColorStyle(Color.custom(hexString)))
  def color(color: Color): Shape[R]                = addStyle(Style.ColorStyle(color))
  def backgroundColor(hexString: String): Shape[R] = addStyle(Style.BackgroundColorStyle(Color.custom(hexString)))
  def backgroundColor(color: Color): Shape[R]      = addStyle(Style.BackgroundColorStyle(color))

  def overflow(overflow: Overflow): Shape[R] = addStyle(Style.Overflowing(overflow))
  def overflowEllipsis: Shape[R]             = addStyle(Style.Overflowing(Overflow.Ellipsis))
  def overflowScroll: Shape[R]               = addStyle(Style.Overflowing(Overflow.Scroll))
  def noWrap: Shape[R]                       = addStyle(Style.Wrapping(Wrap.NoWrap))

  private def addStyle(style: Style): Shape[R] =
    self match {
      case Shape.Styled(styles, inner) => Shape.Styled(styles + style, inner)
      case _                           => Shape.Styled(StyleSheet.fromStyle(style), self)
    }

  private def addStyles(styles: Style*): Shape[R] =
    self match {
      case Shape.Styled(selfStyles, inner) => Shape.Styled(selfStyles ++ StyleSheet.fromStyles(styles*), inner)
      case _                               => Shape.Styled(StyleSheet.fromStyles(styles*), self)
    }

}
