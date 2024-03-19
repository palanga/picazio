package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class TextInputWebAppTest extends WebInterpreterSpec with Matchers {

  testShape("echo text input") { render =>

    def textInputWithEcho(textRef: SubscriptionRef[String]) =
      Shape.column(
        Shape.textInput("start typing...", textRef),
        Shape.text(textRef.signal),
      )

    for {
      textRef     <- SubscriptionRef.make("")
      root        <- render(textInputWithEcho(textRef))
      input        = root.head
      echoTextSpan = root.tail.head
      emptyText    = echoTextSpan.text
      _           <- input.write("hola")
      _           <- debounce
      echoedText   = echoTextSpan.text
    } yield {
      emptyText shouldBe empty
      echoedText shouldBe "hola"
    }

  }

  /**
   * TODO: When editing, the caret goes to the end of the input every time a key
   * is pressed. This bug doesn't happen in the centimeters/inches test, where
   * we build both inputs from signals instead of just refs
   */
  testShape("reverse echo") { render =>

    def textInputWithReversedEcho(textRef: SubscriptionRef[String], textRefReversed: SubscriptionRef[String]) =
      Shape.column(
        Shape
          .textInput("start typing...", textRef)
          .onInput(text => textRefReversed.setAsync(text.reverse)),
        Shape
          .textInput("start typing in reverse...", textRefReversed)
          .onInput(reversedText => textRef.setAsync(reversedText.reverse)),
      )

    for {
      textRef         <- SubscriptionRef.make("")
      textRefReversed <- SubscriptionRef.make("")
      root            <- render(textInputWithReversedEcho(textRef, textRefReversed))
      input            = root.head
      inputReversed    = root.tail.head
      _               <- input.write("hola")
      _               <- debounce
      text             = input.text
      textReversed     = inputReversed.text
      _               <- inputReversed.write("neuquen")
      _               <- debounce
      neuquen          = input.text
      neuquenReversed  = inputReversed.text
    } yield {
      text shouldBe "hola"
      textReversed shouldBe "aloh"
      neuquen shouldBe "neuquen"
      neuquenReversed shouldBe "neuquen"
    }

  }

  testShape("two different inputs that reads from a signal and edit each other") { render =>

    def inchesToCentimetersConverter(inchesRef: SubscriptionRef[Double], centimetersRef: SubscriptionRef[Double]) = {

      def handleInchesInput(input: String): Task[Unit] =
        for {
          inches     <- ZIO.fromOption(input.toDoubleOption).unsome.someOrFailException
          centimeters = inchesToCentimeters(inches)
          _          <- centimetersRef.setAsync(centimeters)
        } yield ()

      def handleCentimetersInput(input: String): Task[Unit] =
        for {
          centimeters <- ZIO.fromOption(input.toDoubleOption).unsome.someOrFailException
          inches       = centimetersToInches(centimeters)
          _           <- inchesRef.setAsync(inches)
        } yield ()

      Shape.column(
        Shape
          .textInput("centimeters", centimetersRef.signal.map(_.toString))
          .onInput(handleCentimetersInput),
        Shape
          .textInput("inches", inchesRef.signal.map(_.toString))
          .onInput(handleInchesInput),
      )
    }

    def inchesToCentimeters(inches: Double): Double      = inches * 2.54
    def centimetersToInches(centimeters: Double): Double = centimeters / 2.54

    for {
      inchesRef            <- SubscriptionRef.make(0.0)
      centimetersRef       <- SubscriptionRef.make(0.0)
      root                 <- render(inchesToCentimetersConverter(inchesRef, centimetersRef))
      centimeters           = root.head
      inches                = root.tail.head
      _                    <- centimeters.write("254")
      _                    <- debounce
      inches_100            = inches.text
      _                    <- inches.write("1")
      _                    <- debounce
      centimeters_2_54      = centimeters.text
      _                    <- inches.write("invalid number")
      _                    <- debounce
      centimetersNotUpdated = centimeters.text
    } yield {
      inches_100 shouldBe "100"
      centimeters_2_54 shouldBe "2.54"
      centimetersNotUpdated shouldBe centimeters_2_54
    }
  }

  testShape("only numbers text input") { render =>

    def numbersInputWithEcho(inputState: SubscriptionRef[String]) =
      Shape.column(
        Shape.textInput("numbers", inputState).onInputFilter(_.toIntOption.isDefined),
        Shape.text(inputState.signal),
      )

    for {
      inputState               <- SubscriptionRef.make("")
      root                     <- render(numbersInputWithEcho(inputState))
      numbersInput              = root.head
      numbersEcho               = root.tail.head
      _                        <- numbersInput.write("107")
      _                        <- debounce
      number_107                = numbersInput.text
      number_107_echo           = numbersEcho.text
      _                        <- numbersInput.write("invalid number")
      _                        <- debounce
      number_107_unchanged      = numbersInput.text
      number_107_echo_unchanged = numbersEcho.text
    } yield {
      number_107 shouldBe "107"
      number_107_echo shouldBe "107"
      number_107_unchanged shouldBe "107"
      number_107_echo_unchanged shouldBe "107"
    }
  }

}
