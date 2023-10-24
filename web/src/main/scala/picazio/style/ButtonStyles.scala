package picazio.style

object ButtonStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.FontSize(Size.medium),
      Style.PaddingTop(Size.smallestPlus),
      Style.PaddingBottom(Size.smallestPlus),
      Style.Cursor(CursorVariant.pointer),
      Style.BorderRadius(Size.mediumSmall),
      Style.BorderTopStyle(Line.none),
      Style.BorderBottomStyle(Line.none),
      Style.BorderStartStyle(Line.none),
      Style.BorderEndStyle(Line.none),
    )
}
