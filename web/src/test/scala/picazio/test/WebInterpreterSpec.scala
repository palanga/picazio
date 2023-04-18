package picazio.test

import com.raquo.laminar.api.L.render as renderLaminar
import org.scalajs.dom.*
import org.scalatest.funsuite.AnyFunSuite
import picazio.*
import zio.*

trait WebInterpreterSpec extends AnyFunSuite {

  type Assertion = String => Boolean

  implicit val runtime: Runtime[Any] = Runtime.default

  def testRender(testName: String)(f: (Renderer, Selector) => Boolean): Unit =
    test(testName)(assert(f(render(testName), SelectorFromNode(testName))))

  type Renderer = Shape => Unit

  def testRendered(testName: String)(shape: Shape)(assertion: Assertion): Unit = {
    render(testName)(shape)
    test(testName)(assert(assertion(testName)))
  }

  def assertRendered(predicate: Selector => Boolean)(testName: String): Boolean =
    predicate(SelectorFromNode(testName))

  private def render(parentId: String)(shape: Shape)(implicit runtime: Runtime[Any]): Unit = {

    val root = document.createElement("div")
    root.setAttribute("id", parentId)
    document.body.appendChild(root)

    zio.Unsafe.unsafe { implicit unsafe =>
      renderLaminar(
        root,
        WebApp.toLaminar(shape),
      )
    }
  }

}
