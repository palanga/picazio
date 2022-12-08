package examples.todos

import com.raquo.airstream.state.{Val, Var}
import examples.todos.domain.Todo

import scala.collection.immutable.SortedSet

object State:
  val todos: Var[SortedSet[Todo]]  = Var(SortedSet.empty)
  def addTodo(todo: Todo): Unit    = todos.update(_ + todo)
  def removeTodo(todo: Todo): Unit = todos.update(_ - todo)
