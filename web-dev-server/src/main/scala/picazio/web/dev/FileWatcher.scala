package picazio.web.dev

import zio.*
import zio.stream.*

import java.nio.file.*
import scala.jdk.CollectionConverters.*

final class FileWatcher(hub: Hub[String]) {
  def streamScoped: ZIO[Scope, Nothing, ZStream[Any, Nothing, String]] = ZStream.fromHubScoped(hub)
}

object FileWatcher {

  def make(path: String): ZIO[Any, Throwable, FileWatcher] = for {
    hub           <- Hub.sliding[String](1)
    javaWatcher   <- ZIO.attemptBlocking(FileSystems.getDefault.newWatchService())
    directoryPath <- ZIO.attemptBlocking(Paths.get(path))
    _             <- register(directoryPath, javaWatcher)
    _             <- process(hub, javaWatcher).forever.forkDaemon
  } yield new FileWatcher(hub)

  private def register(directoryPath: Path, javaWatcher: WatchService) =
    ZIO.attemptBlocking(
      directoryPath.register(
        javaWatcher,
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_MODIFY,
      )
    )

  private def process(hub: Hub[String], watchService: WatchService): ZIO[Any, Throwable, Unit] = for {
    key    <- ZIO.attemptBlocking(watchService.take())
    events <- ZIO.attemptBlocking(key.pollEvents().asScala.map(_.context().toString))
    _      <- hub.publishAll(events.filter(_ == "main.js"))
    _      <- ZIO.attemptBlocking(key.reset())
  } yield ()

}
