package picazio.syntax

import picazio.*
import picazio.style.*

final class StyleOps[R](val self: Shape[R]) extends AnyVal {

  def padding(size: Size): Shape[R] =
    addStyles(
      Style.PaddingTop(Signal.constant(size)),
      Style.PaddingBottom(Signal.constant(size)),
      Style.PaddingStart(Signal.constant(size)),
      Style.PaddingEnd(Signal.constant(size)),
    )

  def paddingTop(size: Size): Shape[R]         = addStyle(Style.PaddingTop(Signal.constant(size)))
  def paddingTop(size: Signal[Size]): Shape[R] = addStyle(Style.PaddingTop(size))
  def paddingBottom(size: Size): Shape[R]      = addStyle(Style.PaddingBottom(Signal.constant(size)))

  def margin(size: Size): Shape[R] =
    addStyles(
      Style.MarginTop(Signal.constant(size)),
      Style.MarginBottom(Signal.constant(size)),
      Style.MarginStart(Signal.constant(size)),
      Style.MarginEnd(Signal.constant(size)),
    )

  def marginInline(size: Size): Shape[R] =
    addStyles(
      Style.MarginStart(Signal.constant(size)),
      Style.MarginEnd(Signal.constant(size)),
    )

  def centered: Shape[R]    = alignCenter
  def alignCenter: Shape[R] = addStyle(Style.SelfAlignment(Signal.constant(Alignment.center)))
  def alignStart: Shape[R]  = addStyle(Style.SelfAlignment(Signal.constant(Alignment.start)))
  def alignEnd: Shape[R]    = addStyle(Style.SelfAlignment(Signal.constant(Alignment.end)))

  def spaceBetween: Shape[R] = addStyle(Style.JustifyContent(Signal.constant(Justification.spaceBetween)))

  def height(size: Size): Shape[R] = addStyles(Style.FixHeight(Signal.constant(size)))
  def width(size: Size): Shape[R]  = addStyles(Style.FixWidth(Signal.constant(size)))

  def fullWidth: Shape[R] = addStyle(Style.Width(Signal.constant(100)))

  def cursor(cursor: Cursor): Shape[R] = addStyle(Style.CursorStyle(Signal.constant(cursor)))

  def fontSize(size: Size): Shape[R]         = addStyle(Style.FontSize(Signal.constant(size)))
  def fontSize(size: Signal[Size]): Shape[R] = addStyle(Style.FontSize(size))

  def borderRadius(size: Size): Shape[R] = addStyle(Style.BorderRadius(Signal.constant(size)))

  def color(hexString: String): Shape[R]           = addStyle(Style.ColorStyle(Signal.constant(Color.custom(hexString))))
  def color(color: Color): Shape[R]                = addStyle(Style.ColorStyle(Signal.constant(color)))
  def backgroundColor(color: Color): Shape[R]      = addStyle(Style.BackgroundColorStyle(Signal.constant(color)))
  def backgroundColor(hexString: String): Shape[R] =
    addStyle(Style.BackgroundColorStyle(Signal.constant(Color.custom(hexString))))

  def overflow(overflow: Overflow): Shape[R] = addStyle(Style.Overflowing(Signal.constant(overflow)))
  def overflowEllipsis: Shape[R]             = addStyle(Style.Overflowing(Signal.constant(Overflow.Ellipsis)))
  def overflowScroll: Shape[R]               = addStyle(Style.Overflowing(Signal.constant(Overflow.Scroll)))
  def noWrap: Shape[R]                       = addStyle(Style.Wrapping(Signal.constant(Wrap.NoWrap)))

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
