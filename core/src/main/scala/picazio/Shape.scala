package picazio

import zio.*

object Shape {

  def text(content: String): Shape   = Text(content)
  def button(content: String): Shape = Button(content)

  case class Text(content: String)                     extends Shape
  case class Button(content: String)                   extends Shape
  case class OnClick(action: Task[Unit], inner: Shape) extends Shape

}

sealed trait Shape {
  def onClick(action: Task[Unit]): Shape = Shape.OnClick(action, this)
}
