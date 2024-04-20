package picazio

import zio.*

object ops {

  implicit private[picazio] class LogOps(self: ZIO[Any, Throwable, ?]) {

    def ignoreLoggedError: ZIO[Any, Nothing, Unit] =
      self.foldCauseZIO(
        cause =>
          ZIO.logLevel(LogLevel.Error) {
            ZIO.logCause("An error was silently ignored because it is not anticipated to be useful", cause)
          },
        _ => ZIO.unit,
      )

    def ignoreLoggedError(prefix: String): ZIO[Any, Nothing, Unit] =
      self.foldCauseZIO(
        cause =>
          ZIO.logLevel(LogLevel.Error) {
            ZIO.logCause(s"$prefix. An error was silently ignored because it is not anticipated to be useful", cause)
          },
        _ => ZIO.unit,
      )

  }

}
