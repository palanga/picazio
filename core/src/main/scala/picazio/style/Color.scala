package picazio.style

final class Color private (
  private[picazio] val role: ColorRole,
  private[picazio] val shade: Shade = Shade.Regular,
) {

  def lightest: Color        = new Color(role, Shade.Lightest)
  def ultraLight: Color      = new Color(role, Shade.UltraLight)
  def lighter: Color         = new Color(role, Shade.Lighter)
  def light: Color           = new Color(role, Shade.Light)
  def moderatelyLight: Color = new Color(role, Shade.ModeratelyLight)
  def slightlyLight: Color   = new Color(role, Shade.SlightlyLight)
  def regular: Color         = new Color(role, Shade.Regular)
  def slightlyDark: Color    = new Color(role, Shade.SlightlyDark)
  def moderatelyDark: Color  = new Color(role, Shade.ModeratelyDark)
  def dark: Color            = new Color(role, Shade.Dark)
  def darker: Color          = new Color(role, Shade.Darker)
  def ultraDark: Color       = new Color(role, Shade.UltraDark)
  def darkest: Color         = new Color(role, Shade.Darkest)

  def lighten(percent: Int): Color = new Color(role, Shade.Lighten(percent))
  def darken(percent: Int): Color  = new Color(role, Shade.Darken(percent))

}

object Color {

  def custom(hexString: String): Color = new Color(ColorRole.Custom(hexString))

  val primary: Color   = new Color(ColorRole.Primary)
  val secondary: Color = new Color(ColorRole.Secondary)
  val background       = new Color(ColorRole.Background)
  val surface          = new Color(ColorRole.Surface)
  val error            = new Color(ColorRole.Error)

  val onPrimary    = new Color(ColorRole.OnPrimary)
  val onSecondary  = new Color(ColorRole.OnSecondary)
  val onBackground = new Color(ColorRole.OnBackground)
  val onSurface    = new Color(ColorRole.OnSurface)
  val onError      = new Color(ColorRole.OnError)

}
