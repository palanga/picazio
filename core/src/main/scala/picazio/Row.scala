package picazio

import com.raquo.laminar.api.L.Signal
import picazio.Shape.*

class Row[-R](
  children: Seq[Shape[R]],
  attributes: List[Attribute[R]] = Nil,
) extends Shape(attributes):

  /**
   * Alias for [[Row.prependChild]]
   */
  def +:[R1](shape: Shape[R1]): Row[R & R1] = this.prependChild(shape)

  /**
   * Alias for [[Row.prependChild]]
   */
  def ::[R1](shape: Shape[R1]): Row[R & R1] = this.prependChild(shape)

  override def build(toLaminarMod: (=> Shape[R]) => Attribute[R] => LaminarMod): LaminarElem =
    import com.raquo.laminar.api.L
    import com.raquo.laminar.api.L.*
    L.div(
      L.display       := "flex",
      L.flexDirection := "row",
      this.children.map(_.build(toLaminarMod)),
      attributes.map(toLaminarMod(this)),
    )

  override def addAttribute[R1](attribute: Attribute[R1]): Row[R & R1] =
    new Row(children, attribute :: attributes)

  /**
   * Add a child node at the start of this Shape
   */
  private def prependChild[R1](shape: Shape[R1]): Row[R & R1] =
    new Row(children.prepended(shape), attributes)
