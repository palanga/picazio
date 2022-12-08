package examples.todos.views

import com.raquo.airstream.state.Var
import examples.todos.domain.Todo
import picazio.*

def CreateTodo(addTodo: Todo => Unit): Shape[Any] =

  val titleVar: Var[String] = Var("")

  def createTodoAndEraseTitle(): Unit =
    addTodo(Todo.fromTitle(titleVar.now()))
    titleVar.set("")

  Shape.input
    .text(titleVar.signal)
    .onInput_(titleVar.set)
    .onKeyPress_ { case ReturnKey => createTodoAndEraseTitle() }

private val ReturnKey = 13
