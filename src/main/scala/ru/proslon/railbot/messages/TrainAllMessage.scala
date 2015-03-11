package ru.proslon.railbot.messages

import ru.proslon.railbot.entity.Train

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 20:44
 */

case class TrainAllMessage(Body: Array[Train]) extends Message {

}
