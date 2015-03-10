package ru.proslon.railbot.tasks

import akka.actor.{Cancellable, ActorLogging, Actor}
import akka.util.Timeout
import com.google.inject.Inject
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Author: oleg.lelenkov
 * Date: 10.03.15
 * Time: 2:58
 */
class MainTask @Inject() (client: RailClient, config: ConfigApplication) extends Actor {
  implicit val timeout = Timeout(5 seconds)
  implicit val executor = ExecutionContext.Implicits.global
  private var scheduler: Cancellable = _
  private val delay: FiniteDuration = ConfigApplication.get(config, "client.intervals.main").toInt.seconds

  override def preStart(): Unit = {
    scheduler = context.system.scheduler.schedule(
      delay, delay, self, Tick
    )
  }

  override def postStop(): Unit = {
    scheduler.cancel()
  }

  def receive = {
    case Tick =>
      client.getUnreadMessage
  }
}
