package ru.proslon.railbot.tasks

import akka.actor.{Cancellable, Actor}
import akka.actor.Actor.Receive
import akka.util.Timeout
import com.google.inject.Inject
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient
import ru.proslon.railbot.entity.Train

import scala.concurrent.ExecutionContext

import scala.concurrent.duration._
import scala.collection.JavaConversions._

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 20:39
 */
class CheckTrainTask @Inject()(client: RailClient, config: ConfigApplication) extends Actor {
  val interval: Int = ConfigApplication.get(config, "game.trains.interval").toInt
  val repeatPersent: Int = ConfigApplication.get(config, "game.trains.repeat").toInt
  private var userId: String = null

  implicit val timeout = Timeout(5 seconds)
  implicit val executor = ExecutionContext.Implicits.global
  private var scheduler: Cancellable = _

  override def preStart(): Unit = {
    userId = client.getUserId

    scheduler = context.system.scheduler.schedule(
      interval.seconds, interval.seconds, self, Tick
    )
  }

  override def postStop(): Unit = {
    scheduler.cancel()
  }

  override def receive = {
    case Tick =>
      val trains = client.getTrains(userId)
      trains.foreach( (train: Train) => {
        if (train.Reliability < repeatPersent ) {
          println("repeat : " + train.Id)
          client.trainRepair(train.Id)
        }
      } )
  }
}
