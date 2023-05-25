package picazio

import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.html.Input
import picazio.test.*
import zio.*
import zio.stream.*

class TextInputWebAppTest extends WebInterpreterSpec {

  testRenderZIO("echo text input") { (render, select) =>

    def textInputWithEcho(textRef: SubscriptionRef[String]) =
      Shape.column(
        Shape.textInput("start typing...", textRef),
        Shape.text(textRef.signal),
      )

    for {
      textRef      <- SubscriptionRef.make("")
      _            <- ZIO.attempt(render(textInputWithEcho(textRef)))
      input        <- ZIO.attempt(select.selectInputWithPlaceholder("start typing..."))
      echoTextSpan <- ZIO.attempt(select.selectSpanWithText(""))
      emptyText     = echoTextSpan.textContent
      _            <- ZIO.attempt(inputText(input, "hola"))
      _            <- textRef.changes.runHead
      echoedText    = echoTextSpan.textContent
    } yield emptyText == "" && echoedText == "hola"

  }

  testRenderZIO("reverse echo") { (render, select) =>

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
      _               <- ZIO.attempt(render(textInputWithReversedEcho(textRef, textRefReversed)))
      input           <- ZIO.attempt(select.selectInputWithPlaceholder("start typing..."))
      inputReversed   <- ZIO.attempt(select.selectInputWithPlaceholder("start typing in reverse..."))
      _               <- ZIO.attempt(inputText(input, "hola"))
      _               <- textRef.changes.runHead
      _               <- textRefReversed.changes.runHead
      text             = input.value
      textReversed     = inputReversed.value
      _               <- ZIO.attempt(inputText(inputReversed, "neuquen"))
      _               <- textRef.changes.runHead
      _               <- textRefReversed.changes.runHead
      neuquen          = input.value
      neuquenReversed  = inputReversed.value
    } yield text == "hola" && textReversed == "aloh" && neuquen == "neuquen" && neuquenReversed == "neuquen"

  }

  testRenderZIO("two different inputs that reads from a signal and edit each other") { (render, select) =>

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
      _                    <- ZIO.attempt(render(inchesToCentimetersConverter(inchesRef, centimetersRef)))
      centimeters          <- ZIO.attempt(select.selectInputWithPlaceholder("centimeters"))
      inches               <- ZIO.attempt(select.selectInputWithPlaceholder("inches"))
      _                    <- ZIO.attempt(inputText(centimeters, "254"))
      _                    <- inchesRef.changes.runHead
      _                    <- centimetersRef.changes.runHead
      inches_100            = inches.value
      _                    <- ZIO.attempt(inputText(inches, "1"))
      _                    <- inchesRef.changes.runHead
      _                    <- centimetersRef.changes.runHead
      centimeters_2_54      = centimeters.value
      _                    <- ZIO.attempt(inputText(inches, "invalid number"))
      _                    <- inchesRef.changes.runHead
      _                    <- centimetersRef.changes.runHead
      centimetersNotUpdated = centimeters.value
    } yield inches_100 == "100" && centimeters_2_54 == "2.54" && centimetersNotUpdated == centimeters_2_54
  }

  testRenderZIO("only numbers text input") { (render, select) =>

    def numbersInputWithEcho(inputState: SubscriptionRef[String]) =
      Shape.column(
        Shape.textInput("numbers", inputState).onInputFilter(_.toIntOption.isDefined),
        Shape.text(inputState.signal),
      )

    for {
      inputState               <- SubscriptionRef.make("")
      _                        <- ZIO.attempt(render(numbersInputWithEcho(inputState)))
      numbersInput             <- ZIO.attempt(select.selectInputWithPlaceholder("numbers"))
      numbersEcho              <- ZIO.attempt(select.selectSpanWithText(""))
      _                        <- ZIO.attempt(inputText(numbersInput, "107"))
      _                        <- inputState.changes.runHead
      number_107                = numbersInput.value
      number_107_echo           = numbersEcho.textContent
      _                        <- ZIO.attempt(inputText(numbersInput, "invalid number"))
      _                        <- inputState.changes.runHead
      number_107_unchanged      = numbersInput.value
      number_107_echo_unchanged = numbersEcho.textContent
    } yield number_107 == "107"
      && number_107_echo == "107"
      && number_107_unchanged == "107"
      && number_107_echo_unchanged == "107"
  }

  private def inputText(input: Input, text: String): Unit = {
    input.value = text
    input.dispatchEvent(new Event("input"));
  }

}
