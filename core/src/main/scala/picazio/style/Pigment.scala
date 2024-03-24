package picazio.style

import java.lang.Math.*

final class Pigment(red: Int, green: Int, blue: Int) {

  def brighter: Pigment = new Pigment(min(red + 32, 255), min(green + 32, 255), min(blue + 32, 255))
  def darker: Pigment   = new Pigment(max(red - 32, 0), max(green - 32, 0), max(blue - 32, 0))

  override def toString: String = s"""rgb($red, $green, $blue)"""

}

object Pigment {

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

}
