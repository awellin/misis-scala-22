package misis.kafka

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.stream.scaladsl.Sink
import io.circe.generic.auto._
import misis.WithKafka
import misis.model.{AccountUpdate, AccountUpdated}
import misis.repository.AccountRepository
import org.apache.kafka.clients.consumer.ConsumerConfig

import scala.concurrent.ExecutionContext

class AccountEventStreams(repository: AccountRepository)(implicit val system: ActorSystem, executionContext: ExecutionContext)
    extends WithKafka {

    def group = s"account-${repository.accountId}"

    kafkaCSource[AccountUpdated]
        .filter { case (offset, event) => repository.account.id == event.accountId }
        .mapAsync(1) { case (offset, event) =>
            repository
                .update(event.value)
                .map { _ =>
                    println(s"Аккаунт ${event.accountId} обновлен на сумму ${event.value}. Баланс: ${repository.account.amount}. ${event}")
                    offset -> event
                }
        }
        .filter { case (offset, event) => event.needCommit.getOrElse(false) }
        .map { case (offset, event) =>
            println(s"Создается snaphot для счета ${event.accountId}")
            offset
        }
        .to(committerSink)
        .run()

    override def consumerSettings: ConsumerSettings[String, String] = super.consumerSettings
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
}
