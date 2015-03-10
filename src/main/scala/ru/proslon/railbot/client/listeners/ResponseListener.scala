package ru.proslon.railbot.client.listeners

import org.codehaus.jackson.JsonNode

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 4:14
 */
trait ResponseListener {
  def process(json: Option[JsonNode])
}
