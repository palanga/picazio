package picazio

import com.raquo.airstream.custom.CustomSource.*
import com.raquo.laminar.api.L.Signal as LaminarSignal
import picazio.ops.LogOps
import zio.*

import scala.util.Try

object signal {

  private[picazio] def toLaminarSignal[A](
    signal: Signal[A]
  )(implicit runtime: Runtime[Any], unsafe: Unsafe): LaminarSignal[A] =
    LaminarSignal.fromCustomSource(
      initial = runtime.unsafe.run(signal.get.logError("Couldn't get signal value")).toTry,
      start = (setCurrent: SetCurrentValue[A], _: GetCurrentValue[A], _, _) =>
        runtime.unsafe.runToFuture(
          signal
            .changes
            .map(value => setCurrent(Try(value)))
            .runDrain
            .ignoreLoggedError("Error converting to laminar signal")
        ),
      stop = _ => (),
    )

}
