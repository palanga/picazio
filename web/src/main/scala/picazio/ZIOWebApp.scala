package picazio

import com.raquo.laminar.api.L.renderOnDomContentLoaded
import picazio.style.Theme
import zio.*

trait ZIOWebApp {

  def root: Task[Shape]

  implicit val runtime: Runtime[Theme] = Runtime.default.withEnvironment {
    ZEnvironment[Theme](Theme.default)
  }

  final def main(args0: Array[String]): Unit =
    Unsafe.unsafe { implicit unsafe =>
      val shape: Shape = runtime.unsafe.run(root).getOrThrow()
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#picazio-root"),
        new ShapeInterpreter().asLaminarElement(shape),
      )
    }

}
