package picazio

final class Icon private (val self: String) extends AnyVal {
  override def toString: String = self
}

object Icon {
  val heart    = new Icon("favorite")
  val flag     = new Icon("personal_places")
  val mine     = new Icon("explosion")
  val question = new Icon("question_mark")
  val box      = new Icon("check_box_outline_blank")
}
