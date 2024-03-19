package picazio.test

class RenderedStyleSet(self: Map[String, String]) extends Iterable[(String, String)] {

  def +(stylePair: (String, String)) = new RenderedStyleSet(self + stylePair)

  def debug: RenderedStyleSet = {
    println(this)
    this
  }

  override def iterator: Iterator[(String, String)] = self.iterator

  override def toString(): String =
    self.map { case (key, value) => s"\"$key\" -> \"$value\"" }.mkString("RenderedStyleSet(\n  ", ",\n  ", ",\n)")

}

object RenderedStyleSet {
  def apply(pairs: (String, String)*): RenderedStyleSet =
    new RenderedStyleSet(pairs.toMap)
}
