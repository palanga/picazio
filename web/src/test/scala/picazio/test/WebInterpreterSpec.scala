package picazio.test

import com.raquo.laminar.api.L.render as renderLaminar
import org.scalajs.dom.*
import org.scalatest.Assertion
import org.scalatest.funsuite.AsyncFunSuite
import picazio.*
import picazio.theme.Theme
import zio.*

import scala.concurrent.*

trait WebInterpreterSpec extends AsyncFunSuite {

  implicit override def executionContext: ExecutionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  init

  implicit val runtime: Runtime[Theme] = Runtime.default.withEnvironment {
    ZEnvironment[Theme](Theme.default)
  }

  def testShape(testName: String)(f: Renderer => Task[Assertion]): Unit =
    test(testName)(
      Unsafe.unsafe { implicit unsafe =>
        runtime.unsafe.runToFuture(f(render(testName)(_).logError(s"Error rendering test <<$testName>>")))
      }
    )

  def debounce[A](task: => A): Task[A] = ZIO.attempt(task).delay(1.nanosecond)
  def debounce: Task[Unit]             = ZIO.unit.delay(1.nanosecond)

  type Renderer = Shape[Any] => Task[RenderedElement]

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

  private def render(testName: String)(shape: Shape[Any])(implicit unsafe: Unsafe): Task[RenderedElement] =
    ZIO.attempt {

      val testTitle = document.createElement("dt")
      testTitle.textContent = testName
      testTitle.setAttribute("style", """margin-top: 18; margin-bottom: 6""")
      document.getElementById(suiteClassName).appendChild(testTitle)

      val link = document.createElement("link")
      link.setAttribute("href", "https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght@100")
      link.setAttribute("rel", "stylesheet")
      document.getElementById(suiteClassName).appendChild(link)

      val testRoot = document.createElement("dd")
      testRoot.setAttribute("id", testId(testName))
      testRoot.setAttribute("style", """display: flex; border: solid; border-width: 10; border-color: lightgray""")
      document.getElementById(suiteClassName).appendChild(testRoot)

      renderLaminar(
        testRoot,
        new ShapeInterpreter().asLaminarElement(shape),
      )

      RenderedElement.fromDomElement(document.getElementById(testId(testName)).firstElementChild)

    }

}
