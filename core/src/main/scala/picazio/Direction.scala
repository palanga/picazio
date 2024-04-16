package picazio

sealed private[picazio] trait Direction {
  def isColumn: Boolean
  def isRow: Boolean = !isColumn
}

private[picazio] object Direction {
  case object Column extends Direction { override def isColumn: Boolean = true  }
  case object Row    extends Direction { override def isColumn: Boolean = false }
}
