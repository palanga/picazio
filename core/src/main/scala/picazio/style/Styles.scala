package picazio.style

final class Styles private (private val stylesMap: Map[Class[? <: Style], Style]) extends AnyVal {

  /**
   * Return a new Styles containing the styles from the left hand operand
   * followed by the styles from the right hand operand. The right hand operand
   * overrides the left hand operand.
   */
  private[picazio] def ++(that: Styles): Styles = new Styles(this.stylesMap ++ that.stylesMap)

  private[picazio] def values: Seq[Style] = this.stylesMap.values.toSeq

  /**
   * Add a new Style to this Styles. If this Styles already contains a Style
   * definition of the same type the new value overrides the old one.
   */
  private[picazio] def +(style: Style): Styles = new Styles(this.stylesMap + (style.getClass -> style))

}

private[picazio] object Styles {
  def fromStyle(style: Style): Styles    = empty + style
  def fromStyles(styles: Style*): Styles = styles.foldLeft(empty)(_ + _)
  val empty: Styles                      = new Styles(Map.empty)
}
