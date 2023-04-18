package picazio

import zio.*
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveElement
import org.scalajs.dom

trait WebApp {

  def root: Shape

  implicit val runtime: Runtime[Any] = Runtime.default

  final def main(args0: Array[String]): Unit = {

    zio.Unsafe.unsafe { implicit unsafe =>
      renderOnDomContentLoaded(
        org.scalajs.dom.document.querySelector("#picazio-root"),
        WebApp.toLaminar(root),
      )
    }

    ()
  }

}

object WebApp {

  // TODO or don't require an Unsafe here!
  def toLaminar(shape: Shape)(implicit runtime: Runtime[Any], unsafe: Unsafe): ReactiveElement[dom.Element] = {
    import com.raquo.laminar.api.L
    shape match {
      case Shape.Text(content)   => L.p(content)
      case Shape.Button(content) => L.button(content)
      case Shape.OnClick(action, inner) =>
        val runOnClick = L.onClick --> { _ => unsafeRunToFuture(action) }
        toLaminar(inner).amend(runOnClick)
    }
  }

  // TODO require an Unsafe here!!!
  private def unsafeRunToFuture[A](action: Task[A])(implicit runtime: Runtime[Any]): CancelableFuture[A] =
    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.runToFuture(action)
    }

}
