package ru.proslon.railbot.tasks

import akka.actor.{Cancellable, Actor}
import akka.actor.Actor.Receive
import akka.util.Timeout
import com.google.inject.Inject
import org.codehaus.jackson.JsonNode
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient
import ru.proslon.railbot.entity.Build
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.collection.JavaConversions._

/**
 * Author: oleg.lelenkov
 * Date: 10.03.15
 * Time: 3:00
 */
abstract class CollectBuild (client: RailClient, config: ConfigApplication) extends Actor {
  implicit val timeout = Timeout(5 seconds)
  implicit val executor = ExecutionContext.Implicits.global
  private var scheduler: Cancellable = _
  private var userId: String = null
  private val buildId:Integer = getBuildId

  override def preStart(): Unit = {
    userId = client.getUserId

    val build = client.getBuilds(userId).find((b: Build) =>
      buildId == b.Type.toInt
    )

    if (build.isEmpty)
      throw new Exception("build " + getBuildName + " not found!")

    self ! Start(getDurationCollect(build.get))
  }

  override def postStop(): Unit = {
    scheduler.cancel()
  }

  override def receive: Receive = {
    case Start(delay) =>
      println("start collect timer " + getBuildName +" (" + delay.toMinutes.toString + "min)")
      scheduler = context.system.scheduler.scheduleOnce(
        delay, self, Collect
      )
    case Collect =>
      println("Collect build " + getBuildName)
      val build = client.collectBuild(buildId, userId)
      self ! Start(getDurationCollect(build))
  }

  def getDurationCollect(build: Build): FiniteDuration = {
    val productionTimeLeft = build.productionTimeLeft.toInt
    val lastProductionUpdate = build.lastProductionUpdate.toInt
    if ((productionTimeLeft + lastProductionUpdate) > 0)
      (productionTimeLeft + lastProductionUpdate + CollectBuild.delay).seconds
    else
      CollectBuild.delay.seconds
  }

  def getBuildId: Integer

  def getBuildName: String
}

object CollectBuild {
  val delay = 30
}
