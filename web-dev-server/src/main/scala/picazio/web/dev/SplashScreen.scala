package picazio.web.dev

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

  val print: Task[Unit] = ZIO.attempt(println(picasso))

}