package picazio.style

final class Color private (
  private[picazio] val colorPick: ColorPick,
  private[picazio] val shade: Shade = Shade.Medium,
) {
  def brightest: Color = new Color(colorPick, Shade.Lightest)
  def brighter: Color  = new Color(colorPick, Shade.Lighter)
  def bright: Color    = new Color(colorPick, Shade.Light)
  def medium: Color    = new Color(colorPick, Shade.Medium)
  def dark: Color      = new Color(colorPick, Shade.Dark)
  def darker: Color    = new Color(colorPick, Shade.Darker)
  def darkest: Color   = new Color(colorPick, Shade.Darkest)
}

object Color {
  def custom(hexString: String): Color = new Color(ColorPick.Custom(hexString))
  def primary: Color                   = new Color(ColorPick.Primary)
  def secondary: Color                 = new Color(ColorPick.Secondary)
}
