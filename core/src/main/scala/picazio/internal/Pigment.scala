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

  def rgbToHsl(rgb: RGB): HSL = {
    val RGB(red, green, blue) = rgb

    val maximum = max(red, max(green, blue)) // 220
    val minimum = min(red, min(green, blue)) // 126
    val chroma  = maximum - minimum          // 220 - 126 = 94

    val hue1: Double =
      if (chroma == 0) 0
      else if (maximum == red) ((green - blue).toDouble / chroma) % 6
      else if (maximum == green) ((blue - red).toDouble / chroma) + 2
      else ((red - green).toDouble / chroma) + 4 // 4.585

    val hue: Int = ((60f * hue1) % 360).toInt // 275

    val lightness: Double = (maximum + minimum).toDouble / 2.0 / 255.0 // 173

    val saturation: Double =
      if (lightness == 1 || lightness == 0) 0
      else chroma / (1 - abs(2 * lightness - 1)) / 256.0 //

    HSL(hue, (saturation * 100).round.toInt, (lightness * 100).round.toInt)
  }

}

object test extends App {
  val base    = Pigment.fromHexStringUnsafe("B57EDC")
  val hsl     = Pigment.rgbToHsl(base.asInstanceOf[Pigment.RGB])
  val lighter = base.lighter
  val darker  = base.darker

  println(base)
  println(hsl)
  println(lighter)
  println(darker)

}
