package picazio.projection

import com.raquo.domtypes.generic.keys.Style as LaminarStyle
import com.raquo.laminar.api.*
import picazio.*

class SpacingProjection[-R](private val self: Shape[R], private val prefix: LaminarStyle[String]):
  def smallest = self.addAttribute(Attribute.Style(() => prefix := "4px"))
  def small    = self.addAttribute(Attribute.Style(() => prefix := "6px"))
  def medium   = self.addAttribute(Attribute.Style(() => prefix := "10px"))
  def large    = self.addAttribute(Attribute.Style(() => prefix := "16px"))
  def largest  = self.addAttribute(Attribute.Style(() => prefix := "22px"))
  def none     = self.addAttribute(Attribute.Style(() => prefix := "none"))
