package picazio

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.Signal
import picazio.Shape.*
import zio.*

import scala.annotation.targetName

class Input[-R](text: Signal[String] = Signal.fromValue(""), attributes: List[Attribute[R]] = Nil)
    extends Shape(attributes):

//  def text(text: => String): Input[R] = new Input(text, this.attributes)

  @targetName("textSignal")
  def text(textSignal: => Signal[String]): Input[R] = new Input(textSignal, this.attributes)

  def placeholder(text: => String): Input[R] = addAttribute(Attribute.Placeholder(text))

  def onInput_(f: String => Unit): Input[R] = addAttribute(Attribute.OnInput(ZIO succeed f(_)))

  def onInput[R1](zio: String => ZIO[R1, Nothing, Any]): Input[R & R1] =
    addAttribute(Attribute.OnInput(zio))

  override def build(toLaminarMod: (=> Shape[R]) => Attribute[R] => LaminarMod): LaminarElem =
    import com.raquo.laminar.api.*
    def laminarMods = attributes.map(toLaminarMod(this))
    L.input(L.value <-- text, laminarMods)

  override def addAttribute[R1](attribute: Attribute[R1]): Input[R & R1] = new Input(text, attribute :: attributes)
