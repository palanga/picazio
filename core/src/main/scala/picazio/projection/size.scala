package picazio.projection

import com.raquo.domtypes.generic.keys.Style as LaminarStyle
import com.raquo.laminar.api.*
import picazio.*

class SizeProjection[-R](private val self: Shape[R], private val prefix: LaminarStyle[String]):
  def smallest = self.addAttribute(Attribute.Style(() => prefix := "16px"))
  def small    = self.addAttribute(Attribute.Style(() => prefix := "18px"))
  def medium   = self.addAttribute(Attribute.Style(() => prefix := "24px"))
  def large    = self.addAttribute(Attribute.Style(() => prefix := "32px"))
  def largest  = self.addAttribute(Attribute.Style(() => prefix := "64px"))
