package picazio

import com.raquo.laminar.api.L.Signal
import picazio.Shape.*
import zio.*

import scala.annotation.targetName

class Text[-R](text: String | Signal[String], attributes: List[Attribute[R]] = Nil) extends Shape(attributes):

  override def build(toLaminarMod: (=> Shape[R]) => Attribute[R] => LaminarMod): LaminarElem =
    import com.raquo.laminar.api.*
    val mods = attributes map toLaminarMod(this)
    this.text match {
      case t: String         => L.div(t, mods)
      case s: Signal[String] => L.div(L.child.text <-- s, mods)
    }

  override def addAttribute[R1](attribute: Attribute[R1]): Text[R & R1] = Text(text, attribute :: attributes)

object Text:
  def from(text: => String | AnyVal): Text[Any]                     = Text(text.toString)
  def fromSignal(textSignal: => Signal[String | AnyVal]): Text[Any] = Text(textSignal.map(_.toString))
