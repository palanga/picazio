package picazio

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom.Element
import zio.*
import zio.stream.*

object stream {

  private[picazio] def toLaminarCommandStream[A](
    stream: Stream[Throwable, A],
    asLaminarElement: A => ReactiveElement[Element],
    command: ReactiveElement[Element] => CollectionCommand.Base,
  )(implicit runtime: Runtime[Any], unsafe: Unsafe): EventStream[CollectionCommand.Base] = {
    // TODO update laminar docs. ChildrenCommand is now CollectionCommand.Base
    val commandBus = new EventBus[CollectionCommand.Base]
    runtime.unsafe.runToFuture(stream.map(asLaminarElement andThen command andThen commandBus.emit).runDrain)
    commandBus.events
  }

}
