package examples.minesweeper

import examples.minesweeper.model.*
import picazio.*
import picazio.style.Size

object Main extends WebApp {

  override def root: Shape[Any] =
    Shape.column(
      difficultySelector,
      board,
    ).provide(InMemoryBoard.layer)

  private def difficultySelector =
    Shape.row(
      easy,
      medium,
      hard,
    )

  private def easy   = Shape.button("EASY").onClick(Board.init(Difficulty.Easy))
  private def medium = Shape.button("MEDIUM").onClick(Board.init(Difficulty.Medium))
  private def hard   = Shape.button("HARD").onClick(Board.init(Difficulty.Hard))

  private def board = Shape.gridWithZIO(Board.dimensions)(cell).refreshOn(Board.refreshTrigger)

  private def cell(column: Int, row: Int) = Shape.variableWith(Board.cellAt(Coordinate.of(column, row))) {
    case Cell.Untouched           => Shape.icon(Icon.box).onClick(Board.stepOn(Coordinate.of(column, row)))
    case Cell.Flagged             => Shape.icon(Icon.flag)
    case Cell.Unsure              => Shape.icon(Icon.question)
    case Cell.Mine                => Shape.icon(Icon.mine)
    case Cell.Empty(surroundings) =>
      Shape.text(surroundings.toString)
        .centered
        .padding(Size.none)
        .height(Size.largeExtra)
        .width(Size.largeExtra)
  }

}
