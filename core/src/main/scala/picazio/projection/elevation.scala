package picazio.projection

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.*
import picazio.*
import zio.ZIO

class ElevationProjection[-R](private val self: Shape[R]):
  def none    = self.addAttribute(Attribute.Style(() => L.boxShadow := shadow(Elevation.None)))
  def lowest  = self.addAttribute(Attribute.Style(() => L.boxShadow := shadow(Elevation.Lowest)))
  def low     = self.addAttribute(Attribute.Style(() => L.boxShadow := shadow(Elevation.Low)))
  def medium  = self.addAttribute(Attribute.Style(() => L.boxShadow := shadow(Elevation.Medium)))
  def high    = self.addAttribute(Attribute.Style(() => L.boxShadow := shadow(Elevation.High)))
  def highest = self.addAttribute(Attribute.Style(() => L.boxShadow := shadow(Elevation.Highest)))

  def dynamic(elevation: Signal[Elevation]) =
    self.addAttribute(Attribute.Style(() => L.boxShadow <-- elevation.map(shadow)))

enum Elevation:
  case None, Lowest, Low, Medium, High, Highest

def shadow(elevation: Elevation) = elevation match {
  case Elevation.None    => "gray 1px 1px 0px -2px"
  case Elevation.Lowest  => "gray 1px 1px 4px -2px"
  case Elevation.Low     => "gray 1px 1px 6px -2px"
  case Elevation.Medium  => "gray 1px 1px 8px -2px"
  case Elevation.High    => "gray 1px 1px 10px -2px"
  case Elevation.Highest => "gray 1px 1px 12px -2px"
}
