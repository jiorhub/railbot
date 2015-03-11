package ru.proslon.railbot.entity

import org.codehaus.jackson.annotate.{JsonIgnoreProperties, JsonIgnore}
import ru.proslon.railbot.messages.Info

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 18:25
 */

/*
{"Id":"57cf05eb-1616-4f53-ba84-9c47596f1bdb","Type":"110100","LastStop":"692e286a-cd09-4600-bb65-58e2efc1b231",
"NextStop":"ea478e7d-c9ed-4856-94c5-fda4c545b2d9","ArrivalTime":13,"UserId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10",
"UpgradeIds":[210104,210101,210102,210103],"Reliability":87.221313274616,"StartTime":-32,
"Next":2,

"Schedule":[{"Resources":{"4":3},"TargetPosition":"ea478e7d-c9ed-4856-94c5-fda4c545b2d9","NextTarget":1},{"Resources":{"4":-3,"5":3},"TargetPosition":"692e286a-cd09-4600-bb65-58e2efc1b231","NextTarget":2},{"Resources":{"1":0},"TargetPosition":"ea478e7d-c9ed-4856-94c5-fda4c545b2d9","NextTarget":3},{"Resources":{"5":-3},"TargetPosition":"580e7238-d85d-4509-84fd-afe17043e224","NextTarget":0}],

"RevenueLastHour":4530,"RevenueToday":78936,"AccelerationTime":5.8147542183077,"Speed":14.536885545769,
"BoughtTime":-277655,"Boni":[],"Name":""}
 */

case class Train (
  Id: String,
  Type: String,
  LastStop: String,
  NextStop: String,
  ArrivalTime: Int,
  UserId: String,
  UpgradeIds: Array[Int],
  Reliability: Double,
  StartTime: Int,
  Next: Int,
  RevenueLastHour: Int,
  RevenueToday: Int,
  AccelerationTime: Double,
  Speed: Double,
  BoughtTime: Int,
  Name: String
) {

}



