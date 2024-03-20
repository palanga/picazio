package picazio.style

final class Justification private (private[picazio] val self: String) extends AnyVal {
  override def toString: String = self
}

object Justification {
  val spaceBetween = new Justification("space-between")
}
