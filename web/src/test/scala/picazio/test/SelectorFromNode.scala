package picazio.test

import org.scalajs.dom.*

case class SelectorFromNode(nodeId: String) extends Selector {
  override def selectAll(query: String): NodeList[Element] =
    document.getElementById(nodeId).querySelectorAll(query)
}
