package picazio.style

import java.lang.Math.*

final class Pigment(red: Int, green: Int, blue: Int) {
  def brighter: Pigment = new Pigment(min(red + 32, 255), min(green + 32, 255), min(blue + 32, 255))
  def darker: Pigment   = new Pigment(max(red - 32, 0), max(green - 32, 0), max(blue - 32, 0))

  override def toString: String = s"""rgb($red, $green, $blue)"""
}

object Pigment {

  def decode(input: String): Pigment = {
    val strippedInput =
      if (input.startsWith("#")) input.substring(1)
      else if (input.startsWith("0x")) input.substring(2)
      else input

    val validChars = "0123456789abcdef"
    if (strippedInput.length != 6) ???
    if (strippedInput.toLowerCase.exists(character => !validChars.contains(character))) ???
    strippedInput.sliding(2, 2).map(Integer.parseInt(_, 16)).toList match {
      case red :: green :: blue :: Nil => new Pigment(red, green, blue)
      case _                           => ???
    }
  }

}

object test extends App {

  val color: Pigment = Pigment.decode("624ad9")

  println(color)
  println(color.brighter)
  println(color.brighter.brighter)
  println(color.darker)
  println(color.darker.darker)

}
