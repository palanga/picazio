package picazio.projection

import com.raquo.laminar.api.L.Signal
import picazio.*

class When(condition: Signal[Boolean]):
  def show[R](shape: Shape[R]): Edge[R] = shape.showWhen(condition)
  def hide[R](shape: Shape[R]): Edge[R] = shape.hideWhen(condition)
