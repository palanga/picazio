package picazio.style

import picazio.Signal

object OnClickStyles {
  val default: StyleSheet = StyleSheet.fromStyle(Style.CursorStyle(Signal.constant(Cursor.pointer)))
}
