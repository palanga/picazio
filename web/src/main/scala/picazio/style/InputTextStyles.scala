package picazio.style

import picazio.Signal

object InputTextStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.FontSize(Signal.constant(Size.medium)),
      Style.Outline(Signal.constant(Line.none)),
      Style.PaddingTop(Signal.constant(Size.small)),
      Style.PaddingBottom(Signal.constant(Size.none)),
      Style.PaddingStart(Signal.constant(Size.none)),
      Style.PaddingEnd(Signal.constant(Size.none)),
      Style.BorderBottomStyle(Signal.constant(Line.solid)),
      Style.BorderBottomWidth(Signal.constant(Size.smallest)),
      Style.BorderTopWidth(Signal.constant(Size.none)),
      Style.BorderStartWidth(Signal.constant(Size.none)),
      Style.BorderEndWidth(Signal.constant(Size.none)),
      Style.Width(Signal.constant(100)),
    )
}
