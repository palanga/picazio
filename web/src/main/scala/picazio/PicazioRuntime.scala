package picazio

import picazio.style.Theme
import zio.*

object PicazioRuntime {
  val default: Runtime[Theme] = Runtime.default.withEnvironment {
    ZEnvironment[Theme](Theme.default)
  }
}
