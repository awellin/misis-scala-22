package misis.model

import java.time.Instant
import java.util.UUID

case class Account(id: Int, amount: Int) {
    def update(value: Int) = this.copy(amount = amount + value)
}

trait Command
case class AccountUpdate(accountId: Int, value: Int, category: Option[String] = None, tags: Option[Seq[String]] = None)
case class Snaphot(accountId: Int)

trait Event
case class AccountUpdated(
    operationId: UUID = UUID.randomUUID(),
    accountId: Int,
    value: Int,
    publishedAt: Option[Instant] = Some(Instant.now()),
    category: Option[String] = None,
    tags: Option[Seq[String]] = None,
    needCommit: Option[Boolean] = Some(false)
)
