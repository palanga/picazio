package picazio.style

case class Styles(self: Seq[Style]) {

  def cursor(cursor: Cursor): Styles = overrideStyle(Style.Cursor(cursor))

  def paddingTop(spacing: SpacingSelector => Spacing): Styles =
    overrideStyle(Style.PaddingTop(spacing(SpacingSelector.default)))

  def paddingBottom(spacing: SpacingSelector => Spacing): Styles =
    overrideStyle(Style.PaddingBottom(spacing(SpacingSelector.default)))

  def marginTop(spacing: SpacingSelector => Spacing): Styles =
    Styles(Style.MarginTop(spacing(SpacingSelector.default)) +: self.filterNot(_.isInstanceOf[Style.MarginTop]))

  /**
   * Combine this styles whit that styles. Right side argument (that) overrides
   * the other.
   */
  def ++(that: Styles): Styles = overrideStyles(that.self)

  private def overrideStyle(style: Style): Styles = Styles(style +: self.filterNot(_.isInstanceOf[style.type]))

  private def overrideStyles(thatStyles: Seq[Style]): Styles =
    Styles(thatStyles ++ self.filterNot(style => thatStyles.exists(_.isInstanceOf[style.type])))

}

object Styles {
  val empty: Styles = Styles(Nil)
}
