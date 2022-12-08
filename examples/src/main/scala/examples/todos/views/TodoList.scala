package examples.todos.views

import com.raquo.airstream.core.Signal
import examples.todos.domain.Todo
import picazio.*

import scala.collection.immutable.SortedSet

def TodoList(todos: Signal[SortedSet[Todo]], removeTodo: Todo => Unit): Shape[Any] =
  Shape.column(
    todos.map(_.toList.map(TodoItem(_, removeTodo)))
  )
