package picazio.style

import picazio.Signal

object BackgroundStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.BackgroundColorStyle(Signal.constant(Color.background))
    )
}
