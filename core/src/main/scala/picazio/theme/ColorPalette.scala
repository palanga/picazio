package picazio.theme

import picazio.internal.Pigment
import picazio.style.*

final private[picazio] case class ColorPalette(
  private[picazio] val primary: Pigment,
  private[picazio] val secondary: Pigment,
  private[picazio] val background: Pigment,
  private[picazio] val surface: Pigment,
  private[picazio] val error: Pigment,
  private[picazio] val onPrimary: Pigment,
  private[picazio] val onSecondary: Pigment,
  private[picazio] val onBackground: Pigment,
  private[picazio] val onSurface: Pigment,
  private[picazio] val onError: Pigment,
) {

  def get(color: Color): Pigment = {

    val baseColor = color.role match {
      case ColorRole.Custom(hexString) => Pigment.fromHexStringUnsafe(hexString)
      case ColorRole.Primary           => primary
      case ColorRole.Secondary         => secondary
      case ColorRole.Background        => background
      case ColorRole.Surface           => surface
      case ColorRole.Error             => error
      case ColorRole.OnPrimary         => onPrimary
      case ColorRole.OnSecondary       => onSecondary
      case ColorRole.OnBackground      => onBackground
      case ColorRole.OnSurface         => onSurface
      case ColorRole.OnError           => onError
    }

    val finalColor = color.shade match {

      case Shade.Lightest        => baseColor.lighten(60)
      case Shade.UltraLight      => baseColor.lighten(50)
      case Shade.Lighter         => baseColor.lighten(40)
      case Shade.Light           => baseColor.lighten(30)
      case Shade.ModeratelyLight => baseColor.lighten(20)
      case Shade.SlightlyLight   => baseColor.lighten(10)
      case Shade.Regular         => baseColor
      case Shade.SlightlyDark    => baseColor.darken(10)
      case Shade.ModeratelyDark  => baseColor.darken(20)
      case Shade.Dark            => baseColor.darken(30)
      case Shade.Darker          => baseColor.darken(40)
      case Shade.UltraDark       => baseColor.darken(50)
      case Shade.Darkest         => baseColor.darken(60)

      case Shade.Lighten(percent) => baseColor.lighten(percent)
      case Shade.Darken(percent)  => baseColor.darken(percent)

    }

    finalColor

  }

}

object ColorPalette {

  def default: ColorPalette =
    new ColorPalette(
      Pigment.fromHexStringUnsafe("00b74f"),
      Pigment.fromHexStringUnsafe("00b74f"),
      Pigment.fromHexStringUnsafe("ffffff"),
      Pigment.fromHexStringUnsafe("ffffff"),
      Pigment.fromHexStringUnsafe("cc0000"),
      Pigment.fromHexStringUnsafe("000000"),
      Pigment.fromHexStringUnsafe("000000"),
      Pigment.fromHexStringUnsafe("000000"),
      Pigment.fromHexStringUnsafe("000000"),
      Pigment.fromHexStringUnsafe("ffffff"),
    )

}
