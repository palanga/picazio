package picazio.style

import picazio.Signal

/**
 * Default styles for columns and rows
 */
object ArrayStyles {
  val default: StyleSheet =
    StyleSheet.fromStyles(
      Style.Overflowing(Signal.constant(Overflow.Hidden))
    )
}
