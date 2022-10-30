package picazio.projection

import com.raquo.domtypes.generic.keys.Style as LaminarStyle
import com.raquo.laminar.api.*
import picazio.*

class PaddingProjection[-R](private val self: Shape[R]) extends SpacingProjection(self, L.padding):
  def horizontal = SpacingProjection(self, new LaminarStyle("padding-inline"))
  def vertical   = SpacingProjection(self, new LaminarStyle("padding-block"))
