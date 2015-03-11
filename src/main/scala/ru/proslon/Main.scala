package ru.proslon

import akka.actor._
import com.google.inject.Guice
import ru.proslon.railbot.client.RailClient
import ru.proslon.railbot.tasks._
import ru.proslon.railbot.{ConfigApplication, RailbotModule}

/**
 * Author: oleg.lelenkov
 * Date: 08.03.15
 * Time: 15:51
 */
object Main extends App {

  import net.codingwell.scalaguice.InjectorExtensions._

  val injector = Guice.createInjector(new RailbotModule())
  val system = ActorSystem("railbot")

  // инициализируем конфиг
  val config = injector.instance[ConfigApplication]
  config.loadProperties("client.properties")

  config.setProperty("client.account.email", args(0))
  config.setProperty("client.account.password", args(1))

  // логин
  val client = injector.instance[RailClient]
  client.logIntoWorld()

  val userID = client.getUserId
  println("Login : " + userID)

  // запускаем задачи
  val mainScheduler = system.actorOf(Props(injector.instance[MainTask]), "mainScheduler")

  val restaurantCollect = system.actorOf(Props(injector.instance[CollectRestaurant]), "restaurantCollect")
  val hotelCollect = system.actorOf(Props(injector.instance[CollectHotel]), "hotelCollect")
  val martCollect = system.actorOf(Props(injector.instance[CollectMart]), "martCollect")

  val checkBalance = system.actorOf(Props(injector.instance[CheckBalanceTask]), "checkBalance")
  val checkTrainTask = system.actorOf(Props(injector.instance[CheckTrainTask]), "checkTrainTask")

  //system.shutdown()
  system.awaitTermination()
}




