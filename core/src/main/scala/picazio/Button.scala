package picazio

import picazio.Shape.*
import picazio.projection.ElevationProjection
import zio.*
import com.raquo.laminar.api.L.{Var, Signal}
import com.raquo.laminar.api.L
import picazio.projection.Elevation
import picazio.projection.shadow

object Buttons:
  def empty: Button[Any] = Button()

  def contained: Button[Any] =
    Button()
      .border.radius.small
      .border.none
      .elevation.low
      .background.color.primary
      .color.onPrimary
      .asInstanceOf[Button[Any]]

  def textOnly =
    Button()
      .border.radius.small
      .border.none
      .elevation.none
      .background.color.transparent
      .color.primary
      .asInstanceOf[Button[Any]]

  def outlined =
    Button()
      .border.radius.small
      .border.thin
      .border.color.primary
      .elevation.none
      .background.color.transparent
      .color.primary
      .asInstanceOf[Button[Any]]

case class Button[-R](
                       private[picazio] val text: String,
                       private[picazio] val attributes: List[Attribute[R]] = Nil,
                       private[picazio] val defaultElevation: Elevation = Elevation.None,
) extends Shape(attributes):

  private val state: Var[ButtonState] = Var(ButtonState())

  private def currentElevation = state.signal.map(calculateElevation(defaultElevation, _))

  def text(text: String): Button[R] = copy(text = text)

  override def elevation: ElevationProjection[R] = ButtonElevationProjection(this)

  override def build(toLaminarMod: (=> Shape[R]) => Attribute[R] => LaminarMod): LaminarElem =
    import com.raquo.laminar.api.*

    def baseAttributes = List(
      Attribute.Style(() => L.boxShadow <-- currentElevation.map(shadow)),
      Attribute.Style(() => L.opacity <-- state.signal.map(calculateOpacity)),
      Attribute.OnMouse(ZIO succeed state.update(_.press), ZIO succeed state.update(_.unPress)),
      Attribute.OnHover(ZIO succeed state.update(_.hover), ZIO succeed state.update(_.unHover)),
    )

    def laminarMods = (attributes.reverse ++ baseAttributes).map(toLaminarMod(this))
    L.button(text, laminarMods)

  override def addAttribute[R1](attribute: Attribute[R1]): Button[R & R1] = copy(attributes = attribute :: attributes)

private def calculateElevation(default: Elevation, state: ButtonState) = state match {
  case ButtonState(_, true) => Elevation.None
  case ButtonState(true, false) =>
    if default.ordinal == Elevation.values.length then default else Elevation.fromOrdinal(default.ordinal + 1)
  case _ => default
}

private def calculateOpacity(state: ButtonState) = if state.isHovered then "90%" else "100%"

object Button:

  def apply(): Button[Any] =
    new Button("")
      .padding.horizontal.large
      .margin.small
      .height.large
      .cursor.pointer
      .asInstanceOf[Button[Any]]

private case class ButtonState(isHovered: Boolean = false, isPressed: Boolean = false):
  def hover   = copy(isHovered = true)
  def unHover = copy(isHovered = false)
  def press   = copy(isPressed = true)
  def unPress = copy(isPressed = false)

class ButtonElevationProjection[-R](private val self: Button[R]) extends ElevationProjection[R](self):

  override def none    = self.copy(defaultElevation = Elevation.None)
  override def lowest  = self.copy(defaultElevation = Elevation.Lowest)
  override def low     = self.copy(defaultElevation = Elevation.Low)
  override def medium  = self.copy(defaultElevation = Elevation.Medium)
  override def high    = self.copy(defaultElevation = Elevation.High)
  override def highest = self.copy(defaultElevation = Elevation.High)

  override def dynamic(elevation: Signal[Elevation]) =
//    import com.raquo.laminar.api.*
//    elevation --> self.defaultElevation
    self

/**
 * Shape .button .withState(ButtonState.init) .elevation.dynamic(state =>
 * calculateElevation(baseElevation, state)) .onHover(state => state.hover,
 * state => state.unHover) .onMouse(state => state.press, state =>
 * state.unPress) .color.background.primary .color.foreground.onPrimary
 */
