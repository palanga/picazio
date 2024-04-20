package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class InjectionTest extends WebInterpreterSpec with Matchers {

  testShape("a shape can have dependencies") { render =>

    class Printer {
      def print(text: String): Task[Unit] = Console.printLine(text)
    }

    object Printer {
      def layer: ULayer[Printer] = ZLayer.succeed(init)
      def init: Printer          = new Printer
    }

    def PrintButton: Shape[Printer] =
      Shape.button("hola")
        .onClick(ZIO.serviceWithZIO[Printer](_.print("hola")))

    for {
      _      <- render(PrintButton.provide(Printer.layer))
      result <- debounce(assert(true)) // compiles and completes
    } yield result

  }

  testShape("when multiple shapes use the same dependency, it should be constructed only once") { render =>

    class Printer {
      def print(text: String): Task[Unit] = Console.printLine(text)
    }

    object Printer {
      def layer(initCounter: SubscriptionRef[Int]): ULayer[Printer] = ZLayer.fromZIO(init(initCounter))
      def init(initCounter: SubscriptionRef[Int]): UIO[Printer]     = initCounter.update(_ + 1).as(new Printer)
    }

    def PrintButton(text: String): Shape[Printer] =
      Shape.button(text)
        .onClick(ZIO.serviceWithZIO[Printer](_.print(text)))

    val Root: Shape[Printer] =
      Shape.row(
        PrintButton("hola"),
        PrintButton("que tal"),
      )

    for {
      initCount  <- SubscriptionRef.make(0)
      changes     = initCount.changes
      _          <- render(Root.provide(Printer.layer(initCount)))
      _          <- changes.runHead
      finalCount <- initCount.get
      result     <- debounce(finalCount shouldBe 1)
    } yield result

  }

}
