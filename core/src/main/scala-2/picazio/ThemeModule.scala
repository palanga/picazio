package picazio

import picazio.theme.*

import scala.language.implicitConversions

trait ThemeModule {
  implicit def themeOps(theme: Theme): ThemeOps = new ThemeOps(theme)
}
