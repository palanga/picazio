package examples.injection

import picazio.*
import zio.*

object Main extends WebApp {

  override def root: Shape[Any] =
    Shape.button("next quote")
      .onClick(printNextQuote)
      .provide(dependencies)

  private def dependencies = ZLayer.succeed(Quotes.default)

  private def printNextQuote = ZIO.serviceWithZIO[Quotes](_.nextQuote).debug.unit

}

final class Quotes(quotes: List[String]) {
  def nextQuote: Task[String] =
    for {
      i <- Random.nextInt
    } yield quotes.apply(Math.abs(i % quotes.length))
}

object Quotes {
  def default: Quotes = new Quotes(
    List(
      "hola",
      "que tal",
      "nube bebe",
      "listo",
    )
  )
}
