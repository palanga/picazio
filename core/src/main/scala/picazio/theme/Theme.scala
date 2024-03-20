package picazio.theme

import picazio.internal.Pigment

case class Theme(
  sizeMultiplier: Double,
  typography: String,
  colorPalette: ColorPalette,
)

object Theme {

  /**
   * A primary color is the color displayed most frequently across your app's
   * screens and components.
   *
   * Your primary color can be used to make a color theme for your app,
   * including dark and light primary color variants.
   *
   * To create contrast between UI elements, such as a top app bar from a system
   * bar, you can use light or dark variants of your primary colors. You can
   * also use these to distinguish elements within a component, such as the icon
   * of a floating action button from its circular container.
   */
  def primaryColor(hexString: String): Theme =
    Theme.default.copy(colorPalette = ColorPalette.default.copy(primary = Pigment.fromHexStringUnsafe(hexString)))

  val default: Theme = Theme(1, "system-ui", ColorPalette.default)

}
