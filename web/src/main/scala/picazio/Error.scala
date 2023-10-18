package picazio

abstract class Error(message: String) extends Exception(message)

object Error {
  object MountingNodeNotFound
      extends Error("""Couldn't find mounting node. Have you defined a <div id="picazio-root"></div> element?""")
}
