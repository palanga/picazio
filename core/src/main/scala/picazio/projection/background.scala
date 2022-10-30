package picazio.projection

import com.raquo.laminar.api.*
import picazio.*

class BackgroundProjection[-R](private val self: Shape[R]):
  def color = ColorProjection(self, L.backgroundColor)
