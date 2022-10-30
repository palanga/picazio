package picazio

import com.raquo.laminar.api.L.Signal
import picazio.Shape.*

class Edge[-R](
  children: Seq[Shape[R]],
  attributes: List[Attribute[R]] = Nil,
  conditionalShow: Option[Signal[Boolean]] = None,
) extends Shape(attributes):

  /**
   * Alias for [[Edge.prependChild]]
   */
  def +:[R1](shape: Shape[R1]): Edge[R & R1] = this.prependChild(shape)

  /**
   * Alias for [[Edge.prependChild]]
   */
  def ::[R1](shape: Shape[R1]): Edge[R & R1] = this.prependChild(shape)

  override def showWhen(condition: => Signal[Boolean]): Edge[R] = new Edge(children, attributes, Some(condition))

  override def build(toLaminarMod: (=> Shape[R]) => Attribute[R] => LaminarMod): LaminarElem =
    import com.raquo.laminar.api.L
    import com.raquo.laminar.api.L.*
    def child                              = L.div(this.children.map(_.build(toLaminarMod)), attributes.map(toLaminarMod(this)))
    def maybeChild(maybe: Signal[Boolean]) = L.span(L.child.maybe <-- maybe.map(Option.when(_)(child)))
    conditionalShow.fold(child)(maybeChild)

  override def addAttribute[R1](attribute: Attribute[R1]): Edge[R & R1] =
    new Edge(children, attribute :: attributes, conditionalShow)

  /**
   * Add a child node at the start of this Shape
   */
  private def prependChild[R1](shape: Shape[R1]): Edge[R & R1] =
    new Edge(children.prepended(shape), attributes, conditionalShow)
