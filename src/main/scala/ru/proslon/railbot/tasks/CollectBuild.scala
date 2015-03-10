package ru.proslon.railbot.tasks

import akka.actor.{Cancellable, Actor}
import akka.actor.Actor.Receive
import akka.util.Timeout
import com.google.inject.Inject
import org.codehaus.jackson.JsonNode
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient
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
    val json = client.getBuilds(userId)
    if (json.isEmpty)
      throw new Exception("build "+ getBuildName +" init error!")

    val build = json.get.get("Body").find((b: JsonNode) =>
      buildId == b.get("type").asInt()
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
      val json = client.collectBuild(buildId, userId)
      if (json.isEmpty)
        throw new Exception("build "+ getBuildName +" collect error!")

      self ! Start(getDurationCollect(json.get.get("Body")))
  }

  def getDurationCollect(build: JsonNode): FiniteDuration = {
    val productionTimeLeft = build.get("productionTimeLeft").asInt(0)
    val lastProductionUpdate = build.get("lastProductionUpdate").asInt(0)
    (productionTimeLeft + lastProductionUpdate + 30).seconds
  }

  def getBuildId: Integer

  def getBuildName: String
}
