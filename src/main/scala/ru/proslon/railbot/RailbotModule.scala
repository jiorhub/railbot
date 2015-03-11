package ru.proslon.railbot

import com.google.inject.Singleton
import com.tzavellas.sse.guice.ScalaModule
import ru.proslon.railbot.client._
import ru.proslon.railbot.tasks._

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 3:43
 */
class RailbotModule extends ScalaModule {
  override def configure(): Unit = {
    bind[ConfigApplication].in[Singleton] // настройки

    bind[BotHttpClient].in[Singleton] // клиент низкого уровня HTTP
    bind[BotSiteClient].in[Singleton] // клиент сайта (авторизация и выбор сервера)
    bind[BotRPCClient].in[Singleton] // клиент игры
    bind[RailClient].in[Singleton] // клиент фасад

    bind[MainTask]
    bind[CollectRestaurant]
    bind[CollectHotel]
    bind[CollectMart]

    bind[CheckBalanceTask]
    bind[CheckTrainTask]
  }
}
