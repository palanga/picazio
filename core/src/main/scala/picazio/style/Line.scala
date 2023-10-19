package picazio.style

sealed trait Line

object Line {
  case object None  extends Line
  case object Solid extends Line
}
