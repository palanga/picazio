package examples.todos.views

import examples.todos.State
import picazio.*

def Root(state: State.type): Shape[Any] =
  Shape.column(
    Shape.text("Todos"),
    CreateTodo(state.addTodo),
    TodoList(State.todos.signal, State.removeTodo),
  )
