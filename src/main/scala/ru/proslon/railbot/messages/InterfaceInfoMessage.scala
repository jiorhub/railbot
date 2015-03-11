package ru.proslon.railbot.messages

import ru.proslon.railbot.entity.Resource

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 20:17
 */

// "Body":{"resources":[
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"0","amount":"166117","limit":"800000"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"1","amount":"406","limit":"-1"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"2","amount":"734","limit":"-1"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"3","amount":"0","limit":"9"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"9","amount":"0","limit":"-1"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"10","amount":"1","limit":"-1"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"69","amount":"1","limit":"1"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"70","amount":"6","limit":"6"},
// {"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"71","amount":"8","limit":"8"}
// ],"cityRewards":[],"lastJoinedCorporation":1426097621,"chatBanned":0,"openBonuses":0}

case class InterfaceInfo(
  resources: Array[Resource],
  cityRewards: Array[AnyRef],
  lastJoinedCorporation: Long,
  chatBanned: Int,
  openBonuses: Int
) {}

case class InterfaceInfoMessage(Body: InterfaceInfo) extends Message {

}
