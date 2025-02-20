package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class StreamColumnTest extends WebInterpreterSpec with Matchers {

  testShape("a streamed text") { render =>

    def TextStream(textStream: Stream[Nothing, String]) =
      Shape.column(textStream.map(Shape.text))

    for {
      queue  <- Queue.sliding[String](1)
      stream <- ZIO.attempt(ZStream.fromQueue(queue))
      root   <- render(Shape.column(TextStream(stream)))
      _      <- debounce(root.head.size shouldBe 0)
      _      <- queue.offer("107")
      _      <- debounce(root.head.head.text shouldBe "107")
      _      <- queue.offer("53")
      _      <- debounce(root.head.head.text shouldBe "107")
      _      <- debounce(root.head.last.text shouldBe "53")
    } yield assert(true)

  }

}
