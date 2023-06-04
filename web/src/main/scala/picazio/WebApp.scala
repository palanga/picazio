package picazio

import zio.*
import com.raquo.laminar.api.L.renderOnDomContentLoaded
import picazio.style.Theme

trait WebApp {

  def root: Shape

  implicit val runtime: Runtime[Theme] = Runtime.default.withEnvironment {
    ZEnvironment[Theme](Theme.default)
  }

  final def main(args0: Array[String]): Unit =
    Unsafe.unsafe { implicit unsafe =>
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#picazio-root"),
        new ShapeInterpreter().asLaminarElement(root),
      )
    }

}
