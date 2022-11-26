package examples

//import picazio.*
//import zio.*
//
//object todos:
//
//  implicit val defaultRuntime: Runtime[Any] = Runtime.default
//
//  val root = Shape.text("hola")
//
//  def main(args: Array[String]): Unit =
//    import com.raquo.laminar.api.L.*
//
//    zio.Unsafe.unsafe { implicit unsafe =>
//      renderOnDomContentLoaded(
//        org.scalajs.dom.document.querySelector("#app"),
//        root.build(toLaminarModDefault),
//      )
//    }

//object TutorialApp {
//  def main(args: Array[String]): Unit = {
//    println("Hello world!")
//  }
//}

import org.scalajs.dom
import org.scalajs.dom.document
import zio.*
import zio.Clock.*

import java.util.concurrent.TimeUnit

object Main extends ZIOAppDefault {

  override def run =
    for {
      _ <- Console.printLine("Starting progress bar demo.") // Outputs on browser console log.
      rootNode <- ZIO.succeed(document.createElement("div"))
      _ <- ZIO.succeed(rootNode.setAttribute("id", "app"))
      _ <- ZIO.succeed(document.getElementsByTagName("body").item(0).appendChild(rootNode))
      _ <- update(rootNode).repeat(Schedule.spaced(1.seconds))
    } yield ExitCode.success

  def update(target: dom.Element) =
    for {
      time <- currentTime(TimeUnit.SECONDS)
      output = progressBar((time % 11).toInt, 10)
      _ <- ZIO.succeed(target.innerHTML = output)
    } yield ()

  def progressBar(tick: Int, size: Int): String = {
    val bar_length = tick
    val empty_length = size - tick
    val bar = "#" * bar_length + " " * empty_length
    s"$bar $bar_length%"
  }

}
