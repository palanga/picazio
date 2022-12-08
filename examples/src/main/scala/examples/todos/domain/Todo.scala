package examples.todos.domain

import java.time.LocalDateTime

case class Todo(title: String, created: LocalDateTime)

object Todo:
  def fromTitle(title: String): Todo    = Todo(title, LocalDateTime.now())
  given orderByDateTime: Ordering[Todo] = Ordering.by(_.created)
