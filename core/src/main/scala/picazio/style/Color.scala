package picazio.style

case class Color(paletteColor: PaletteColor, shade: Shade = Shade.Medium) {
  def brightest: Color = this.copy(shade = Shade.Brightest)
  def brighter: Color  = this.copy(shade = Shade.Brighter)
  def bright: Color    = this.copy(shade = Shade.Bright)
  def medium: Color    = this.copy(shade = Shade.Medium)
  def dark: Color      = this.copy(shade = Shade.Dark)
  def darker: Color    = this.copy(shade = Shade.Darker)
  def darkest: Color   = this.copy(shade = Shade.Darkest)
}

object Color {

  def custom(hexString: String): Color = Color(PaletteColor.Custom(hexString))
  def primary: Color                   = Color(PaletteColor.Primary)
  def secondary: Color                 = Color(PaletteColor.Secondary)

}
