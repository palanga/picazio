package picazio.projection

import com.raquo.domtypes.generic.keys.Style as LaminarStyle
import com.raquo.laminar.api.*
import picazio.*

class BorderProjection[-R](private val self: Shape[R]):

  def thin   = withBorder.addAttribute(Attribute.Style(() => L.borderWidth := "thin"))
  def medium = withBorder.addAttribute(Attribute.Style(() => L.borderWidth := "medium"))
  def thick  = withBorder.addAttribute(Attribute.Style(() => L.borderWidth := "thick"))
  def none   = self.addAttribute(Attribute.Style(() => L.borderStyle := "none"))
  def radius = RadiusProjection(self)
  def color  = ColorProjection(withBorder, L.borderColor)

  private def withBorder = self.addAttribute(Attribute.Style(() => L.borderStyle := "solid"))

// TODO use size projection
class RadiusProjection[-R](private val self: Shape[R]):
  def small = self.addAttribute(Attribute.Style(() => L.borderRadius := "4px"))
  def large = self.addAttribute(Attribute.Style(() => L.borderRadius := "8px"))
  def none  = self.addAttribute(Attribute.Style(() => L.borderRadius := "none"))
