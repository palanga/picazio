package picazio.web.dev

import zio.*

object Main extends ZIOAppDefault {
  override def run: ZIO[ZIOAppArgs, Throwable, Unit] = Server.start("examples")
}
