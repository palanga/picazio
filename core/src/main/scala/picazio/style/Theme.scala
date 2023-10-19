package picazio.style

case class Theme(
  sizeMultiplier: Double,
  typography: String,
)

object Theme {
  val default: Theme = Theme(1, "system-ui")
}
