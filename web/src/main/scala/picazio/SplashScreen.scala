package picazio

import org.scalajs.dom.console
import zio.*

object SplashScreen {

  private val picasso =
    """
      |░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒░░░░░░░░░▓██░
      |░░░░▒████████▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓█▓░░░░░░▒████░
      |░░░▒███▒█▒░░░███▓░░░░░░░░░░░░░░▒▓▓░░░░░░░░░▒▓▓░░░░░░░░░░▒░░░░░░░░▓██▓░░░░▓██▓▒██
      |░░░░▓▒░░██░░░▒████░░░░░░██░░░░███▓░░░░░▒▓▓▓███░░░░░░▒▓█████▓▒░░░░░███▒░░▓███░▒██
      |░░░░░░░███▓░░░████▒░░░░░░░░░▒██▓░░░░░▒███▓░▓█▓░░▒▓██▒░░░░████▓░░░▒███▒░▒█████▓▒░
      |░░░░░░▒███▒░░░████░░░▓█▓░░░░██▒░░░░░░▒████▒▒▓▒▓██▒░░░░░░░██▓▒░░░▓▓▓▒░░░░▒▒░░░░░░
      |░░░░░░███▒░░░██▓▒░░░▒███▓░░▒██▒░░░░▒▒▓▓▓▓███▓▓▒░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▓▓▒░
      |░░░░░▓██▓░░░░░░░░░░░░███▓░░░░▒▓▓▓▒▒░░░░░░░░░░░░░░░░░▒▒▒▒▓▓▓▓████████▓▓▓▒▒▒░░░░░░
      |░░░░▒██▓░░░░░░░░░░░░░░▒▒░░░░░░░░░░░░░░▒▒▒▓▓▓███████████▓▓▓▒▒▒▒░░░░░░░░░░░░░░░░░░
      |░░░░██▓░░░░░░░░░░░░░░░░░░░▒▒▒▓▓▓███████████▓▓▓▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
      |░░░▓█▓░░░░░░░░░░░▒▒▓▓██████████▓▓▓▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
      |░░▒█▓░░░░░▒▓▓██████▓▓▓▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
      |░░█▓░░░░▒▓▒▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
      |░▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
      |▒▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
      |""".stripMargin

  val print: Task[Unit] = ZIO.attempt(console.log(picasso))

}
