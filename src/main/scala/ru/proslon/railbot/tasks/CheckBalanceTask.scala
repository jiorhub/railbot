package ru.proslon.railbot.tasks

import akka.actor.{Cancellable, Actor}
import akka.util.Timeout
import com.google.inject.Inject
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient
import ru.proslon.railbot.client.listeners.ResponseListener
import ru.proslon.railbot.entity.Resource

import scala.concurrent.duration._
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 12:33
 */
class CheckBalanceTask @Inject()(client: RailClient, config: ConfigApplication) extends Actor {
  val moneyId: String = ConfigApplication.get(config, "game.resources.gold")
  val bankLimitPersent: Int =  ConfigApplication.get(config, "game.resources.gold.limit").toInt
  val bankBuildId: Int = ConfigApplication.get(config, "game.build.bank").toInt
  val interval: Int = ConfigApplication.get(config, "game.resources.gold.interval").toInt

  var preBalance: Long = 0

  implicit val timeout = Timeout(5 seconds)
  implicit val executor = ExecutionContext.Implicits.global
  private var scheduler: Cancellable = _

  override def preStart(): Unit = {
    scheduler = context.system.scheduler.schedule(
      interval.seconds, interval.seconds, self, Tick
    )
  }

  override def postStop(): Unit = {
    scheduler.cancel()
  }

  override def receive: Receive = {
    case Tick =>
      val gold: Option[Resource] = getMoneyNode
      if (gold.isDefined) {
        val amount = gold.get.amount.toLong
        val limit = gold.get.limit.toLong
        val gross = (amount * 100) / limit

        val speed = (amount - preBalance) / (interval / 60)
        println("Current balance = " + amount + " limit = " + limit + " ("+ speed +"gold/min)")
        preBalance = amount

        if (gross >= bankLimitPersent) {
          client.buildLevelUp(bankBuildId)
          println("Bank level UP")
        }
      }
  }

  def getMoneyNode: Option[Resource] = {
    client.getUserInfo.resources.find((r: Resource) => {
      r.resourceId == moneyId
    })
  }

}
