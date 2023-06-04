package picazio.test

import zio.*

trait SelectorZIO {
  def renderedHtml: Task[String]
}
