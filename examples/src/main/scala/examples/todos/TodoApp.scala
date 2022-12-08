package examples.todos

import picazio.*
import zio.*

object TodoApp:

  implicit val defaultRuntime: Runtime[Any] = Runtime.default

  def main(args: Array[String]): Unit =
    import com.raquo.laminar.api.L.*

    zio.Unsafe.unsafe { implicit unsafe =>
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#app"),
        views.Root(State).build(toLaminarModDefault),
      )
    }
