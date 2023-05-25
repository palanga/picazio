package picazio

import zio.*
import zio.stream.*

trait Signal[A] {
  def get: ZIO[Any, Nothing, A]
  def changes: ZStream[Any, Nothing, A]
  def map[B](f: A => B): Signal[B]

  def debug(prefix: String): ZIO[Any, Nothing, A]               = get.debug(prefix)
  def tap(f: A => ZIO[Any, Nothing, Any]): ZIO[Any, Nothing, A] = get.tap(f)
}

object Signal {
  def fromRef[A](ref: SubscriptionRef[A]) = new SingleSignal(ref)
}

private[picazio] final class SingleSignal[A](underlying: SubscriptionRef[A]) extends Signal[A] {
  override def get: ZIO[Any, Nothing, A]         = underlying.get
  override def changes: ZStream[Any, Nothing, A] = underlying.changes
  override def map[B](f: A => B): Signal[B]      = new MappedSignal(underlying, f)
}

private[picazio] final class MappedSignal[A, B](underlying: SubscriptionRef[A], f: A => B) extends Signal[B] {
  override def get: ZIO[Any, Nothing, B]         = underlying.get.map(f)
  override def changes: ZStream[Any, Nothing, B] = underlying.changes.map(f)
  override def map[C](f: B => C): Signal[C]      = new MappedSignal(underlying, f compose this.f)
}
