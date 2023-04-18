package picazio.test

import org.scalajs.dom.*

trait Selector {
  def selectAll(query: String): NodeList[Element]
}
