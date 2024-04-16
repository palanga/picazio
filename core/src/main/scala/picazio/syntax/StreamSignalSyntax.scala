package picazio.syntax

import picazio.Signal
import zio.*
import zio.stream.*

final class StreamSignalSyntax[A](val self: Stream[Throwable, A]) extends AnyVal {
  def makeSignal: Task[Signal[A]] = Signal.fromStream(self)
}
