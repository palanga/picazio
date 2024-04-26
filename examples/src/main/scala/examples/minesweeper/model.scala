package examples.minesweeper

import picazio.*
import zio.*

object model {

  trait Board {
    def init(difficulty: Difficulty): Task[Unit]
    def cellAt(coordinate: Coordinate): Task[Signal[Cell]]
    def dimensions: Task[(Int, Int)]
    def stepOn(coordinate: Coordinate): Task[Unit]
    def resets: Signal[Unit]
  }

  sealed abstract class Difficulty(val columns: Int, val rows: Int, val mines: Int)

  object Difficulty {
    final case object Easy   extends Difficulty(9, 9, 10)
    final case object Medium extends Difficulty(16, 16, 40)
    final case object Hard   extends Difficulty(30, 16, 99)
  }

  sealed trait Cell

  object Cell {
    case object Untouched               extends Cell
    case object Flagged                 extends Cell
    case object Unsure                  extends Cell
    case object Mine                    extends Cell
    case class Empty(surroundings: Int) extends Cell
  }

  /**
   * @param column
   *   or y, starting from 0 left to right
   * @param row
   *   or x, starting from 0 bottom to top
   */
  case class Coordinate(column: Column, row: Row) {
    def surroundings: Set[Coordinate] = {
      val c = column.self
      val r = row.self
      Set(
        Coordinate(Column(c + 1), Row(r - 1)),
        Coordinate(Column(c + 1), Row(r)),
        Coordinate(Column(c + 1), Row(r + 1)),
        Coordinate(Column(c), Row(r - 1)),
        Coordinate(Column(c), Row(r + 1)),
        Coordinate(Column(c - 1), Row(r - 1)),
        Coordinate(Column(c - 1), Row(r)),
        Coordinate(Column(c - 1), Row(r + 1)),
      )
    }
  }

  object Coordinate {
    def of(column: Int, row: Int): Coordinate = Coordinate(Column(column), Row(row))
  }

  final case class Column(self: Int) extends AnyVal
  final case class Row(self: Int)    extends AnyVal

  object Board {
    def init(difficulty: Difficulty)   = ZIO.serviceWithZIO[Board](_.init(difficulty))
    def cellAt(coordinate: Coordinate) = ZIO.serviceWithZIO[Board](_.cellAt(coordinate))
    def dimensions                     = ZIO.serviceWithZIO[Board](_.dimensions)
    def stepOn(coordinate: Coordinate) = ZIO.serviceWithZIO[Board](_.stepOn(coordinate))
    def resets                         = ZIO.serviceWith[Board](_.resets)
  }

}
