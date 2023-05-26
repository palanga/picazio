package picazio

import zio.*
import zio.stream.*

object Shape {

  def text(content: String): Shape                                        = StaticText(content)
  def text(content: Signal[String]): Shape                                = Text(content)
  def textInput(): Shape                                                  = TextInput("")
  def textInput(placeholder: String): Shape                               = TextInput(placeholder)
  def textInput(ref: SubscriptionRef[String]): Shape                      = SubscribedTextInput("", ref)
  def textInput(placeholder: String, ref: SubscriptionRef[String]): Shape = SubscribedTextInput(placeholder, ref)
  def textInput(signal: Signal[String]): Shape                            = SignaledTextInput("", signal)
  def textInput(placeholder: String, signal: Signal[String]): Shape       = SignaledTextInput(placeholder, signal)
  def button(content: String): Shape                                      = Button(content)
  def column(content: Shape*): Shape                                      = StaticColumn(content)
  def column(content: Signal[Seq[Shape]]): Shape                          = DynamicColumn(content)
  def row(content: Shape*): Shape                                         = StaticRow(content)
  def row(content: Signal[Seq[Shape]]): Shape                             = DynamicRow(content)

  private[picazio] final case class StaticText(content: String)                                            extends Shape
  private[picazio] final case class Text(content: Signal[String])                                          extends Shape
  private[picazio] final case class TextInput(placeholder: String)                                         extends Shape
  private[picazio] final case class SubscribedTextInput(placeholder: String, ref: SubscriptionRef[String]) extends Shape
  private[picazio] final case class SignaledTextInput(placeholder: String, signal: Signal[String])         extends Shape
  private[picazio] final case class Button(content: String)                                                extends Shape
  private[picazio] final case class StaticColumn(content: Seq[Shape])                                      extends Shape
  private[picazio] final case class DynamicColumn(content: Signal[Seq[Shape]])                             extends Shape
  private[picazio] final case class StaticRow(content: Seq[Shape])                                         extends Shape
  private[picazio] final case class DynamicRow(content: Signal[Seq[Shape]])                                extends Shape
  private[picazio] final case class OnClick(action: Task[Unit], inner: Shape)                              extends Shape
  private[picazio] final case class OnInput(action: String => Task[Unit], inner: Shape)                    extends Shape
  private[picazio] final case class OnInputFilter(filter: String => Boolean, inner: Shape)                 extends Shape

}

sealed trait Shape {
  def onClick(action: Task[Unit]): Shape              = Shape.OnClick(action, this)
  def onInput(action: String => Task[Unit]): Shape    = Shape.OnInput(action, this)
  def onInputFilter(filter: String => Boolean): Shape = Shape.OnInputFilter(filter, this)
}
