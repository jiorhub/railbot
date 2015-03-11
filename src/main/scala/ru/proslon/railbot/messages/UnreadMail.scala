package ru.proslon.railbot.messages

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 19:57
 */

// "Body":{"messages":{"unreadThreads":"0","messages":[]},"corporation":0,"support":"0"}

case class UnreadMail(
  messages: Object,
  corporation: Int,
  support: String
) {}

case class UnreadMailMessage(Body: UnreadMail) extends Message {}
