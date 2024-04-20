// scalafmt: { maxColumn = 200 }
package picazio

import picazio.style.StyleSheet
import zio.*
import zio.stream.*

// TODO
// * plantilla gitter 8
// * los inputs se ven mal (mirar ejemplo de reactividad)
// * que cambie de color el botón cuando lo apretás
// * el botón en iOS tiene letra color azul
// * la entrada de texto en iOS tiene borde redondeado
// * aprender de SubscriptionRef.changes como usan ZStream.unwrapScoped y aplicarlo en Signal.fromStream y los ejemplos
// * enrutador y navegación
// * iconos
// * flotantes
// * imágenes
// * videos
// * surface (material design)
// * manejo de errores
// * listas mutables atómicas reactivas
// * ZIOWebApp ya no tiene sentido?
// * mejorar el logging del servidor de desarrollo
object Shape {

  def text(content: String): Shape[Any]                                                                        = StaticText(content)
  def text(content: Signal[String]): Shape[Any]                                                                = Text(content)
  def text[R](content: ZIO[R, Throwable, String]): Shape[R]                                                    = Eventual(content.map(StaticText.apply))
  def text[R, A: Tag](content: ZIO[R, Throwable, Signal[String]]): Shape[R]                                    = Eventual(content.map(Text.apply)) // the `A: Tag` here is a hack to prevent double definition after erasure
  def textInput: Shape[Any]                                                                                    = TextInput("")
  def textInput(placeholder: String): Shape[Any]                                                               = TextInput(placeholder)
  def textInput(ref: SubscriptionRef[String]): Shape[Any]                                                      = SubscribedTextInput("", ref)
  def textInput(placeholder: String, ref: SubscriptionRef[String]): Shape[Any]                                 = SubscribedTextInput(placeholder, ref)
  def textInput(signal: Signal[String]): Shape[Any]                                                            = SignaledTextInput("", signal)
  def textInput(placeholder: String, signal: Signal[String]): Shape[Any]                                       = SignaledTextInput(placeholder, signal)
  def button(content: String): Shape[Any]                                                                      = Button(content)
  def icon(icon: picazio.Icon): Shape[Any]                                                                     = Icon(icon)
  def background[R](content: Shape[R]): Shape[R]                                                               = Background(content)
  def column[R](content: Shape[R]*): Shape[R]                                                                  = StaticArray(content, Direction.Column)
  def column[R](content: Iterable[Shape[R]]): Shape[R]                                                         = StaticArray(content.toSeq, Direction.Column)
  def column[R](content: Signal[Seq[Shape[R]]]): Shape[R]                                                      = SignaledArray(content, Direction.Column)
  def column[R](content: Stream[Throwable, Shape[R]]): Shape[R]                                                = StreamedArray(content, Direction.Column)
  def column[R, R1](content: ZIO[R1, Throwable, Stream[Throwable, Shape[R]]]): Shape[R & R1]                   = Eventual(content.map(StreamedArray(_, Direction.Column)))
  def row[R](content: Shape[R]*): Shape[R]                                                                     = StaticArray(content, Direction.Row)
  def row[R](content: Iterable[Shape[R]]): Shape[R]                                                            = StaticArray(content.toSeq, Direction.Row)
  def row[R](content: Signal[Seq[Shape[R]]]): Shape[R]                                                         = SignaledArray(content, Direction.Row)
  def row[R](content: Stream[Throwable, Shape[R]]): Shape[R]                                                   = StreamedArray(content, Direction.Row)
  def row[R, R1](content: ZIO[R1, Throwable, Stream[Throwable, Shape[R]]]): Shape[R & R1]                      = Eventual(content.map(StreamedArray(_, Direction.Row)))
  def eventual[R, R1](content: ZIO[R1, Throwable, Shape[R]]): Shape[R & R1]                                    = Eventual(content)
  def eventualWith[R, R1, A](content: ZIO[R1, Throwable, A])(f: A => Shape[R]): Shape[R & R1]                  = Eventual(content.map(f))
  def variable[R](content: Signal[Shape[R]]): Shape[R]                                                         = Variable(content)
  def variableWith[R, A](content: Signal[A])(f: A => Shape[R]): Shape[R]                                       = Variable(content.map(f))
  def variableWith[R, R1, A](content: ZIO[R1, Throwable, Signal[A]])(f: A => Shape[R]): Shape[R & R1]          = Eventual(content.map(variableWith(_)(f)))
  def gridWith[R](columns: Int, rows: Int)(f: (Int, Int) => Shape[R]): Shape[R]                                =
    Shape.column(
      for (row <- 0 until rows)
        yield Shape.row(
          for (column <- 0 until columns)
            yield f(column, row)
        )
    )
  def gridWithZIO[R, R1](dimensions: ZIO[R1, Throwable, (Int, Int)])(f: (Int, Int) => Shape[R]): Shape[R & R1] =
    eventualWith(dimensions) { case (column, row) => gridWith(column, row)(f) }

  final private[picazio] case class StaticText(content: String)                                                   extends Shape[Any]
  final private[picazio] case class Text(content: Signal[String])                                                 extends Shape[Any]
  final private[picazio] case class TextInput(placeholder: String)                                                extends Shape[Any]
  final private[picazio] case class SubscribedTextInput(placeholder: String, ref: SubscriptionRef[String])        extends Shape[Any]
  final private[picazio] case class SignaledTextInput(placeholder: String, signal: Signal[String])                extends Shape[Any]
  final private[picazio] case class Button(content: String)                                                       extends Shape[Any]
  final private[picazio] case class Icon(icon: picazio.Icon)                                                      extends Shape[Any]
  final private[picazio] case class Background[R](inner: Shape[R])                                                extends Shape[R]
  final private[picazio] case class StaticArray[R](shapes: Seq[Shape[R]], direction: Direction)                   extends Shape[R]
  final private[picazio] case class SignaledArray[R](shapes: Signal[Seq[Shape[R]]], direction: Direction)         extends Shape[R]
  final private[picazio] case class StreamedArray[R](shapes: Stream[Throwable, Shape[R]], direction: Direction)   extends Shape[R]
  final private[picazio] case class Focused[R](inner: Shape[R])                                                   extends Shape[R]
  final private[picazio] case class Reversed[R](inner: Shape[R])                                                  extends Shape[R]
  final private[picazio] case class OnInputFilter[R](filter: String => Boolean, inner: Shape[R])                  extends Shape[R]
  final private[picazio] case class Styled[R](styles: StyleSheet, inner: Shape[R])                                extends Shape[R]
  final private[picazio] case class OnClick[R, R1](action: ZIO[R1, Throwable, Unit], inner: Shape[R])             extends Shape[R & R1]
  final private[picazio] case class OnInput[R, R1](action: String => ZIO[R1, Throwable, Unit], inner: Shape[R])   extends Shape[R & R1]
  final private[picazio] case class OnKeyPressed[R, R1](action: Int => ZIO[R1, Throwable, Unit], inner: Shape[R]) extends Shape[R & R1]
  final private[picazio] case class Eventual[R, R1](content: ZIO[R1, Throwable, Shape[R]])                        extends Shape[R & R1]
  final private[picazio] case class Loading[R, R1](loading: Shape[R], eventual: Shape[R1])                        extends Shape[R & R1]
  final private[picazio] case class Variable[R](shapeSignal: Signal[Shape[R]])                                    extends Shape[R]

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

  /**
   * Show a shape while this eventual shape is loading.
   */
  final def onLoading[R1](content: Shape[R1]): Shape[R & R1] =
    this match {
      case eventual: Shape.Eventual[?, ?] => Shape.Loading(content, eventual)
      case _                              =>
        println(s"Calling onLoading on a ${this.getClass.getSimpleName} is useless")
        this
    }

  // TODO Signal.as(something) instead of Signal.map(_ => something)
  /**
   * Repaint this shape every time the signal emits.
   */
  final def refreshOn(refreshTrigger: Signal[Any]): Shape[R] = Shape.variable(refreshTrigger.map(_ => this))

  /**
   * Repaint this shape every time the signal emits.
   */
  final def refreshOn[R1](refreshTrigger: ZIO[R1, Throwable, Signal[Any]]): Shape[R & R1] =
    Shape.eventualWith(refreshTrigger)(refreshOn)

  final def provide(layer: ZLayer[Any, Throwable, R]): Shape[Any] =
    Shape.eventual(
      ZIO.scopedWith(scope => layer.build(scope)).map(this.provideEnvironment)
    )

  private def provideEnvironment(env: ZEnvironment[R]): Shape[Any] = this match {
    case s @ Shape.StaticText(_)               => s
    case s @ Shape.Text(_)                     => s
    case s @ Shape.TextInput(_)                => s
    case s @ Shape.SubscribedTextInput(_, _)   => s
    case s @ Shape.SignaledTextInput(_, _)     => s
    case s @ Shape.Button(_)                   => s
    case s @ Shape.Icon(_)                     => s
    case s @ Shape.Background(inner)           => s.copy(inner.provideEnvironment(env))
    case s @ Shape.StaticArray(content, _)     => s.copy(content.map(_.provideEnvironment(env)))
    case s @ Shape.SignaledArray(content, _)   => s.copy(content.map(_.map(_.provideEnvironment(env))))
    case s @ Shape.StreamedArray(content, _)   => s.copy(content.map(_.provideEnvironment(env)))
    case s @ Shape.Focused(inner)              => s.copy(inner.provideEnvironment(env))
    case s @ Shape.Reversed(inner)             => s.copy(inner.provideEnvironment(env))
    case s @ Shape.OnInputFilter(_, inner)     => s.copy(inner = inner.provideEnvironment(env))
    case s @ Shape.Styled(_, inner)            => s.copy(inner = inner.provideEnvironment(env))
    case s @ Shape.OnClick(action, inner)      => s.copy(action.provideEnvironment(env), inner.provideEnvironment(env))
    case s @ Shape.OnInput(action, inner)      => s.copy(action(_).provideEnvironment(env), inner.provideEnvironment(env))
    case s @ Shape.OnKeyPressed(action, inner) => s.copy(action(_).provideEnvironment(env), inner.provideEnvironment(env))
    case s @ Shape.Eventual(content)           => s.copy(content.provideEnvironment(env).map(_.provideEnvironment(env)))
    case s @ Shape.Loading(content, inner)     => s.copy(content.provideEnvironment(env), inner.provideEnvironment(env))
    case s @ Shape.Variable(content)           => s.copy(content.map(_.provideEnvironment(env)))
  }

}
