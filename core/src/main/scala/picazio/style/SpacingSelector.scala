package picazio.style

trait SpacingSelector {
  val none         = 0
  val smallest     = 1
  val smallestPlus = 2
  val smallMinus   = 3
  val small        = 4
  val mediumSmall  = 6
  val medium       = 8
  val mediumLarge  = 12
  val large        = 16
  val largeExtra   = 24
  val largest      = 32
}

object SpacingSelector {
  val default: SpacingSelector = new SpacingSelector {}
}
