package picazio

import zio.*
import com.raquo.laminar.api.L.renderOnDomContentLoaded

trait WebApp {

  def root: Shape

  implicit val runtime: Runtime[Any] = Runtime.default

  final def main(args0: Array[String]): Unit =
    Unsafe.unsafe { implicit unsafe =>
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#picazio-root"),
        WebInterpreter.toLaminar(root),
      )
    }

}
