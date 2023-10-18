package picazio

import zio.*

trait WebApp { self =>

  def root: Shape

  final def main(args0: Array[String]): Unit = app.main(args0)

  private val app = new ZIOWebApp {
    override def root: Task[Shape] = ZIO.succeed(self.root)
  }

}
