package examples.chat.common

import zio.*
import zio.stream.*

trait ChatRoom {
  def sendMessage(message: String): ZIO[Any, Nothing, Unit]
  def readMessages: ZIO[Scope, Nothing, Stream[Nothing, String]]
}
