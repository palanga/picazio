package picazio.style

sealed trait Shade

object Shade {
  case object Lightest extends Shade
  case object Lighter  extends Shade
  case object Light    extends Shade
  case object Medium   extends Shade
  case object Dark     extends Shade
  case object Darker   extends Shade
  case object Darkest  extends Shade
}
