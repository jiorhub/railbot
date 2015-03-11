package ru.proslon.railbot

import java.util.Properties

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 15:31
 */
class ConfigApplication {
  private val properties: Properties = new Properties()

  def getProperty(key: String): Option[String] = {
    Option(properties.getProperty(key))
  }

  def loadProperties(fileName: String): Unit = {
    val is = getClass.getClassLoader.getResourceAsStream(fileName)
    properties.load(is)
  }

  def apply(key: String): Option[String] = {
    getProperty(key)
  }

  def setProperty(key: String, value: String): Unit = {
    properties.setProperty(key, value)
  }
}

object ConfigApplication {
  def get(config: ConfigApplication, key: String): String = {
    val value = config(key)
    if (value.isEmpty)
      throw new ConfigException(key)
    value.get
  }
}

class ConfigException(key: String) extends Exception("Not found config '" + key + "'") {}
