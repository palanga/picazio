package picazio.projection

import com.raquo.domtypes.generic.keys.Style as LaminarStyle
import com.raquo.laminar.api.*
import picazio.*

class ColorProjection[-R](private val self: Shape[R], private val prefix: LaminarStyle[String]):
  def primary     = self.addAttribute(Attribute.Style(() => prefix := "rgb(248, 210, 196)"))
  def secondary   = self.addAttribute(Attribute.Style(() => prefix := "cornflowerblue"))
  def onPrimary   = self.addAttribute(Attribute.Style(() => prefix := "#442C2E"))
  def onSecondary = self.addAttribute(Attribute.Style(() => prefix := "white"))
  def background  = self.addAttribute(Attribute.Style(() => prefix := "white"))
  def foreground  = self.addAttribute(Attribute.Style(() => prefix := "black"))
  def transparent = self.addAttribute(Attribute.Style(() => prefix := "transparent"))
