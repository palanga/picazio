package picazio

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.api.L.{ button as laminarButton, * }
import com.raquo.laminar.defs.attrs.HtmlAttrs
import com.raquo.laminar.nodes.*
import org.scalajs.dom.{ console, Element }
import picazio.Shape.*
import picazio.ops.LogOps
import picazio.signal.toLaminarSignal
import picazio.theme.Theme
import zio.*
import zio.stream.*

import scala.annotation.tailrec
import scala.util.chaining.scalaUtilChainingOps

private[picazio] class ShapeInterpreter(implicit runtime: Runtime[Theme], unsafe: Unsafe) extends HtmlAttrs {

  private val styleInterpreter = new StyleInterpreter()

  private[picazio] def asLaminarElement(shape: Shape[Any]): ReactiveElement[Element] =
    convertToLaminarReactiveElement(shape) pipe styleInterpreter.applyStyles(shape)

  @tailrec
  private def convertToLaminarReactiveElement(shape: Shape[Any]): ReactiveElement[Element] = {
    shape match {

      case StaticText(content) => span(content)
      case Text(content)       => span(child.text <-- toLaminarSignal(content))

      case TextInput(_placeholder) =>
        console.warn("Using a text input without an onInput handler has no sense.")
        input(placeholder := _placeholder)

      case SubscribedTextInput(_placeholder, ref) =>
        convertToLaminarReactiveElement(
          Shape.textInput(_placeholder, ref.signal).onInput(text => ref.set(text))
        ) // TODO check if this should call asLaminarElement instead of convertToLaminarReactiveElement

      case SignaledTextInput(_placeholder, signal) =>
        console.warn("Using a text input without an onInput handler has no sense.")
        input(
          placeholder := _placeholder,
          value <-- toLaminarSignal(signal),
        )

      case Button(content) =>
        laminarButton(
          content,
          width := "fit-content",
        )

      case Icon(icon) =>
        span(
          className("material-symbols-outlined"),
          icon.toString,
        )

      case Image(source) => img(src(source))

      case Video(source) =>
        videoTag(
          boolAsTrueFalseHtmlAttr("controls")(true),
          stringHtmlAttr("preload")("auto"),
          src(source),
        )

      case Background(inner) => div(asLaminarElement(inner), minHeight.vh(100))

      case Reversed(inner) =>
        val flexModifier = if (isColumn(inner)) flexDirection.columnReverse else flexDirection.rowReverse
        amendHtmlOrEcho(asLaminarElement(inner))(flexModifier)

      case StaticArray(shapes, direction) =>
        div(
          shapes.map(asLaminarElement),
          display.flex,
          asFlexDirection(direction),
        )

      case SignaledArray(shapes, direction) =>
        div(
          children <-- toLaminarSignal(shapes).map(_.map(asLaminarElement)),
          display.flex,
          asFlexDirection(direction),
        )

      case StreamedArray(shapes, direction) =>
        val commandBus = new EventBus[CollectionCommand.Base]
        runtime.unsafe.runToFuture(
          shapes
            .map(asLaminarElement)
            .map(CollectionCommand.Append.apply)
            // TODO explore if the following shouldn't be a flatMap of a ZIO.attempt(commandBus.emit)
            .map(commandBus.emit)
            .runDrain
            .ignoreLoggedError
        )
        div(
          children.command <-- commandBus.events,
          display.flex,
          asFlexDirection(direction),
        )

      case Grid(matrix) =>
        div(
          display.flex,
          flexDirection.column,
          matrix.map(row =>
            div(
              display.flex,
              flexDirection.row,
              row.map(asLaminarElement),
            )
          ),
        )

      case OnClick(task, inner) =>
        val runOnClick = onClick --> { _ => runtime.unsafe.runToFuture(task.ignoreLoggedError) }
        asLaminarElement(inner).amend(runOnClick)

      case OnInput(action, TextInput(_placeholder)) =>
        input(
          placeholder := _placeholder,
          onInput.mapToValue --> { current => runtime.unsafe.runToFuture(action(current).ignoreLoggedError) },
        )

      case OnInput(action, SubscribedTextInput(_placeholder, ref)) =>
        input(
          placeholder := _placeholder,
          controlled(
            value <-- toLaminarSignal(ref.signal),
            onInput.mapToValue --> { current =>
              runtime.unsafe.runToFuture(ref.set(current) <* action(current).ignoreLoggedError)
            },
          ),
        )

      case OnInput(action, SignaledTextInput(_placeholder, signal)) =>
        val state = Var("")

        def handleOnInput(current: String): Unit = {
          state.set(current)
          runtime.unsafe.runToFuture(action(current).ignoreLoggedError)
        }

        runtime.unsafe.runToFuture(signal.changes.map(state.set).runDrain.ignoreLoggedError)
        input(
          placeholder := _placeholder,
          controlled(
            value <-- state,
            onInput.mapToValue --> { current => handleOnInput(current) },
          ),
        )

      case OnKeyPressed(action, inner) =>
        val runOnKeyPressed = onKeyDown --> { event =>
          runtime.unsafe.runToFuture(action(event.keyCode).ignoreLoggedError)
        }
        asLaminarElement(inner).amend(runOnKeyPressed)

      case OnInputFilter(filter, SubscribedTextInput(_placeholder, ref)) =>
        val state = Var("")

        def handleOnInput(current: String): Unit = {
          state.set(current)
          runtime.unsafe.runToFuture(ref.set(current).ignoreLoggedError)
        }

        runtime.unsafe.runToFuture(ref.changes.filter(filter).map(state.set).runDrain.ignoreLoggedError)
        input(
          placeholder := _placeholder,
          controlled(
            value <-- state,
            onInput.mapToValue.filter(filter) --> { current => handleOnInput(current) },
          ),
        )

      case Focused(inner) => amendHtmlOrEcho(asLaminarElement(inner))(onMountFocus)

      case Styled(_, inner) => asLaminarElement(inner)

      case OnInput(action, Styled(_, inner)) => asLaminarElement(OnInput(action, inner))

      case OnInputFilter(filter, Styled(_, inner)) => asLaminarElement(OnInputFilter(filter, inner))

      case Variable(_)             => reactiveShapeAsLaminarElement(shape)
      case Eventual(_)             => reactiveShapeAsLaminarElement(shape)
      case Loading(_, Eventual(_)) => reactiveShapeAsLaminarElement(shape)

      case Loading(_, inner) =>
        throw new IllegalArgumentException(
          s"Can't add an on loading shape to a non eventual shape: ${inner.getClass.getName}"
        )

      case OnInput(_, inner) =>
        throw new IllegalArgumentException(
          s"Can't add an on input handler to a non input element: ${inner.getClass.getName}"
        )

      case OnInputFilter(_, inner) =>
        throw new IllegalArgumentException(
          s"An on input filter can only be added to a text input constructed from a subscription ref, not a: ${inner.getClass.getName}"
        )

    }

  }

  private def asFlexDirection(direction: Direction) = direction match {
    case Direction.Column => flexDirection.column
    case Direction.Row    => flexDirection.row
  }

  private def reactiveShapeAsLaminarElement(shape: Shape[Any]) = {
    val dummy: ReactiveElement.Base = span("dummy", display.none)
    lazy val parent                 = dummy.maybeParent.get
    runtime.unsafe.runToFuture(
      shapeAsShapeStream(shape)
        .map(asLaminarElement)
        .runFoldZIO(dummy)(replaceElement(parent))
        .ignoreLoggedError("Error replacing child")
    )
    dummy
  }

  private def shapeAsShapeStream(shape: Shape[Any]): Stream[Throwable, Shape[Any]] = shape match {
    case Shape.Eventual(task)             => ZStream.fromZIO(task.map(shapeAsShapeStream)).flatten
    case Shape.Variable(signal)           => signal.changes.flatMap(shapeAsShapeStream)
    case Shape.Loading(loading, eventual) => ZStream.succeed(loading) ++ shapeAsShapeStream(eventual)
    case _                                => ZStream.succeed(shape)
  }

  private def replaceElement(
    parent: => ParentNode.Base
  )(currentElement: ReactiveElement.Base, newElement: ReactiveElement.Base) =
    ZIO.succeed {
      ParentNode.replaceChild(parent, currentElement, newElement)
      newElement
    }

  @tailrec
  private def isColumn(inner: Shape[?]): Boolean = inner match {
    case StaticText(_)               => invalid
    case Text(_)                     => invalid
    case TextInput(_)                => invalid
    case SubscribedTextInput(_, _)   => invalid
    case SignaledTextInput(_, _)     => invalid
    case Button(_)                   => invalid
    case Icon(_)                     => invalid
    case Image(_)                    => invalid
    case Video(_)                    => invalid
    case Background(_)               => invalid
    case StaticArray(_, direction)   => direction.isColumn
    case SignaledArray(_, direction) => direction.isColumn
    case StreamedArray(_, direction) => direction.isColumn
    case Grid(_)                     => invalid // TODO by now
    case Focused(_)                  => invalid
    case Reversed(inner)             => isColumn(inner)
    case OnInputFilter(_, _)         => invalid
    case Styled(_, inner)            => isColumn(inner)
    case OnClick(_, inner)           => isColumn(inner)
    case OnInput(_, inner)           => isColumn(inner)
    case OnKeyPressed(_, inner)      => isColumn(inner)
    case Eventual(_)                 => invalid
    case Loading(_, _)               => invalid
    case Variable(_)                 => invalid
  }

  private def invalid = {
    println("Calling isColumn on a non column or row shape has no sense")
    false
  }

  private def amendHtmlOrEcho(
    element: ReactiveElement.Base
  )(modifier: Modifier[ReactiveHtmlElement.Base]): ReactiveElement.Base =
    element match {
      case html: ReactiveHtmlElement[?] => html.amend(modifier)
      case svg: ReactiveSvgElement[?]   =>
        console.warn("Applying a modifier to an svg has no effect.")
        svg
      case _                            =>
        throw new IllegalArgumentException(
          s"ReactiveHtmlElement or ReactiveSvgElement expected. Got: ${element.getClass.getName}"
        )
    }

}
