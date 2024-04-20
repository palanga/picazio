package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class VariableShapeTest extends WebInterpreterSpec with Matchers {

  testShape("a variable shape") { render =>

    def shape(numberSignal: Signal[Shape[Any]]) =
      Shape.row(
        Shape.variable(numberSignal)
      )

    for {
      shapeRef   <- SubscriptionRef.make(Shape.text("hola"))
      shapeSignal = Signal.fromRef(shapeRef)
      root       <- render(shape(shapeSignal))
      _          <- debounce
      _          <- debounce(root.head.text shouldBe "hola")
      _          <- shapeRef.set(Shape.text("chau"))
      _          <- debounce
      result     <- debounce(root.head.text shouldBe "chau")
    } yield result

  }

  testShape("a variable shape based on case classes") { render =>

    sealed trait Number
    object Number {
      case object Zero extends Number
      case object One  extends Number
    }

    def numberToShape(number: Number): Shape[Any] = number match {
      case Number.Zero => Shape.text("zero")
      case Number.One  => Shape.text("one")
    }

    def shape(numberSignal: Signal[Number]) =
      Shape.row(
        Shape.variableWith(numberSignal)(numberToShape)
      )

    for {
      number <- SubscriptionRef.make[Number](Number.Zero)
      root   <- render(shape(number.signal))
      _      <- debounce
      _      <- debounce(root.head.text shouldBe "zero")
      _      <- number.set(Number.One)
      _      <- debounce
      result <- debounce(root.head.text shouldBe "one")
    } yield result

  }

  testShape("an eventual variable shape") { render =>

    sealed trait Number
    object Number {
      case object Zero extends Number
      case object One  extends Number
    }

    def numberToShape(number: Number): Shape[Any] = number match {
      case Number.Zero => Shape.text("zero")
      case Number.One  => Shape.text("one")
    }

    def shape(numberSignalTask: UIO[Signal[Number]]) =
      Shape.row(
        Shape.eventual(
          numberSignalTask.map(numberSignal => Shape.variableWith(numberSignal)(numberToShape))
        )
      )

    for {
      number          <- SubscriptionRef.make[Number](Number.Zero)
      numberSignalTask = number.changes.take(1).runHead.as(number.signal)
      root            <- render(shape(numberSignalTask))
      _               <- debounce
      _               <- debounce(root.head.text shouldBe "dummy")
      _               <- debounce
      _               <- number.set(Number.One).delay(0.seconds)
      _               <- debounce
      _               <- debounce
      result          <- debounce(root.head.text shouldBe "one")
    } yield result

  }

  testShape("a variable eventual shape") { render =>
    for {
      queueQueTal   <- Queue.sliding[Unit](1)
      queueTodoBien <- Queue.sliding[Unit](1)
      taskQueTal     = queueQueTal.take
      taskTodoBien   = queueTodoBien.take
      currentShape  <- SubscriptionRef.make(Shape.text("hola"))
      root          <- render(Shape.row(Shape.variable(currentShape.signal)))
      _             <- debounce
      _             <- debounce(root.head.text shouldBe "hola")
      _             <- currentShape.set(Shape.eventual(taskQueTal.as(Shape.text("que tal?"))))
      _             <- debounce
      _             <- debounce(root.head.text shouldBe "hola")
      _             <- queueQueTal.offer(())
      _             <- debounce
      _             <- debounce(root.head.text shouldBe "que tal?")
      _             <- currentShape.set(Shape.eventual(taskTodoBien.as(Shape.text("todo bien"))))
      _             <- debounce
      _             <- debounce(root.head.text shouldBe "que tal?")
      _             <- queueTodoBien.offer(())
      _             <- debounce
      _             <- debounce(root.head.text shouldBe "todo bien")
    } yield assert(true)

  }

}
