package picazio

import picazio.theme.*

import scala.language.implicitConversions

implicit def themeOps(theme: Theme): ThemeOps = new ThemeOps(theme)
