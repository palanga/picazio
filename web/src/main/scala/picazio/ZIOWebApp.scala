package picazio

import com.raquo.laminar.api.L.*
import picazio.style.Theme
import zio.*

import scala.concurrent.ExecutionContext

trait ZIOWebApp {

  def root: Task[Shape]

  implicit val runtime: Runtime[Theme] = Runtime.default.withEnvironment {
    ZEnvironment[Theme](Theme.default)
  }

  implicit private def executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  final def main(args0: Array[String]): Unit =
    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.runToFuture(root.logError).map { shape =>
        render(
          org.scalajs.dom.document.querySelector("#picazio-root"),
          new ShapeInterpreter().asLaminarElement(shape),
        )
      }
    }

}
