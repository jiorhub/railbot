package ru.proslon

import akka.actor._
import com.google.inject.Guice
import ru.proslon.railbot.client.RailClient
import ru.proslon.railbot.client.listeners.LogResponseListener
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

  // логин
  val client = injector.instance[RailClient]
  client.logIntoWorld()

  client.addListener(new LogResponseListener)

  val userID = client.getUserId

  // запускаем задачи
  //val mainScheduler = system.actorOf(Props(injector.instance[MainTask]), "mainScheduler")
  val restaurantCollect = system.actorOf(Props(injector.instance[CollectRestaurant]), "restaurantCollect")
  val hotelCollect = system.actorOf(Props(injector.instance[CollectHotel]), "hotelCollect")
  val martCollect = system.actorOf(Props(injector.instance[CollectMart]), "martCollect")

  system.awaitTermination()
}




