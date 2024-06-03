package picazio.style

import picazio.Signal

object TextStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.FontSize(Signal.constant(Size.medium)),
      Style.PaddingTop(Signal.constant(Size.small)),
    )
}
