package ru.proslon.railbot.messages

import ru.proslon.railbot.entity.Build

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 20:07
 */
case class BuildsMessage(Body: Array[Build]) extends Message {

}
