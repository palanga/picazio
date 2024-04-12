package picazio

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import picazio.theme.Theme
import zio.*

trait ZIOWebApp {

  def root: Task[Shape[Any]]

  def theme: Theme = Theme.default

  implicit val runtime: Runtime[Theme] = PicazioRuntime.withTheme(theme)

  final def main(args0: Array[String]): Unit =
    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.runToFuture(start.logError("Unable to start application"))
    }

  private def start(implicit unsafe: Unsafe) =
    for {
      _            <- SplashScreen.print
      _            <- loadDOMContent
      mountingNode <- selectMountingNode
      shape        <- root
      laminarRoot  <- ZIO.attempt(new ShapeInterpreter().asLaminarElement(shape))
      _            <- ZIO.attempt(render(mountingNode, laminarRoot))
    } yield ()

  private def loadDOMContent: UIO[Unit] = ZIO.async { callback =>
    dom.document.addEventListener("DOMContentLoaded", (_: dom.Event) => callback(ZIO.unit))
  }

  private def selectMountingNode: Task[dom.Element] =
    ZIO
      .attempt(Option(dom.document.querySelector("#picazio-root")))
      .someOrFail(Error.MountingNodeNotFound)

}
