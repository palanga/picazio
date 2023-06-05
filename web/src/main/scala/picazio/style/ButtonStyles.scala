package picazio.style

object ButtonStyles {
  val default: Styles =
    Styles.fromStyles(
      Style.PaddingTop(Size.smallMinus),
      Style.PaddingBottom(Size.smallest),
      Style.Cursor(CursorVariant.Pointer),
    )
}
