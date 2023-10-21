package picazio

import picazio.style.*

final class StyleOps(val self: Shape) extends AnyVal {

  def paddingTop(size: Size): Shape         = addStyle(Style.PaddingTop(size))
  def paddingTop(size: Signal[Size]): Shape = addStyle(Style.DynamicPaddingTop(size))
  def paddingBottom(size: Size): Shape      = addStyle(Style.PaddingBottom(size))
  def cursor(cursor: CursorVariant): Shape  = addStyle(Style.Cursor(cursor))

  private def addStyle(style: Style): Shape =
    self match {
      case Shape.Styled(styles, inner) => Shape.Styled(styles + style, inner)
      case _                           => Shape.Styled(StyleSheet.fromStyle(style), self)
    }

}
