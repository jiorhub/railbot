package ru.proslon.railbot.client

import org.codehaus.jackson.JsonNode
import ru.proslon.railbot.client.listeners.ResponseListener

import scala.collection.mutable.ArrayBuffer

/**
 * Author: oleg.lelenkov
 * Date: 10.03.15
 * Time: 0:02
 */
trait ClientObserverable {
  val listeners = ArrayBuffer[ResponseListener]()

  def addListener(listener: ResponseListener) = {
    listeners += listener
  }

  def removeListener(listener: ResponseListener) = {
    listeners -= listener
  }

  def emit(json: Option[JsonNode]): Unit = {
    listeners.foreach((l: ResponseListener) => l.process(json))
  }
}
