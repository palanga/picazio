package picazio.style

sealed trait Overflow

object Overflow {
  case object Hidden   extends Overflow
  case object Ellipsis extends Overflow
  case object Scroll   extends Overflow
}
