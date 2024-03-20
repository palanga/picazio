package picazio.style

final class Alignment private (private[picazio] val self: String) extends AnyVal {
  override def toString: String = self
}

object Alignment {
  val center = new Alignment("center")
  val start  = new Alignment("start")
  val end    = new Alignment("end")
}
