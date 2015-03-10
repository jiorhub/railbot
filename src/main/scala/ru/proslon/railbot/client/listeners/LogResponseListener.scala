package ru.proslon.railbot.client.listeners

import org.codehaus.jackson.JsonNode

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 4:22
 */
class LogResponseListener extends ResponseListener {
  override def process(json: Option[JsonNode]): Unit = {
    if (json.isDefined) {
      println(json.get)
    }
  }
}
