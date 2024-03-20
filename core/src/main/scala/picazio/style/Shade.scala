package picazio.style

sealed trait Shade

object Shade {

  // TODO use the following lightness values:
  case object Lightest        extends Shade // 100
  case object UltraLight      extends Shade // 99
  case object Lighter         extends Shade // 95
  case object Light           extends Shade // 90
  case object ModeratelyLight extends Shade // 80
  case object SlightlyLight   extends Shade // 70
  case object Regular         extends Shade // 60
  case object SlightlyDark    extends Shade // 50
  case object ModeratelyDark  extends Shade // 40
  case object Dark            extends Shade // 30
  case object Darker          extends Shade // 20
  case object UltraDark       extends Shade // 10
  case object Darkest         extends Shade // 0

  case class Lighten(percent: Int) extends Shade
  case class Darken(percent: Int)  extends Shade

}
