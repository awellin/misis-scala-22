package misis.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated, Snaphot}
import misis.repository.AccountRepository

import scala.concurrent.ExecutionContext

class AccountCommandStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {

    def group = s"account-${repository.accountId}"

    kafkaSource[AccountUpdate]
        .filter(command => repository.account.id == command.accountId && repository.account.amount + command.value >= 0)
        .map(command => AccountUpdated(
            accountId = command.accountId,
            value = command.value,
            category = command.category,
            tags = command.tags
        ))
        .to(kafkaSink)
        .run()

    kafkaSource[Snaphot]
        .map(command => AccountUpdated(
            accountId = command.accountId,
            value = 0, // todo ошибка
            needCommit = Some(true)
        ))
        .to(kafkaSink)
        .run()

}
