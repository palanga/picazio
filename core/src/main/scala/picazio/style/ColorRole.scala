package picazio.style

sealed private[picazio] trait ColorRole

/**
 * Represents a color choice from the theme's palette, or a custom one.
 */
private[picazio] object ColorRole {
  case class Custom(hexString: String) extends ColorRole
  case object Primary                  extends ColorRole
  case object Secondary                extends ColorRole
  case object Background               extends ColorRole
  case object Surface                  extends ColorRole
  case object Error                    extends ColorRole
  case object OnPrimary                extends ColorRole
  case object OnSecondary              extends ColorRole
  case object OnBackground             extends ColorRole
  case object OnSurface                extends ColorRole
  case object OnError                  extends ColorRole
}
