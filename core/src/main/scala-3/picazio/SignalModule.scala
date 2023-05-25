package picazio

import picazio.syntax.SignalSyntax
import zio.stream.SubscriptionRef

import scala.language.implicitConversions

implicit def signalSubscriptionRef[A](ref: SubscriptionRef[A]): SignalSyntax[A] = new SignalSyntax[A](ref)
