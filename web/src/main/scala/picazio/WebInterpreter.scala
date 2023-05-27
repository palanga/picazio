package picazio

import com.raquo.airstream.custom.CustomSource.*
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.laminar.api.L.{Signal as LaminarSignal, button as laminarButton, *}
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom.Element
import picazio.Shape.*
import zio.*
import zio.stream.*

import scala.util.Try

private[picazio] object WebInterpreter {

  private[picazio] def toLaminar(
    shape: Shape
  )(implicit runtime: Runtime[Any], unsafe: Unsafe): ReactiveElement[Element] =
    shape match {
      case StaticText(content) => span(content)

      case Text(content) => span(child.text <-- toLaminarSignal(content))

      case TextInput(_placeholder) =>
        println("Using a text input without an onInput handler is unrecommended")
        input(placeholder := _placeholder)

      case SubscribedTextInput(_placeholder, ref) =>
        input(
          placeholder := _placeholder,
          controlled(
            value <-- toLaminarSignal(ref.signal),
            onInput.mapToValue --> { current => runtime.unsafe.run(ref.setAsync(current)) },
          ),
        )

      case SignaledTextInput(_placeholder, signal) =>
        println("Using a text input without an onInput handler is unrecommended")
        input(
          placeholder := _placeholder,
          value <-- toLaminarSignal(signal),
        )

      case Button(content) => laminarButton(content)

      case StaticColumn(content) =>
        div(content.map(toLaminar), display.flex, flexDirection.column, alignItems.flexStart, justifyContent.flexStart)

      case DynamicColumn(content) =>
        val laminarSignal: LaminarSignal[Seq[Shape]] = toLaminarSignal(content)
        laminarSignal.map(_.map(toLaminar))
        div(
          children <-- laminarSignal.map(_.map(toLaminar)),
          display.flex,
          flexDirection.column,
          alignItems.flexStart,
          justifyContent.flexStart,
        )

      case StaticRow(content) =>
        div(content.map(toLaminar), display.flex, flexDirection.row, alignItems.flexStart, justifyContent.flexStart)

      case DynamicRow(content) =>
        val laminarSignal: LaminarSignal[Seq[Shape]] = toLaminarSignal(content)
        laminarSignal.map(_.map(toLaminar))
        div(
          children <-- laminarSignal.map(_.map(toLaminar)),
          display.flex,
          flexDirection.row,
          alignItems.flexStart,
          justifyContent.flexStart,
        )

      case OnClick(task, inner) =>
        val runOnClick = onClick --> { _ => runtime.unsafe.run(task) }
        toLaminar(inner).amend(runOnClick)

      case OnInput(action, TextInput(_placeholder)) =>
        input(
          placeholder := _placeholder,
          onInput.mapToValue --> { current => runtime.unsafe.run(action(current)) },
        )

      case OnInput(action, SubscribedTextInput(_placeholder, ref)) =>
        input(
          placeholder := _placeholder,
          controlled(
            value <-- toLaminarSignal(ref.signal),
            onInput.mapToValue --> { current => runtime.unsafe.run(ref.setAsync(current) <* action(current)) },
          ),
        )

      case OnInput(action, SignaledTextInput(_placeholder, signal)) =>
        val state = Var("")
        def handleOnInput(current: String): Unit = {
          state.set(current)
          runtime.unsafe.run(action(current))
        }
        runtime.unsafe.runToFuture(signal.changes.map(value => state.set(value)).runDrain)
        input(
          placeholder := _placeholder,
          controlled(
            value <-- state.signal,
            onInput.mapToValue --> { current => handleOnInput(current) },
          ),
        )

      case OnInputFilter(filter, SubscribedTextInput(_placeholder, ref)) =>
        input(
          placeholder := _placeholder,
          controlled(
            value <-- toLaminarSignal(ref.signal),
            onInput.mapToValue --> { current => runtime.unsafe.run(ref.setAsync(current).when(filter(current))) },
          ),
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

  private def toLaminarSignal[A](signal: Signal[A])(implicit runtime: Runtime[Any], unsafe: Unsafe): LaminarSignal[A] =
    LaminarSignal.fromCustomSource(
      initial = runtime.unsafe.run(signal.get).toTry,
      start = (setCurrent: SetCurrentValue[A], _: GetCurrentValue[A], _, _) =>
        runtime.unsafe.runToFuture(signal.changes.map(value => setCurrent(Try(value))).runDrain),
      stop = _ => (),
    )

}
