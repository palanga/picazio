package picazio.test

import org.scalajs.dom.*
import org.scalajs.dom.html.Input
import zio.*

class RenderedElement(self: Element) extends Iterable[RenderedElement] {

  def styles: RenderedStyleSet = RenderedElement.parseStyles(self)

  def tag: String = self.tagName.toLowerCase

  def text: String = self match {
    case input: Input => input.value
    case _            => self.innerText
  }

  def write(text: String): Task[Unit] =
    ZIO.attempt {
      self.asInstanceOf[Input].value = text
      self.dispatchEvent(new Event("input"))
      ()
    }

  def click: Task[Unit] = ZIO.attempt(self.asInstanceOf[HTMLElement].click())

  def children: List[RenderedElement] = self.children.toList.map(RenderedElement.fromDomElement)

  def debug: RenderedElement = {
    println(self.outerHTML)
    this
  }

  override def iterator: Iterator[RenderedElement] = this.children.iterator

}

object RenderedElement {

  def fromDomElement(self: Element): RenderedElement = new RenderedElement(self)

  private def parseStyles(self: Element): RenderedStyleSet =
    RenderedStyleSet(
      self.getAttribute("style")
        .split(";")
        .map(parseStyleKeyValue)
        .toIndexedSeq*
    )

  private def parseStyleKeyValue(input: String): (String, String) =
    input.split(":").map(_.trim).toList match {
      case key :: value :: Nil => key -> value
      case _                   => throw new ParsingException(s"Couldn't parse <<$input>> as a style key-value pair")
    }

}

class ParsingException(message: String) extends Exception(message)
