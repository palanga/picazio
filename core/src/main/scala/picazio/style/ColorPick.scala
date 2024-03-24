package picazio.style

sealed private[picazio] trait ColorPick

/**
 * Represents a color choice from the theme's palette, or a custom one.
 */
private[picazio] object ColorPick {
  case class Custom(hexString: String) extends ColorPick
  case object Primary                  extends ColorPick
  case object Secondary                extends ColorPick
}
