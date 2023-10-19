package picazio.style

object InputTextStyles {
  val default: Styles =
    Styles.fromStyles(
      Style.FontSize(Size.small),
      Style.PaddingTop(Size.small),
      Style.PaddingBottom(Size.none),
      Style.PaddingStart(Size.none),
      Style.PaddingEnd(Size.none),
      Style.BorderBottomStyle(Line.Solid),
      Style.BorderBottomWidth(Size.smallest),
      Style.BorderTopWidth(Size.none),
      Style.BorderStartWidth(Size.none),
      Style.BorderEndWidth(Size.none),
    )
}
