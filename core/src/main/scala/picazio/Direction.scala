package picazio

sealed private[picazio] trait Direction

private[picazio] object Direction {
  case object Column extends Direction
  case object Row    extends Direction
}
