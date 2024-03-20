package picazio.style

sealed trait Wrap

object Wrap {
  case object NoWrap         extends Wrap
  case object WhiteSpaceWrap extends Wrap
}
