package picazio

import com.raquo.laminar.api.L.{ button as laminarButton, * }
import com.raquo.laminar.nodes.*
import org.scalajs.dom.{ console, Element }
import picazio.Shape.*
import picazio.signal.toLaminarSignal
import picazio.stream.toLaminarCommandStream
import picazio.theme.Theme
import zio.*

import scala.annotation.tailrec
import scala.util.chaining.scalaUtilChainingOps

private[picazio] class ShapeInterpreter(implicit runtime: Runtime[Theme], unsafe: Unsafe) {

  private val styleInterpreter = new StyleInterpreter()

  private[picazio] def asLaminarElement(shape: Shape): ReactiveElement[Element] =
    convertToLaminarReactiveElement(shape) pipe styleInterpreter.applyStyles(shape)

  @tailrec
  private def convertToLaminarReactiveElement(shape: Shape): ReactiveElement[Element] = {
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

      case Background(inner) => div(asLaminarElement(inner), minHeight.vh(100))

      case Reversed(inner) =>
        val flexModifier = if (isColumn(inner)) flexDirection.columnReverse else flexDirection.rowReverse
        amendHtmlOrEcho(asLaminarElement(inner))(flexModifier)

      case StaticColumn(content) =>
        div(
          content.map(asLaminarElement),
          display.flex,
          flexDirection.column,
        )

      case DynamicColumn(content) =>
        div(
          children <-- toLaminarSignal(content).map(_.map(asLaminarElement)),
          display.flex,
          flexDirection.column,
        )

      case StreamColumn(content) =>
        div(
          children.command <-- toLaminarCommandStream(content, asLaminarElement, CollectionCommand.Append.apply),
          display.flex,
          flexDirection.column,
        )

      case ZIOStreamColumn(content) =>
        div(
          children.command <-- toLaminarCommandStream(content, asLaminarElement, CollectionCommand.Append.apply),
          display.flex,
          flexDirection.column,
        )

      case StaticRow(content) =>
        div(
          content.map(asLaminarElement),
          display.flex,
          flexDirection.row,
        )

      case DynamicRow(content) =>
        div(
          children <-- toLaminarSignal(content).map(_.map(asLaminarElement)),
          display.flex,
          flexDirection.row,
        )

      case OnClick(task, inner) =>
        val runOnClick = onClick --> { _ => runtime.unsafe.runToFuture(task) }
        asLaminarElement(inner).amend(runOnClick)

      case OnInput(action, TextInput(_placeholder)) =>
        input(
          placeholder := _placeholder,
          onInput.mapToValue --> { current => runtime.unsafe.runToFuture(action(current)) },
        )

      case OnInput(action, SubscribedTextInput(_placeholder, ref)) =>
        input(
          placeholder := _placeholder,
          controlled(
            value <-- toLaminarSignal(ref.signal),
            onInput.mapToValue --> { current => runtime.unsafe.runToFuture(ref.set(current) <* action(current)) },
          ),
        )

      case OnInput(action, SignaledTextInput(_placeholder, signal)) =>
        val state = Var("")

        def handleOnInput(current: String): Unit = {
          state.set(current)
          runtime.unsafe.runToFuture(action(current))
        }

        runtime.unsafe.runToFuture(signal.changes.map(state.set).runDrain)
        input(
          placeholder := _placeholder,
          controlled(
            value <-- state,
            onInput.mapToValue --> { current => handleOnInput(current) },
          ),
        )

      case OnKeyPressed(action, inner) =>
        val runOnKeyPressed = onKeyDown --> { event => runtime.unsafe.runToFuture(action(event.keyCode)) }
        asLaminarElement(inner).amend(runOnKeyPressed)

      case OnInputFilter(filter, SubscribedTextInput(_placeholder, ref)) =>
        val state = Var("")

        def handleOnInput(current: String): Unit = {
          state.set(current)
          runtime.unsafe.runToFuture(ref.set(current))
        }

        runtime.unsafe.runToFuture(ref.changes.filter(filter).map(state.set).runDrain)
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

  @tailrec
  private def isColumn(inner: Shape): Boolean = inner match {
    case StaticColumn(_)         => true
    case DynamicColumn(_)        => true
    case StreamColumn(_)         => true
    case ZIOStreamColumn(_)      => true
    case StaticRow(_)            => false
    case DynamicRow(_)           => false
    case OnClick(_, inner)       => isColumn(inner)
    case OnInput(_, inner)       => isColumn(inner)
    case OnKeyPressed(_, inner)  => isColumn(inner)
    case OnInputFilter(_, inner) => isColumn(inner)
    case Focused(inner)          => isColumn(inner)
    case Styled(_, inner)        => isColumn(inner)
    case Reversed(inner)         => isColumn(inner)
    case _                       => println("Calling isColumn on a non column or row shape has no sense"); false
  }

  private def amendHtmlOrEcho(
    element: ReactiveElement[Element]
  )(modifier: Modifier[ReactiveHtmlElement.Base]): ReactiveElement[Element] =
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
