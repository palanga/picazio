package picazio.style

import picazio.Signal

object ButtonStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.FontSize(Signal.constant(Size.medium)),
      Style.PaddingTop(Signal.constant(Size.smallestPlus)),
      Style.PaddingBottom(Signal.constant(Size.smallestPlus)),
      Style.CursorStyle(Signal.constant(Cursor.pointer)),
      Style.BorderRadius(Signal.constant(Size.mediumSmall)),
      Style.BorderTopStyle(Signal.constant(Line.none)),
      Style.BorderBottomStyle(Signal.constant(Line.none)),
      Style.BorderStartStyle(Signal.constant(Line.none)),
      Style.BorderEndStyle(Signal.constant(Line.none)),
    )
}
