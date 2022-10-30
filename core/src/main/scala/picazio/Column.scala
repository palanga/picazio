package picazio

import com.raquo.laminar.api.L.Signal
import picazio.Shape.*

class Column[-R](
  children: Seq[Shape[R]],
  attributes: List[Attribute[R]] = Nil,
) extends Shape(attributes):

  /**
   * Alias for [[Column.prependChild]]
   */
  def +:[R1](shape: Shape[R1]): Column[R & R1] = this.prependChild(shape)

  /**
   * Alias for [[Column.prependChild]]
   */
  def ::[R1](shape: Shape[R1]): Column[R & R1] = this.prependChild(shape)

  override def build(toLaminarMod: (=> Shape[R]) => Attribute[R] => LaminarMod): LaminarElem =
    import com.raquo.laminar.api.L
    import com.raquo.laminar.api.L.*
    L.div(
      L.display       := "flex",
      L.flexDirection := "column",
      this.children.map(_.build(toLaminarMod)),
      attributes.map(toLaminarMod(this)),
    )

  override def addAttribute[R1](attribute: Attribute[R1]): Column[R & R1] =
    new Column(children, attribute :: attributes)

  /**
   * Add a child node at the start of this Shape
   */
  private def prependChild[R1](shape: Shape[R1]): Column[R & R1] =
    new Column(children.prepended(shape), attributes)
