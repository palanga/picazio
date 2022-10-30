package picazio.projection

import com.raquo.laminar.api.*
import picazio.*

class CursorProjection[-R](private val self: Shape[R]):
  def default = self.addAttribute(Attribute.Style(() => L.cursor := "default"))
  def pointer = self.addAttribute(Attribute.Style(() => L.cursor := "pointer"))
  def text    = self.addAttribute(Attribute.Style(() => L.cursor := "text"))
