package examples

import picazio.*
import zio.*

object hola:

  implicit val defaultRuntime: Runtime[Any] = Runtime.default

  val root = Shape.text("hola")

  def main(args: Array[String]): Unit =
//  def run(args: Array[String]): Unit =
    import com.raquo.laminar.api.L.*

    zio.Unsafe.unsafe { implicit unsafe =>
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#app"),
        root.build(toLaminarModDefault),
      )
    }
