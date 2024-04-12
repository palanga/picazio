package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import zio.*
import zio.stream.*

class DynamicListWebAppTest extends WebInterpreterSpec with Matchers {

  testShape("In a TODO list, items can be added and removed") { render =>

    class TodosState(val items: SubscriptionRef[List[Item]], val newTodoTitleInput: SubscriptionRef[String]) {

      val addItem: Task[Unit] =
        for {
          title <- newTodoTitleInput.get
          _     <- items.update(Item(title) :: _)
          _     <- this.clearInput
        } yield ()

      def removeItem(item: Item): Task[Unit] = items.update(_ diff List(item))

      private def clearInput: Task[Unit] = newTodoTitleInput.set("")

    }

    object TodosState {
      def build: Task[TodosState] =
        for {
          itemsRef           <- SubscriptionRef.make(List.empty[Item])
          createTodoInputRef <- SubscriptionRef.make("")
        } yield new TodosState(itemsRef, createTodoInputRef)

    }

    case class Item(title: String)

    def TodosPage(state: TodosState) =
      Shape.column(
        AddItemForm(state),
        TodoList(state),
      )

    def AddItemForm(state: TodosState) =
      Shape.row(
        Shape.textInput("TODO item title...", state.newTodoTitleInput),
        Shape.button("ADD").onClick(state.addItem),
      )

    def TodoList(state: TodosState) =
      Shape.column(
        state.items.signal.map(_.map(ItemShape(state)))
      )

    def ItemShape(state: TodosState)(item: Item) =
      Shape.row(
        Shape.text(item.title),
        Shape.button(s"DONE ${item.title}").onClick(state.removeItem(item)),
      )

    for {
      state          <- TodosState.build
      root           <- render(TodosPage(state))
      input           = root.head.head
      addItemButton   = root.head.tail.head
      _              <- input.write("sleep")
      _              <- addItemButton.click
      _              <- input.write("eat")
      _              <- addItemButton.click
      itemsList       = root.tail.head
      _              <- debounce(itemsList should have size 2)
      doneSleepButton = itemsList.tail.head.tail.head
      _              <- doneSleepButton.click
      _              <- debounce(itemsList should have size 1)
      doneEatButton   = itemsList.head.tail.head
      _              <- doneEatButton.click
      result         <- debounce(itemsList should have size 0)
    } yield result
  }

  testShape("numbers in a row") { render =>

    def Numbers(numbers: SubscriptionRef[List[Int]]) =
      Shape.row(numbers.signal.map(_.map(number => Shape.text(number.toString))))

    def updateNumbers(numbers: SubscriptionRef[List[Int]], lastNumberRef: SubscriptionRef[Int]) =
      for {
        last <- lastNumberRef.getAndUpdate(_ + 1)
        _    <- numbers.update(last :: _)
      } yield ()

    for {
      numbers    <- SubscriptionRef.make(List.empty[Int])
      lastNumber <- SubscriptionRef.make(0)
      root       <- render(Numbers(numbers))
      _          <- debounce(root shouldBe empty)
      _          <- updateNumbers(numbers, lastNumber).repeat(Schedule.recurs(2))
      result     <- debounce(root should have size 3)
    } yield result

  }

}
