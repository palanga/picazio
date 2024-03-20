package examples.wallet.ui

import picazio.*
import picazio.style.*
import picazio.theme.Theme
import zio.*
import zio.stream.*

import java.time.LocalDateTime

object Main extends ZIOWebApp {

  override def theme: Theme =
    Theme
      .primaryColor("624ad9")
      .backgroundColor("efe1fd")
      .secondaryColor("4d9c5f")

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
    ).color(Color.background)

  private def Balance(balance: Signal[Float]) =
    Shape.column(
      Shape.text("Saldo").color(Color.primary.slightlyLight).centered,
      Shape.text(balance.map(b => f"${"$"} $b%.2f"))
        .color(Color.primary)
        .fontSize(Size.large)
        .centered,
    ).paddingTop(Size.medium)

  private def Transactions(transactions: Stream[Throwable, Transaction]) =
    Shape.column(
      Shape.text("Movimientos").color(Color.primary.moderatelyLight).margin(Size.mediumLarge),
      Shape.column(transactions.map(transaction => TransactionStreamItem(transaction)))
        .reverse
        .borderRadius(Size.medium)
        .backgroundColor(Color.surface),
    ).margin(Size.medium)

  private def TransactionStreamItem(transaction: Transaction) =
    Shape.row(
      Shape.column(
        Shape.text(transaction.description)
          .color(Color.onBackground)
          .overflowEllipsis,
        Shape.text(transaction.time.toString())
          .color(Color.onBackground.lightest)
          .fontSize(Size.mediumSmall)
          .overflowEllipsis,
      ),
      TransactionAmount(transaction.amount).centered,
    ).spaceBetween
      .padding(Size.mediumLarge)
      .paddingTop(Size.small)

  private def TransactionAmount(amount: Float): Shape =
    if (amount >= 0)
      Shape.text(f"+ $amount%,.2f")
        .color(Color.secondary)
        .backgroundColor(Color.secondary.ultraLight)
        .fontSize(Size.mediumLarge)
        .noWrap
        .borderRadius(Size.mediumSmall)
        .padding(Size.medium)
    else
      Shape.text(f"- ${amount * -1}%,.2f")
        .color(Color.onBackground)
        .fontSize(Size.mediumLarge)
        .noWrap
        .padding(Size.medium)

  case class Transaction(description: String, time: LocalDateTime, amount: Float)

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
                                 .throttleShape(1, 160.millis, 1)(_ => 1)
                                 .tap(updateBalance(balanceRef))
                                 .take(15)
                             )
      } yield new FakeAccount(transactionStream, balanceRef)

    private def generateTransaction =
      Clock.currentDateTime.map(time =>
        Transaction(
          "Recibiste dinero de Nube",
          time.toLocalDateTime,
          Math.random().toFloat * 1000000 * Math.random().toFloat * randomSign,
        )
      )

    private def randomSign: Float = if (Math.random() > 0.5) 1 else -1

    val layer: ZLayer[Any, Throwable, FakeAccount] = ZLayer.fromZIO(init)

    private def updateBalance(balance: Ref[Float])(transaction: Transaction): Task[Unit] =
      balance.update(_ + transaction.amount)

  }

}
