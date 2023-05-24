package misis.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import misis.repository.AccountRepository

import scala.concurrent.ExecutionContext

class AccountRoute(repository: AccountRepository)(implicit ec: ExecutionContext) extends FailFastCirceSupport {

    def routes =
        (path("hello") & get) {
            complete("ok")
        } ~
        (path("amount") & get) {
            complete(repository.account.amount)
        }

}
