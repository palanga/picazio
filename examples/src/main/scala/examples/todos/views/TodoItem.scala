package examples.todos.views

import com.raquo.airstream.state.{Val, Var}
import examples.todos.State
import examples.todos.domain.Todo
import picazio.*
import zio.*

import java.time.LocalDateTime
import scala.collection.immutable.SortedSet

def TodoItem(todo: Todo, removeTodo: Todo => Unit): Shape[Any] =

  val title =
    Shape.text(todo.title)
      .margin.vertical.smallest
      .margin.horizontal.smallest

  val doneButton =
    Shape.button.contained
      .text("x")
      .onClick_(removeTodo(todo))
      .height.small
      .margin.vertical.smallest
      .margin.horizontal.smallest

  Shape.row(title, doneButton)
    .elevation.low
    .margin.vertical.smallest
    .margin.horizontal.smallest
