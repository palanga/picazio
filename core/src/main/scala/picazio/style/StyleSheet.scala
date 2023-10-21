package picazio.style

final class StyleSheet private (private val stylesByType: Map[Class[? <: Style], Style]) extends AnyVal {

  /**
   * Add a new Style to this StyleSheet. If this StyleSheet already contains a
   * Style definition of the same type the new value overrides the old one.
   */
  private[picazio] def +(style: Style): StyleSheet = new StyleSheet(this.stylesByType + (style.getClass -> style))

  /**
   * Return a new StyleSheet containing the styles from the left hand operand
   * followed by the styles from the right hand operand. The right hand operand
   * overrides the left hand operand.
   */
  private[picazio] def ++(that: StyleSheet): StyleSheet = new StyleSheet(this.stylesByType ++ that.stylesByType)

  private[picazio] def values: Seq[Style] = this.stylesByType.values.toSeq

}

private[picazio] object StyleSheet {
  val empty: StyleSheet                      = new StyleSheet(Map.empty)
  def fromStyle(style: Style): StyleSheet    = empty + style
  def fromStyles(styles: Style*): StyleSheet = styles.foldLeft(empty)(_ + _)
}
