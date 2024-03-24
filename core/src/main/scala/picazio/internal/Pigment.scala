package picazio.internal

import java.lang.Math.*

sealed private[picazio] trait Pigment {
  def lighter: Pigment
  def darker: Pigment
}

private[picazio] object Pigment {

  final case class RGB(red: Int, green: Int, blue: Int) extends Pigment {
    override def lighter: Pigment = rgbToHsl(this).lighter
    override def darker: Pigment  = rgbToHsl(this).darker
    override def toString: String = s"""rgb($red, $green, $blue)"""
  }

  final case class HSL(hue: Int, saturation: Int, lightness: Int) extends Pigment {
    override def lighter: Pigment = this.copy(lightness = this.lightness + 10)
    override def darker: Pigment  = this.copy(lightness = this.lightness - 10)
    override def toString: String = s"""hsl($hue, $saturation%, $lightness%)"""
  }

  def fromHexStringUnsafe(input: String): Pigment = {

    val strippedInput =
      if (input.startsWith("#")) input.substring(1)
      else if (input.startsWith("0x")) input.substring(2)
      else input

    def hasOnlyValidChars = (strippedInput.toLowerCase.toSet -- "0123456789abcdef".toSet).isEmpty
    def exception         = new IllegalArgumentException(s"<<$input>> doesn't look like an hex color.")

    if (strippedInput.length != 6) throw exception
    if (!hasOnlyValidChars) throw exception
    strippedInput.sliding(2, 2).map(Integer.parseInt(_, 16)).toList match {
      case red :: green :: blue :: Nil => RGB(red, green, blue)
      case _                           => throw exception
    }

  }

  private def rgbToHsl(rgb: RGB): HSL = {
    val RGB(red, green, blue) = rgb

    val maximum = max(red, max(green, blue))
    val minimum = min(red, min(green, blue))
    val chroma  = maximum - minimum

    val hue1: Int =
      if (chroma == 0) 0
      else if (maximum == red) ((green - blue) / chroma) % 6
      else if (maximum == green) ((blue - red) / chroma) + 2
      else ((red - green) / chroma) + 4

    val hue: Int = (60 * hue1) % 360

    val lightness = (maximum + minimum) / 2

    val saturation =
      if (lightness == 1 || lightness == 0) 0
      else chroma / (1 - abs(2 * lightness - 1))

    HSL(hue, saturation, lightness)
  }

}
