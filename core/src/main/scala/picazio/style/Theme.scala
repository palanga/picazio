package picazio.style

case class Theme(
  sizeMultiplier: Double,
  typography: String,
  colorPalette: ColorPalette,
)

object Theme {
  val default: Theme = Theme(1, "system-ui", ColorPalette.default)
}
