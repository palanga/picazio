package picazio

import picazio.style.StyleSheet
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
  def column(content: Stream[Throwable, Shape]): Shape                    = StreamColumn(content)
  def column(content: Task[Stream[Throwable, Shape]]): Shape              = ZIOStreamColumn(content)
  def row(content: Shape*): Shape                                         = StaticRow(content)
  def row(content: Signal[Seq[Shape]]): Shape                             = DynamicRow(content)

  final private[picazio] case class StaticText(content: String)                                            extends Shape
  final private[picazio] case class Text(content: Signal[String])                                          extends Shape
  final private[picazio] case class TextInput(placeholder: String)                                         extends Shape
  final private[picazio] case class SubscribedTextInput(placeholder: String, ref: SubscriptionRef[String]) extends Shape
  final private[picazio] case class SignaledTextInput(placeholder: String, signal: Signal[String])         extends Shape
  final private[picazio] case class Button(content: String)                                                extends Shape
  final private[picazio] case class StaticColumn(content: Seq[Shape])                                      extends Shape
  final private[picazio] case class DynamicColumn(content: Signal[Seq[Shape]])                             extends Shape
  final private[picazio] case class StreamColumn(content: Stream[Throwable, Shape])                        extends Shape
  final private[picazio] case class ZIOStreamColumn(content: Task[Stream[Throwable, Shape]])               extends Shape
  final private[picazio] case class StaticRow(content: Seq[Shape])                                         extends Shape
  final private[picazio] case class DynamicRow(content: Signal[Seq[Shape]])                                extends Shape
  final private[picazio] case class OnClick(action: Task[Unit], inner: Shape)                              extends Shape
  final private[picazio] case class OnInput(action: String => Task[Unit], inner: Shape)                    extends Shape
  final private[picazio] case class OnKeyPressed(action: Int => Task[Unit], inner: Shape)                  extends Shape
  final private[picazio] case class OnInputFilter(filter: String => Boolean, inner: Shape)                 extends Shape
  final private[picazio] case class Focused(inner: Shape)                                                  extends Shape
  final private[picazio] case class Styled(styles: StyleSheet, inner: Shape)                               extends Shape

}

sealed trait Shape {

  /**
   * Run an effect when this shape is clicked.
   */
  final def onClick(action: Task[Unit]): Shape = Shape.OnClick(action, this)

  /**
   * Run an effect when this text input value changes.
   */
  final def onInput(action: String => Task[Unit]): Shape = Shape.OnInput(action, this)

  /**
   * Run an effect when a key is pressed on this text input.
   */
  final def onKeyPressed(action: Int => Task[Unit]): Shape = Shape.OnKeyPressed(action, this)

  /**
   * Filter out the values this text input allows.
   */
  final def onInputFilter(filter: String => Boolean): Shape = Shape.OnInputFilter(filter, this)

  /**
   * Focus this shape on mount.
   */
  final def focused: Shape = Shape.Focused(this)

}
