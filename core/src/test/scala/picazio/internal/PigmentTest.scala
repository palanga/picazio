package picazio.internal

import org.scalatest.funsuite.AnyFunSuiteLike
import org.scalatest.matchers.should.Matchers

class PigmentTest extends AnyFunSuiteLike with Matchers {

  // pantone 7481 -> 00B74F
  private val verdeRgb = Pigment.RGB(0, 183, 79)
  private val verdeHsl = Pigment.HSL(146, 100, 36)

  test("pigment from hex string lower case with numeral char") {
    Pigment.fromHexStringUnsafe("#00b74f") match {
      case rgb: Pigment.RGB =>
        rgb shouldBe verdeRgb
        Pigment.rgbToHsl(rgb) shouldBe verdeHsl
      case _                => ??? // shouldn't happen
    }
  }

  test("pigment from hex string upper case with numeral char") {
    Pigment.fromHexStringUnsafe("#00B74F") match {
      case rgb: Pigment.RGB =>
        rgb shouldBe verdeRgb
        Pigment.rgbToHsl(rgb) shouldBe verdeHsl
      case _                => ??? // shouldn't happen
    }
  }

  test("pigment from hex string lower case with 0x") {
    Pigment.fromHexStringUnsafe("0x00b74f") match {
      case rgb: Pigment.RGB =>
        rgb shouldBe verdeRgb
        Pigment.rgbToHsl(rgb) shouldBe verdeHsl
      case _                => ??? // shouldn't happen
    }
  }

  test("pigment from hex string lower case with no 0x or # beginning") {

    Pigment.fromHexStringUnsafe("00b74f") match {
      case rgb: Pigment.RGB =>
        rgb shouldBe verdeRgb
        Pigment.rgbToHsl(rgb) shouldBe verdeHsl
      case _                => ??? // shouldn't happen
    }
  }

  test("pigment from invalid hex string throws") {
    assertThrows[IllegalArgumentException](Pigment.fromHexStringUnsafe("00b74t"))
    assertThrows[IllegalArgumentException](Pigment.fromHexStringUnsafe("00b74"))
  }

  test("lighter should increase lightness by 10%") {
    verdeRgb.lighter shouldBe Pigment.HSL(146, 100, 46)
  }

  test("lighter should increase lightness until 100%") {
    verdeRgb.lighter.lighter.lighter.lighter.lighter.lighter.lighter shouldBe Pigment.HSL(146, 100, 100)
  }

  test("darker should decrease lightness by 10%") {
    verdeRgb.darker shouldBe Pigment.HSL(146, 100, 26)
  }

  test("darker should decrease lightness until 0%") {
    verdeRgb.darker.darker.darker.darker shouldBe Pigment.HSL(146, 100, 0)
  }

  test("rgb to string") {
    verdeRgb.toString shouldBe "rgb(0, 183, 79)"
  }

  test("hsl to string") {
    verdeHsl.toString shouldBe "hsl(146, 100%, 36%)"
  }

}
