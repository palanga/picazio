package picazio.style

object InputTextStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.FontSize(Size.medium),
      Style.Outline(Line.none),
      Style.PaddingTop(Size.small),
      Style.PaddingBottom(Size.none),
      Style.PaddingStart(Size.none),
      Style.PaddingEnd(Size.none),
      Style.BorderBottomStyle(Line.solid),
      Style.BorderBottomWidth(Size.smallest),
      Style.BorderTopWidth(Size.none),
      Style.BorderStartWidth(Size.none),
      Style.BorderEndWidth(Size.none),
      Style.Width(100),
    )
}
