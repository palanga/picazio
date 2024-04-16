package picazio

import picazio.syntax.*
import zio.stream.*

import scala.language.implicitConversions

implicit def signalSubscriptionRef[A](ref: SubscriptionRef[A]): RefSignalSyntax[A] = new RefSignalSyntax[A](ref)
implicit def signalStream[A](stream: Stream[Throwable, A]): StreamSignalSyntax[A]  = new StreamSignalSyntax[A](stream)
