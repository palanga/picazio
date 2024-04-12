package picazio

import picazio.theme.Theme
import zio.*

trait WebApp { self =>

  def root: Shape[Any]

  def theme: Theme = Theme.default

  final def main(args0: Array[String]): Unit = app.main(args0)

  private val app = new ZIOWebApp {
    override def root: Task[Shape[Any]] = ZIO.succeed(self.root)
  }

}
