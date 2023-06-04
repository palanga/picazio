package picazio.style

import picazio.style

sealed trait Style

object Style {
  case class MarginTop(spacing: Spacing)     extends Style
  case class PaddingTop(spacing: Spacing)    extends Style
  case class PaddingBottom(spacing: Spacing) extends Style
  case class Cursor(cursor: style.Cursor)    extends Style
}
