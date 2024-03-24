package picazio.style

case class ColorPalette(
  primary: Pigment,
  secondary: Pigment,
) {

  def get(color: Color): Pigment = {

    val baseColor = color.paletteColor match {
      case PaletteColor.Custom(hexString) => Pigment.fromHexStringUnsafe(hexString)
      case PaletteColor.Primary           => primary
      case PaletteColor.Secondary         => secondary
    }

    val finalColor = color.shade match {
      case Shade.Brightest => baseColor.lighter.lighter.lighter
      case Shade.Brighter  => baseColor.lighter.lighter
      case Shade.Bright    => baseColor.lighter
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
    ColorPalette(
      Pigment.fromHexStringUnsafe("B57EDC"),
      Pigment.fromHexStringUnsafe("FADFAD"),
    )

}
