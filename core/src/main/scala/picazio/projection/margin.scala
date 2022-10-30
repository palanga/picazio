package picazio.projection

import com.raquo.domtypes.generic.keys.Style as LaminarStyle
import com.raquo.laminar.api.*
import picazio.*

class MarginProjection[-R](private val self: Shape[R]) extends SpacingProjection(self, L.margin):
  def horizontal = SpacingProjection(self, new LaminarStyle("margin-inline"))
  def vertical   = SpacingProjection(self, new LaminarStyle("margin-block"))
