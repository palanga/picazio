package picazio.style

sealed trait PaletteColor

object PaletteColor {
  case class Custom(hexString: String) extends PaletteColor
  case object Primary                  extends PaletteColor
  case object Secondary                extends PaletteColor
}
