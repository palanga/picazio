package picazio.theme

import picazio.internal.Pigment
import zio.*

final class ThemeOps(val self: Theme) extends AnyVal {

  /**
   * Convert this theme to a zio layer.
   */
  def asLayer: ULayer[Theme] = ZLayer.succeed(self)

  /**
   * A secondary color provides more ways to accent and distinguish your
   * product. Having a secondary color is optional, and should be applied
   * sparingly to accent select parts of your UI.
   *
   * If you don’t have a secondary color, your primary color can also be used to
   * accent elements.
   *
   * Secondary colors are best for:
   *   - Floating action buttons
   *   - Selection controls, like sliders and switches
   *   - Highlighting selected text
   *   - Progress bars
   *   - Links and headlines
   *
   * Just like the primary color, your secondary color can have dark and light
   * variants. A color theme can use your primary color, secondary color, and
   * dark and light variants of each color.
   */
  def secondaryColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(secondary = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * The background color appears behind scrollable content. The baseline
   * background and surface color is white.
   */
  def backgroundColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(background = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * Surface colors affect surfaces of components, such as cards, sheets, and
   * menus.
   */
  def surfaceColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(surface = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * Error color indicates errors in components, such as invalid text in a text
   * field. The baseline error color is red.
   */
  def errorColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(error = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * “On” colors, color elements that appear “on” top of surfaces.
   *
   * “On” colors are primarily applied to text, iconography, and strokes.
   * Sometimes, they are applied to surfaces.
   *
   * The default values for “on” colors are white and black.
   */
  def onPrimaryColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(onPrimary = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * “On” colors, color elements that appear “on” top of surfaces.
   *
   * “On” colors are primarily applied to text, iconography, and strokes.
   * Sometimes, they are applied to surfaces.
   *
   * The default values for “on” colors are white and black.
   */
  def onSecondaryColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(onSecondary = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * “On” colors, color elements that appear “on” top of surfaces.
   *
   * “On” colors are primarily applied to text, iconography, and strokes.
   * Sometimes, they are applied to surfaces.
   *
   * The default values for “on” colors are white and black.
   */
  def onBackgroundColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(onBackground = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * “On” colors, color elements that appear “on” top of surfaces.
   *
   * “On” colors are primarily applied to text, iconography, and strokes.
   * Sometimes, they are applied to surfaces.
   *
   * The default values for “on” colors are white and black.
   */
  def onSurfaceColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(onSurface = Pigment.fromHexStringUnsafe(hexString)))

  /**
   * “On” colors, color elements that appear “on” top of surfaces.
   *
   * “On” colors are primarily applied to text, iconography, and strokes.
   * Sometimes, they are applied to surfaces.
   *
   * The default values for “on” colors are white and black.
   */
  def onErrorColor(hexString: String): Theme =
    self.copy(colorPalette = self.colorPalette.copy(onError = Pigment.fromHexStringUnsafe(hexString)))

}
