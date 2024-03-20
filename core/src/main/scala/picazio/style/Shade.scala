package picazio.style

sealed trait Shade

object Shade {
  case object Brightest extends Shade
  case object Brighter  extends Shade
  case object Bright    extends Shade
  case object Medium    extends Shade
  case object Dark      extends Shade
  case object Darker    extends Shade
  case object Darkest   extends Shade
}
