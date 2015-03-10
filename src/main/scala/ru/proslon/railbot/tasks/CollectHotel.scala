package ru.proslon.railbot.tasks

import com.google.inject.Inject
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient

/**
 * Author: oleg.lelenkov
 * Date: 10.03.15
 * Time: 5:02
 */
class CollectHotel @Inject() (client: RailClient, config: ConfigApplication) extends CollectBuild(client, config){
  override def getBuildId: Integer = {
    ConfigApplication.get(config, "game.build.hotel").toInt
  }

  override def getBuildName: String = {
    "Hotel"
  }
}