package picazio.style

sealed private[picazio] trait PaletteColor

private[picazio] object PaletteColor {
  case class Custom(hexString: String) extends PaletteColor
  case object Primary                  extends PaletteColor
  case object Secondary                extends PaletteColor
}
