package picazio.test

import org.scalajs.dom.*
import zio.*

final case class SelectorFromNodeZIO(nodeId: String) extends SelectorZIO {
  override def renderedHtml: Task[String] = ZIO.attempt(document.getElementById(nodeId).innerHTML).delay(0.seconds)
}
