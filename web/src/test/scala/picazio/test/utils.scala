package picazio.test

import org.scalajs.dom.html.Input
import org.scalajs.dom.{ Event, HTMLElement }
import zio.*
import zio.stream.*

object utils {

  def inputText(input: Input, text: String): Task[Unit] = ZIO.attempt {
    input.value = text
    input.dispatchEvent(new Event("input"))
    ()
  }

  def inputTextAndWait(input: Input, text: String, ref: SubscriptionRef[?]): Task[Unit] =
    for {
      _ <- ZIO.attempt(input.value = text)
      _ <- ZIO.attempt(input.dispatchEvent(new Event("input")))
      _ <- ref.changes.runHead
    } yield ()

  def clickAndWait(button: HTMLElement, ref: SubscriptionRef[?]): Task[Unit] =
    for {
      _ <- ZIO.attempt(button.click())
      _ <- ref.changes.runHead
    } yield ()

}
