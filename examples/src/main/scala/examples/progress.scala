package examples

import org.scalajs.dom
import org.scalajs.dom.document
import zio.*
import zio.Clock.*

import java.io.IOException
import java.util.concurrent.TimeUnit

object progress /*extends ZIOAppDefault*/ {

  def run: ZIO[Any, IOException, ExitCode] =
    for {
      _        <- Console.printLine("Starting progress bar demo.") // Outputs on browser console log.
      rootNode <- ZIO.succeed(document.createElement("div"))
      _        <- ZIO.succeed(rootNode.setAttribute("id", "app"))
      _        <- ZIO.succeed(document.getElementsByTagName("body").item(0).appendChild(rootNode))
      _        <- update(rootNode).repeat(Schedule.spaced(1.seconds))
    } yield ExitCode.success

  private def update(target: dom.Element) =
    for {
      time  <- currentTime(TimeUnit.SECONDS)
      output = progressBar((time % 11).toInt, 10)
      _     <- ZIO.succeed(target.innerHTML = output)
    } yield ()

  private def progressBar(tick: Int, size: Int): String = {
    val bar_length   = tick
    val empty_length = size - tick
    val bar          = "#" * bar_length + " " * empty_length
    s"$bar $bar_length%"
  }

}
