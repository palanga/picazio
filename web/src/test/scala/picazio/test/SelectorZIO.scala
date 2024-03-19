package picazio.test

import zio.*

trait SelectorZIO {
  def root: Task[RenderedElement]
}
