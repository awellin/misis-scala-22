package misis.model

import java.util.UUID

trait Command
case class AccountUpdate(accountId: Int, value: Int)

trait Event
case class AccountUpdated(accountId: Int, value: Int)
