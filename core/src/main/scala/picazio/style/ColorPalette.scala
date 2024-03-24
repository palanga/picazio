package picazio.style

import picazio.internal.Pigment

final class ColorPalette private[picazio] (
  private[picazio] val primary: Pigment,
  private[picazio] val secondary: Pigment,
) {

  def get(color: Color): Pigment = {

    val baseColor = color.colorPick match {
      case ColorPick.Custom(hexString) => Pigment.fromHexStringUnsafe(hexString)
      case ColorPick.Primary           => primary
      case ColorPick.Secondary         => secondary
    }

    val finalColor = color.shade match {
      case Shade.Lightest => baseColor.lighter.lighter.lighter
      case Shade.Lighter  => baseColor.lighter.lighter
      case Shade.Light    => baseColor.lighter
      case Shade.Medium    => baseColor
      case Shade.Dark      => baseColor.darker
      case Shade.Darker    => baseColor.darker.darker
      case Shade.Darkest   => baseColor.darker.darker.darker
    }

    finalColor

  }

}

object ColorPalette {

  def default: ColorPalette =
    new ColorPalette(
      Pigment.fromHexStringUnsafe("B57EDC"),
      Pigment.fromHexStringUnsafe("FADFAD"),
    )

}
