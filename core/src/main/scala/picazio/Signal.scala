package picazio

import zio.*
import zio.stream.*

import scala.util.chaining.scalaUtilChainingOps

trait Signal[+A] {

  def get: Task[A]
  def changes: Stream[Throwable, A]
  def map[B](f: A => B): Signal[B]

  def debug(prefix: String): Task[A]  = get.debug(prefix)
  def tap(f: A => Task[Any]): Task[A] = get.tap(f)

  def zip[B](that: Signal[B]): Signal[(A, B)]                   = new ZippedSignal(this, that)
  def zipWith[B, C](that: Signal[B])(f: (A, B) => C): Signal[C] = new ZippedWithSignal(this, that, f)

}

object Signal {

  def constant[A](value: A): Signal[A] = new ConstantSignal(value)

  def fromRef[A](ref: SubscriptionRef[A]): Signal[A] = new RefSignal(ref)

  def fromStream[A](stream: Stream[Throwable, A]): Task[Signal[A]] =
    for {
      head <- stream.take(1).runHead.someOrFailException
      ref  <- SubscriptionRef.make(head)
      _    <- stream.mapZIO(element => ref.set(element)).runDrain.forkDaemon
    } yield new RefSignal(ref)

}

final private[picazio] class ConstantSignal[A](val self: A) extends Signal[A] {
  override def get: Task[A]                  = ZIO.succeed(self)
  override def changes: Stream[Throwable, A] = ZStream.succeed(self)
  override def map[B](f: A => B): Signal[B]  = new ConstantSignal(f(self))
}

final private[picazio] class RefSignal[A](underlying: SubscriptionRef[A]) extends Signal[A] {
  override def get: Task[A]                  = underlying.get
  override def changes: Stream[Throwable, A] = underlying.changes
  override def map[B](f: A => B): Signal[B]  = new MappedSignal(underlying, f)
}

final private[picazio] class MappedSignal[A, B](underlying: SubscriptionRef[A], f: A => B) extends Signal[B] {
  override def get: Task[B]                  = underlying.get.map(f)
  override def changes: Stream[Throwable, B] = underlying.changes.map(f)
  override def map[C](g: B => C): Signal[C]  = new MappedSignal(underlying, g compose f)
}

final private[picazio] class ZippedSignal[A, B](first: Signal[A], second: Signal[B]) extends Signal[(A, B)] {
  override def get: Task[(A, B)]                   = first.get.zip(second.get)
  override def changes: Stream[Throwable, (A, B)]  = first.changes.zipLatest(second.changes)
  override def map[C](f: ((A, B)) => C): Signal[C] = new ZippedWithSignal[A, B, C](first, second, f(_, _))
}

final private[picazio] class ZippedWithSignal[A, B, C](first: Signal[A], second: Signal[B], f: (A, B) => C)
    extends Signal[C] {
  override def get: Task[C]                  = first.get.zipWith(second.get)(f)
  override def changes: Stream[Throwable, C] = first.changes.zipLatestWith(second.changes)(f)
  override def map[D](g: C => D): Signal[D]  = new ZippedWithSignal[A, B, D](first, second, f(_, _) pipe g)
}
