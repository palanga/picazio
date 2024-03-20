package examples.clock

import picazio.*
import zio.*
import zio.stream.*

object Main extends ZIOWebApp {

  override def root: Task[Shape] =
    for {
      secondsRef <- SubscriptionRef.make(0)
      _          <- secondsRef.getAndUpdate(_ + 1).delay(1.second).forever.forkDaemon
    } yield clock(secondsRef.signal)

  private def clock(seconds: Signal[Int]): Shape = Shape.text(seconds.map(_.toString))

}
