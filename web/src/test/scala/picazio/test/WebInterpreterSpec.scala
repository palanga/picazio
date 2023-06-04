package picazio.test

import com.raquo.laminar.api.L.render as renderLaminar
import org.scalajs.dom.*
import org.scalatest.Assertion
import org.scalatest.funsuite.AsyncFunSuite
import picazio.*
import picazio.style.Theme
import zio.*

import scala.concurrent.*

trait WebInterpreterSpec extends AsyncFunSuite {

  implicit override def executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  init

  implicit val runtime: Runtime[Theme] = Runtime.default.withEnvironment {
    ZEnvironment[Theme](Theme.default)
  }

  def testRender(testName: String)(f: (RendererUnsafe, Selector) => Boolean): Unit =
    test(testName)(assert(f(render(testName), SelectorFromNode(testId(testName)))))

  def testRenderZIO(testName: String)(f: (RendererUnsafe, Selector) => Task[Boolean]): Unit =
    testRenderFuture(testName)(unsafeRunToFuture(f))

  def testRenderZIOSafe(testName: String)(f: (RendererZIO, SelectorZIO) => Task[Assertion]): Unit =
    testRenderZIOSelectZIO(testName)(asRendererUnsafe(f))

  private def testRenderZIOSelectZIO(testName: String)(f: (RendererUnsafe, SelectorZIO) => Task[Assertion]): Unit =
    testRenderFutureZIO(testName)(unsafeRunToFutureSelectZIO(f))

  private def asRendererUnsafe(
    f: (RendererZIO, SelectorZIO) => Task[Assertion]
  ): (RendererUnsafe, SelectorZIO) => Task[Assertion] =
    (renderUnsafe, select) => f(shape => ZIO.attempt(renderUnsafe(shape)), select)

  private def testRenderFuture(testName: String)(f: (RendererUnsafe, Selector) => Future[Boolean]): Unit =
    test(testName)(f(render(testName), SelectorFromNode(testId(testName))).map(assert(_)))

  private def testRenderFutureZIO(testName: String)(f: (RendererUnsafe, SelectorZIO) => Future[Assertion]): Unit =
    test(testName)(f(render(testName), SelectorFromNodeZIO(testId(testName))))

  private def unsafeRunToFuture(
    f: (RendererUnsafe, Selector) => Task[Boolean]
  ): (RendererUnsafe, Selector) => CancelableFuture[Boolean] =
    (renderer, selector) =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture(f(renderer, selector))
      }

  private def unsafeRunToFutureSelectZIO(
    f: (RendererUnsafe, SelectorZIO) => Task[Assertion]
  ): (RendererUnsafe, SelectorZIO) => CancelableFuture[Assertion] =
    (renderer, selector) =>
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture(f(renderer, selector))
      }

  type RendererUnsafe = Shape => Unit
  type RendererZIO    = Shape => Task[Unit]

  private def init = {
    val suiteRootList    = document.createElement("dl")
    val suiteNameElement = document.createElement("h2")
    suiteNameElement.textContent = suiteClassSimpleName
    suiteRootList.appendChild(suiteNameElement)
    suiteRootList.setAttribute("id", suiteClassName)
    suiteRootList.setAttribute("style", """margin: 0; padding: 16""")
    document.body.appendChild(suiteRootList)
  }

  private def suiteClassName           = this.getClass.getName
  private def suiteClassSimpleName     = this.getClass.getSimpleName
  private def testId(testName: String) = s"$suiteClassName: $testName"

  private def render(testName: String)(shape: Shape): Unit = {

    val testTitle = document.createElement("dt")
    testTitle.textContent = testName
    testTitle.setAttribute("style", """margin-top: 18; margin-bottom: 6""")
    document.getElementById(suiteClassName).appendChild(testTitle)

    val testRoot = document.createElement("dd")
    testRoot.setAttribute("id", testId(testName))
    testRoot.setAttribute("style", """display: flex; border: solid; border-width: 10; border-color: lightgray""")
    document.getElementById(suiteClassName).appendChild(testRoot)

    Unsafe.unsafe { implicit unsafe =>
      renderLaminar(
        testRoot,
        new ShapeInterpreter().asLaminarElement(shape),
      )
    }

  }

}
