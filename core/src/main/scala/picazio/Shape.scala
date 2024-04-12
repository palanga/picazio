// scalafmt: { maxColumn = 150 }
package picazio

import picazio.style.StyleSheet
import zio.*
import zio.stream.*

object Shape {

  def text(content: String): Shape[Any]                                        = StaticText(content)
  def text(content: Signal[String]): Shape[Any]                                = Text(content)
  def textInput(): Shape[Any]                                                  = TextInput("")
  def textInput(placeholder: String): Shape[Any]                               = TextInput(placeholder)
  def textInput(ref: SubscriptionRef[String]): Shape[Any]                      = SubscribedTextInput("", ref)
  def textInput(placeholder: String, ref: SubscriptionRef[String]): Shape[Any] = SubscribedTextInput(placeholder, ref)
  def textInput(signal: Signal[String]): Shape[Any]                            = SignaledTextInput("", signal)
  def textInput(placeholder: String, signal: Signal[String]): Shape[Any]       = SignaledTextInput(placeholder, signal)
  def button(content: String): Shape[Any]                                      = Button(content)
  def background[R](content: Shape[R]): Shape[R]                               = Background(content)
  def column[R](content: Shape[R]*): Shape[R]                                  = StaticColumn[R](content)
  def column[R](content: Signal[Seq[Shape[R]]]): Shape[R]                      = DynamicColumn[R](content)
  def column[R](content: Stream[Throwable, Shape[R]]): Shape[R]                = StreamColumn[R](content)
  def column[R](content: Task[Stream[Throwable, Shape[R]]]): Shape[R]          = ZIOStreamColumn[R](content)
  def row[R](content: Shape[R]*): Shape[R]                                     = StaticRow[R](content)
  def row[R](content: Signal[Seq[Shape[R]]]): Shape[R]                         = DynamicRow[R](content)

  final private[picazio] case class StaticText(content: String)                                                   extends Shape[Any]
  final private[picazio] case class Text(content: Signal[String])                                                 extends Shape[Any]
  final private[picazio] case class TextInput(placeholder: String)                                                extends Shape[Any]
  final private[picazio] case class SubscribedTextInput(placeholder: String, ref: SubscriptionRef[String])        extends Shape[Any]
  final private[picazio] case class SignaledTextInput(placeholder: String, signal: Signal[String])                extends Shape[Any]
  final private[picazio] case class Button(content: String)                                                       extends Shape[Any]
  final private[picazio] case class Background[R](inner: Shape[R])                                                extends Shape[R]
  final private[picazio] case class StaticColumn[R](content: Seq[Shape[R]])                                       extends Shape[R]
  final private[picazio] case class DynamicColumn[R](content: Signal[Seq[Shape[R]]])                              extends Shape[R]
  final private[picazio] case class StreamColumn[R](content: Stream[Throwable, Shape[R]])                         extends Shape[R]
  final private[picazio] case class ZIOStreamColumn[R](content: Task[Stream[Throwable, Shape[R]]])                extends Shape[R]
  final private[picazio] case class StaticRow[R](content: Seq[Shape[R]])                                          extends Shape[R]
  final private[picazio] case class DynamicRow[R](content: Signal[Seq[Shape[R]]])                                 extends Shape[R]
  final private[picazio] case class Focused[R](inner: Shape[R])                                                   extends Shape[R]
  final private[picazio] case class Reversed[R](inner: Shape[R])                                                  extends Shape[R]
  final private[picazio] case class OnInputFilter[R](filter: String => Boolean, inner: Shape[R])                  extends Shape[R]
  final private[picazio] case class Styled[R](styles: StyleSheet, inner: Shape[R])                                extends Shape[R]
  final private[picazio] case class OnClick[R, R1](action: ZIO[R1, Throwable, Unit], inner: Shape[R])             extends Shape[R & R1]
  final private[picazio] case class OnInput[R, R1](action: String => ZIO[R1, Throwable, Unit], inner: Shape[R])   extends Shape[R & R1]
  final private[picazio] case class OnKeyPressed[R, R1](action: Int => ZIO[R1, Throwable, Unit], inner: Shape[R]) extends Shape[R & R1]

}

sealed trait Shape[-R] {

  /**
   * Run an effect when this shape is clicked.
   */
  final def onClick[R1](action: ZIO[R1, Throwable, Unit]): Shape[R & R1] = Shape.OnClick(action, this)

  /**
   * Run an effect when this text input value changes.
   */
  final def onInput[R1](action: String => ZIO[R1, Throwable, Unit]): Shape[R & R1] = Shape.OnInput(action, this)

  /**
   * Run an effect when a key is pressed on this text input.
   */
  final def onKeyPressed[R1](action: Int => ZIO[R1, Throwable, Unit]): Shape[R & R1] = Shape.OnKeyPressed(action, this)

  /**
   * Filter out the values this text input allows.
   */
  final def onInputFilter(filter: String => Boolean): Shape[R] = Shape.OnInputFilter(filter, this)

  /**
   * Focus this shape on mount.
   */
  final def focused: Shape[R] = Shape.Focused(this)

  /**
   * Reverse the order of the items on this column or row.
   */
  final def reverse: Shape[R] = Shape.Reversed(this)

  final def provide(layer: ZLayer[Any, Throwable, R]): Shape[Any] = this match {
    case s @ Shape.StaticText(_)               => s
    case s @ Shape.Text(_)                     => s
    case s @ Shape.TextInput(_)                => s
    case s @ Shape.SubscribedTextInput(_, _)   => s
    case s @ Shape.SignaledTextInput(_, _)     => s
    case s @ Shape.Button(_)                   => s
    case s @ Shape.Background(inner)           => s.copy(inner.provide(layer))
    case s @ Shape.StaticColumn(content)       => s.copy(content.map(_.provide(layer)))
    case s @ Shape.DynamicColumn(content)      => s.copy(content.map(_.map(_.provide(layer))))
    case s @ Shape.StreamColumn(content)       => s.copy(content.map(_.provide(layer)))
    case s @ Shape.ZIOStreamColumn(content)    => s.copy(content.map(_.map(_.provide(layer))))
    case s @ Shape.StaticRow(content)          => s.copy(content.map(_.provide(layer)))
    case s @ Shape.DynamicRow(content)         => s.copy(content.map(_.map(_.provide(layer))))
    case s @ Shape.Focused(inner)              => s.copy(inner.provide(layer))
    case s @ Shape.Reversed(inner)             => s.copy(inner.provide(layer))
    case s @ Shape.OnInputFilter(_, inner)     => s.copy(inner = inner.provide(layer))
    case s @ Shape.Styled(_, inner)            => s.copy(inner = inner.provide(layer))
    case s @ Shape.OnClick(action, inner)      => s.copy(action.provide(layer), inner.provide(layer))
    case s @ Shape.OnInput(action, inner)      => s.copy(action(_).provide(layer), inner.provide(layer))
    case s @ Shape.OnKeyPressed(action, inner) => s.copy(action(_).provide(layer), inner.provide(layer))
  }

}
