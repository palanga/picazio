package picazio.style

final class Size private (val self: Double) extends AnyVal {
  def *(multiplier: Double): Size = new Size(self * multiplier)
  override def toString: String   = self.toString
}

object Size {
  def custom(value: Double) = new Size(value)
  val none                  = new Size(0)
  val smallest              = new Size(1)
  val smallestPlus          = new Size(2)
  val smallMinus            = new Size(3)
  val small                 = new Size(4)
  val mediumSmall           = new Size(6)
  val medium                = new Size(8)
  val mediumLarge           = new Size(12)
  val large                 = new Size(16)
  val largeExtra            = new Size(24)
  val largest               = new Size(32)
}
