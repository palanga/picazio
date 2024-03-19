package picazio.test

import org.scalajs.dom.*
import zio.*

final case class SelectorFromNodeZIO(nodeId: String) extends SelectorZIO {

  override def root: Task[RenderedElement] =
    ZIO.attempt(document.getElementById(nodeId).firstElementChild).map(RenderedElement.fromDomElement)

}
