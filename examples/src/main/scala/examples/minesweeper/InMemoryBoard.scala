package examples.minesweeper

import examples.minesweeper.model.*
import picazio.*
import zio.*
import zio.stream.*

import scala.util.chaining.scalaUtilChainingOps

final class InMemoryBoard(private var state: State, resetRef: SubscriptionRef[Boolean]) extends Board {

  override def init(difficulty: Difficulty): Task[Unit] =
    State.init(difficulty).map(this.state = _) <* resetRef.update(!_)

  override def cellAt(coordinate: Coordinate): Task[Signal[Cell]] =
    state.coordinateToCell
      .get(coordinate)
      .pipe(ZIO.getOrFail(_))
      .map(_.signal)

  override def dimensions: Task[(Int, Int)] = ZIO.succeed(state.boardDimensions)

  override def resets: Signal[Unit] = resetRef.signal.map(_ => ())

  override def stepOn(coordinate: Coordinate): Task[Unit] =
    if (state.minesCoordinates.contains(coordinate)) {
      updateCell(coordinate, Cell.Mine)
    } else {
      checkSurroundings(coordinate)
    }

  private def checkSurroundings(coordinate: Coordinate): Task[Unit] = {

    val surroundings = coordinate.surroundings.filterNot(outOfBounds)
    val mines        = state.minesCoordinates.intersect(surroundings)

    if (mines.nonEmpty) updateCell(coordinate, Cell.Empty(mines.size))
    else updateCell(coordinate, Cell.Empty(0)) *> stepOnSurroundings(surroundings -- state.visited)

  }

  private def stepOnSurroundings(notVisitedSurroundings: Set[Coordinate]) =
    ZIO.foreachDiscard(notVisitedSurroundings)(checkSurroundings).unless(notVisitedSurroundings.isEmpty).unit

  private def updateCell(coordinate: Coordinate, cell: Cell): Task[Unit] =
    ZIO.getOrFail(state.coordinateToCell.get(coordinate))
      .flatMap(_.set(cell))
      .as(state.visited += coordinate) // TODO ojo acá
      .unit

  private def outOfBounds(coordinate: Coordinate) = {
    val column = coordinate.column.self
    val row    = coordinate.row.self
    column < 0 || column >= state.boardDimensions._1 || row < 0 || row >= state.boardDimensions._2
  }

}

object InMemoryBoard {
  def layer: ULayer[Board]     = ZLayer.fromZIO(make)
  private def make: UIO[Board] =
    for {
      newState   <- State.init(Difficulty.Medium)
      refreshRef <- SubscriptionRef.make(false)
    } yield new InMemoryBoard(newState, refreshRef)
}

final private class State(
  val boardDimensions: (Int, Int),
  val minesCoordinates: Set[Coordinate],
  val coordinateToCell: Map[Coordinate, SubscriptionRef[Cell]],
  val visited: scala.collection.mutable.Set[Coordinate] = scala.collection.mutable.Set.empty,
)

private object State {
  def init(difficulty: Difficulty): UIO[State] = {
    val coordinates: Seq[Coordinate] = for {
      column <- 0 until difficulty.columns
      row    <- 0 until difficulty.rows
    } yield Coordinate.of(column, row)
    for {
      mines <- generateMines(difficulty)
      cells <- ZIO.collect(coordinates)(c => SubscriptionRef.make[Cell](Cell.Untouched).map(c -> _))
    } yield new State(
      difficulty.columns -> difficulty.rows,
      mines.toSet,
      cells.toMap,
    )
  }

  private def generateMines(difficulty: Difficulty): UIO[List[Coordinate]] =
    ZIO.foreach((0 until difficulty.mines).toList)(_ =>
      Random.nextIntBounded(difficulty.columns).zip(Random.nextIntBounded(difficulty.rows))
    ).map(_.map(pair => Coordinate.of(pair._1, pair._2)))

}
