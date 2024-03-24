package picazio.style

import java.lang.Math.*

final private[picazio] case class Pigment private (red: Int, green: Int, blue: Int) {

  def brighter: Pigment = new Pigment(min(red + 32, 255), min(green + 32, 255), min(blue + 32, 255))
  def darker: Pigment   = new Pigment(max(red - 32, 0), max(green - 32, 0), max(blue - 32, 0))

  override def toString: String = s"""rgb($red, $green, $blue)"""

}

private[picazio] object Pigment {

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
      case red :: green :: blue :: Nil => new Pigment(red, green, blue)
      case _                           => throw exception
    }

  }

  private def rgbToHue(pigment: Pigment): HSL = {
    val Pigment(red, green, blue) = pigment

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

final private[picazio] case class HSL private (hue: Int, saturation: Int, lightness: Int)
final private[picazio] case class RGB private (red: Int, green: Int, blue: Int)
