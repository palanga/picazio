package picazio.test

import org.scalajs.dom.html.*
import org.scalajs.dom.{Element, NodeList}

trait Selector {

  def selectAll(query: String): NodeList[org.scalajs.dom.Element]
  def childrenCount: Int
  def firstElementChild: Element

  def selectAllDivs: Seq[Div]              = selectAll("div").map(_.asInstanceOf[Div]).toSeq
  def selectDivWithText(text: String): Div = selectAllDivs.filter(_.textContent == text).head

  def selectAllSpans: Seq[Span]              = selectAll("span").map(_.asInstanceOf[Span]).toSeq
  def selectSpanWithText(text: String): Span = selectAllSpans.filter(_.textContent == text).head

  def selectAllButtons: Seq[Button]              = selectAll("button").map(_.asInstanceOf[Button]).toSeq
  def selectButtonWithText(text: String): Button = selectAllButtons.filter(_.textContent == text).head

  def selectAllInputs: Seq[Input]                     = selectAll("input").map(_.asInstanceOf[Input]).toSeq
  def selectInputWithPlaceholder(text: String): Input = selectAllInputs.filter(_.placeholder == text).head

}
