package picazio.syntax

import picazio.Signal
import zio.stream.SubscriptionRef

final class RefSignalSyntax[A](val self: SubscriptionRef[A]) extends AnyVal {
  def signal: Signal[A] = Signal.fromRef(self)
}
