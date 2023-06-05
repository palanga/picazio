package picazio

import picazio.style.*

final class StyleOps(val self: Shape) extends AnyVal {

  def paddingTop(spacing: Size): Shape     = addStyle(Style.PaddingTop(spacing))
  def paddingBottom(spacing: Size): Shape  = addStyle(Style.PaddingBottom(spacing))
  def cursor(cursor: CursorVariant): Shape = addStyle(Style.Cursor(cursor))

  private def addStyle(style: Style): Shape =
    self match {
      case Shape.Styled(styles, inner) => Shape.Styled(styles + style, inner)
      case _                           => Shape.Styled(Styles.fromStyle(style), self)
    }

}
