package examples.wallet.ui

import picazio.*
import picazio.style.*
import zio.*
import zio.stream.*

import java.time.LocalDateTime

object Main extends ZIOWebApp {

  val violeta           = "#624ad9"
  val violetaClarito    = "#8877e7"
  val violetaMasClarito = "#efe1fd"
  val blanco            = "#ffffff"
  val negro             = "#000000"
  val gris              = "#999999"

  override def root: Task[Shape] =
    (for {
      account <- ZIO.service[Account]
    } yield Root(account))
      .provide(FakeAccount.layer)

  private def Root(account: Account) =
    Shape.surface(
      Shape.column(
        Balance(account.balance),
        Transactions(account.transactions),
      )
    ).color(Color.primary.brightest)

  private def Balance(balance: Signal[Float]) =
    Shape.column(
      Shape.text("Saldo").color(Color.primary).centered,
      Shape.text(balance.map(b => f"${"$"} $b%.2f"))
        .color(Color.primary.dark)
        .fontSize(Size.large)
        .centered,
    ).paddingTop(Size.medium)

  private def Transactions(transactions: Stream[Throwable, Transaction]) =
    Shape.column(
      Shape.text("Movimientos").color(Color.primary.bright).margin(Size.mediumLarge),
      Shape.column(transactions.map(transaction => TransactionStreamItem(transaction)))
        .reverse
        .borderRadius(Size.medium)
        .backgroundColor(blanco),
    ).margin(Size.medium)

  case class Transaction(description: String, time: LocalDateTime, amount: Float)

  private def TransactionStreamItem(transaction: Transaction) =
    Shape.row(
      Shape.column(
        Shape.text(transaction.description).color(negro),
        Shape.text(transaction.time.toString()).color(gris).fontSize(Size.mediumSmall),
      ),
      Shape.text(f" ${transaction.amount}%.2f").color(negro).fontSize(Size.mediumLarge),
    )
      .padding(Size.mediumLarge)
      .paddingTop(Size.small)

  trait Account {
    def balance: Signal[Float]
    def transactions: Stream[Throwable, Transaction]
  }

  final case class FakeAccount(transactionGenerator: Stream[Throwable, Transaction], balanceRef: SubscriptionRef[Float])
      extends Account {
    override def balance: Signal[Float]                       = balanceRef.signal
    override def transactions: Stream[Throwable, Transaction] = transactionGenerator
  }

  object FakeAccount {

    private val init: ZIO[Any, Throwable, FakeAccount] =
      for {
        balanceRef        <- SubscriptionRef.make(0f)
        transactionStream <- ZIO.attempt(
                               ZStream
                                 .repeatZIO(generateTransaction)
                                 .throttleShape(1, 10.second, 1)(_ => 1)
                                 .tap(updateBalance(balanceRef))
                             )
      } yield new FakeAccount(transactionStream, balanceRef)

    private def generateTransaction =
      Clock.currentDateTime.map(time => Transaction("Recibiste dinero de Nube", time.toLocalDateTime, 107.34f))

    val layer: ZLayer[Any, Throwable, FakeAccount] = ZLayer.fromZIO(init)

    private def updateBalance(balance: Ref[Float])(transaction: Transaction): Task[Unit] =
      balance.update(_ + transaction.amount)

  }

}
