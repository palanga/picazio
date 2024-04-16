package picazio

import com.raquo.airstream.custom.CustomSource.*
import com.raquo.laminar.api.L.Signal as LaminarSignal
import zio.*

import scala.util.Try

object signal {

  private[picazio] def toLaminarSignal[A](
    signal: Signal[A]
  )(implicit runtime: Runtime[Any], unsafe: Unsafe): LaminarSignal[A] =
    LaminarSignal.fromCustomSource(
      initial = runtime.unsafe.runToFuture(signal.get.logError).value.get,
      start = (setCurrent: SetCurrentValue[A], _: GetCurrentValue[A], _, _) =>
        runtime.unsafe.runToFuture(signal.changes.map(value => setCurrent(Try(value))).runDrain.ignoreLogged),
      stop = _ => (),
    )

}
