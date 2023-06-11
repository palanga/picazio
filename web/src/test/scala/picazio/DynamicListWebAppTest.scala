package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*
import picazio.test.utils.*
import zio.*
import zio.stream.*

class DynamicListWebAppTest extends WebInterpreterSpec with Matchers {

  testRenderZIO("In a TODO list, items can be added and removed") { (render, select) =>

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

    def drawTodosPage(state: TodosState) =
      Shape.column(
        drawAddItemForm(state),
        drawTodoList(state),
      )

    def drawAddItemForm(state: TodosState) =
      Shape.row(
        Shape.textInput("TODO item title...", state.newTodoTitleInput),
        Shape.button("ADD").onClick(state.addItem),
      )

    def drawTodoList(state: TodosState) =
      Shape.column(
        state.items.signal.map(_.map(drawItem(state)))
      )

    def drawItem(state: TodosState)(item: Item): Shape =
      Shape.row(
        Shape.text(item.title),
        Shape.button(s"DONE ${item.title}").onClick(state.removeItem(item)),
      )

    for {
      state               <- TodosState.build
      _                   <- ZIO.attempt(render(drawTodosPage(state)))
      input               <- ZIO.attempt(select.selectInputWithPlaceholder("TODO item title..."))
      addItemButton       <- ZIO.attempt(select.selectButtonWithText("ADD"))
      _                   <- inputText(input, "sleep")
      _                   <- clickAndWait(addItemButton, state.items)
      _                   <- inputText(input, "eat")
      _                   <- clickAndWait(addItemButton, state.items)
      doneEatButton       <- ZIO.attempt(select.selectButtonWithText("DONE eat"))
      maxItemsCount       <- ZIO.attempt(select.firstElementChild.children(1).childElementCount)
      _                   <- clickAndWait(doneEatButton, state.items)
      eatDoneItemsCount   <- ZIO.attempt(select.firstElementChild.children(1).childElementCount)
      doneSleepButton     <- ZIO.attempt(select.selectButtonWithText("DONE sleep"))
      _                   <- clickAndWait(doneSleepButton, state.items)
      sleepDoneItemsCount <- ZIO.attempt(select.firstElementChild.children(1).childElementCount)
    } yield maxItemsCount == 2
      && eatDoneItemsCount == 1
      && sleepDoneItemsCount == 0

  }

  testRenderZIOSafe("numbers in a row") { (render, select) =>

    def drawNumbers(numbers: SubscriptionRef[List[Int]]) =
      Shape.row(numbers.signal.map(_.map(number => Shape.text(number.toString))))

    def updateNumbers(numbers: SubscriptionRef[List[Int]], lastNumberRef: SubscriptionRef[Int]) =
      for {
        last <- lastNumberRef.getAndUpdate(_ + 1)
        _    <- numbers.update(last :: _)
      } yield ()

    for {
      numbers          <- SubscriptionRef.make(List.empty[Int])
      lastNumber       <- SubscriptionRef.make(0)
      _                <- render(drawNumbers(numbers))
      noNumbersYetHtml <- select.renderedHtml
      fiber            <- updateNumbers(numbers, lastNumber).repeat(Schedule.recurs(2)).fork
      _                <- fiber.join
      html             <- select.renderedHtml
    } yield {
      noNumbersYetHtml shouldBe """<div style="display: flex; flex-direction: row; align-items: flex-start; justify-content: flex-start;"><!----></div>"""
      html shouldBe """<div style="display: flex; flex-direction: row; align-items: flex-start; justify-content: flex-start;"><!----><span style="padding-top: 4px;">2</span><span style="padding-top: 4px;">1</span><span style="padding-top: 4px;">0</span></div>"""
    }

  }

}
