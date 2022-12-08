package examples.hello

import picazio.*
import zio.*

object HelloWorld:

  implicit val defaultRuntime: Runtime[Any] = Runtime.default

  def run(args: Array[String]): Unit =
    import com.raquo.laminar.api.L.*

    zio.Unsafe.unsafe { implicit unsafe =>
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#app"),
        Shape.text("hola").build(toLaminarModDefault),
      )
    }
