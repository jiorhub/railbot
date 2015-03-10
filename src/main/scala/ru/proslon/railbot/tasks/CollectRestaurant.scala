package ru.proslon.railbot.tasks

import com.google.inject.Inject
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.client.RailClient

/**
 * Author: oleg.lelenkov
 * Date: 10.03.15
 * Time: 4:50
 */
class CollectRestaurant @Inject() (client: RailClient, config: ConfigApplication) extends CollectBuild(client, config) {
  override def getBuildId: Integer = {
    ConfigApplication.get(config, "game.build.restaurant").toInt
  }

  override def getBuildName: String = {
    "Restaurant"
  }
}
