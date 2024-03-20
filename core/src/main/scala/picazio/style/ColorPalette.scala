package picazio.style

case class ColorPalette(
  primary: Pigment,
  secondary: Pigment,
) {

  def get(color: Color): Pigment = {

    val baseColor = color.paletteColor match {
      case PaletteColor.Custom(hexString) => Pigment.decode(hexString)
      case PaletteColor.Primary           => primary
      case PaletteColor.Secondary         => secondary
    }

    val finalColor = color.shade match {
      case Shade.Brightest => baseColor.brighter.brighter.brighter
      case Shade.Brighter  => baseColor.brighter.brighter
      case Shade.Bright    => baseColor.brighter
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
      Pigment.decode("B57EDC"),
      Pigment.decode("FADFAD"),
    )

}
