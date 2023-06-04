package picazio.test

import org.scalajs.dom.*

case class SelectorFromNode(nodeId: String) extends Selector {

  override def selectAll(query: String): NodeList[Element] =
    document.getElementById(nodeId).querySelectorAll(query)

  override def childrenCount: Int =
    document.getElementById(nodeId).childElementCount

  override def firstElementChild: Element =
    document.getElementById(nodeId).firstElementChild

  override def innerHtml: String = document.getElementById(nodeId).innerHTML

}
