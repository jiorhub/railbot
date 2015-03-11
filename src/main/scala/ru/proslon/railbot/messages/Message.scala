package ru.proslon.railbot.messages

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 18:53
 */
trait Message {
  var Server: String = ""
  var Errorcode: String = ""
  var Infos: Info = null
}

case class Info(
 Resources: String,
 Position: Int,
 ServerTime: Double
) {}
