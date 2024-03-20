package picazio

import picazio.theme.Theme
import zio.*

object PicazioRuntime {

  val default: Runtime[Theme] = Runtime.default.withEnvironment(ZEnvironment[Theme](Theme.default))

  def withTheme(theme: Theme): Runtime[Theme] = Runtime.default.withEnvironment(ZEnvironment[Theme](theme))

}
