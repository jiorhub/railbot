package ru.proslon.railbot

import org.codehaus.jackson.JsonNode

import scala.concurrent.duration.FiniteDuration

/**
 * Author: oleg.lelenkov
 * Date: 10.03.15
 * Time: 2:57
 */

package object tasks {
  case object Stop
  case object Tick
  case class Start(delay: FiniteDuration)
  case object Collect
  case class Resources(json: JsonNode)
}
