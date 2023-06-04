package picazio.style

object ButtonStyles {
  val default: Styles =
    Styles.empty
      .paddingTop(_.smallMinus)
      .paddingBottom(_.smallest)
      .cursor(Cursor.Pointer)
}
