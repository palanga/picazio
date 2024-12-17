package picazio.style

sealed trait Size {
  def *(multiplier: Double): Size
}

object Size {

  private[picazio] case class Units(self: Double) extends Size {
    override def *(multiplier: Double): Size = Size.Units(self * multiplier)
  }

  private[picazio] case class Percent(self: Int) extends Size {
    override def *(multiplier: Double): Size = this
  }

  def custom(value: Double) = Size.Units(value)
  val none                  = Size.Units(0)

  def percent(i: Int) = Size.Percent(i)

  val smallest     = Size.Units(1)
  val smallestPlus = Size.Units(2)
  val smallMinus   = Size.Units(3)
  val small        = Size.Units(4)
  val mediumSmall  = Size.Units(6)
  val medium       = Size.Units(8)
  val mediumLarge  = Size.Units(12)
  val large        = Size.Units(16)
  val largeExtra   = Size.Units(24)
  val largest      = Size.Units(32)
}
