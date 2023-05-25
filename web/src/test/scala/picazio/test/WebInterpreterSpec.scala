package picazio.test

import com.raquo.laminar.api.L.render as renderLaminar
import org.scalajs.dom.*
import org.scalatest.funsuite.AsyncFunSuite
import picazio.*
import zio.*

import scala.concurrent.*

trait WebInterpreterSpec extends AsyncFunSuite {

  implicit override def executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  init

  implicit val runtime: Runtime[Any] = Runtime.default

  def testRender(testName: String)(f: (Renderer, Selector) => Boolean): Unit =
    test(testName)(assert(f(render(testName), SelectorFromNode(testName))))

  def testRenderZIO(testName: String)(f: (Renderer, Selector) => Task[Boolean]): Unit =
    testRenderFuture(testName)(unsafeRunToFuture(f))

  private def testRenderFuture(testName: String)(f: (Renderer, Selector) => Future[Boolean]): Unit =
    test(testName)(f(render(testName), SelectorFromNode(testName)).map(assert(_)))

  private def unsafeRun(f: (Renderer, Selector) => Task[Boolean]): (Renderer, Selector) => Boolean =
    (renderer, selector) =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.run(f(renderer, selector)).getOrThrow()
      }

  private def unsafeRunToFuture(
    f: (Renderer, Selector) => Task[Boolean]
  ): (Renderer, Selector) => CancelableFuture[Boolean] =
    (renderer, selector) =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture(f(renderer, selector))
      }

  type Renderer = Shape => Unit

  private def init = {
    val suiteRootList = document.createElement("dl")
    suiteRootList.setAttribute("id", "suite root list")
    document.body.appendChild(suiteRootList)
  }

  private def render(parentId: String)(shape: Shape): Unit = {

    val testTitle = document.createElement("dt")
    testTitle.textContent = parentId
    testTitle.setAttribute("style", """margin-top: 18; margin-bottom: 6""")
    document.getElementById("suite root list").appendChild(testTitle)

    val root = document.createElement("dt")
    root.setAttribute("id", parentId)
    root.setAttribute("style", """border: solid; border-width: 10; border-color: lightgray""")
    document.getElementById("suite root list").appendChild(root)

    Unsafe.unsafe { implicit unsafe =>
      renderLaminar(
        root,
        WebInterpreter.toLaminar(shape),
      )
    }

  }

}
