package ru.proslon.railbot.messages

/**
 * @author: o.lelenkov
 *          Date: 11.03.15
 *          Time: 19:28
 */

/*
{"Server":"","Errorcode":0,"Infos":{"Resources":"{\"0\":\"95759\",\"1\":\"406\",\"2\":\"665\",\"3\":\"0\",\"9\":\"0\",\"10\":\"1\",\"69\":\"1\",\"70\":\"6\",\"71\":\"8\"}","Position":-1,"Server-Time":0.019337892532349},

"Body":{"worldName":"\u0411\u043b\u043e\u043a \u0446\u0438\u043b\u0438\u043d\u0434\u0440\u043e\u0432","gameSpeed":"1",
"worldSelection":"http:\/\/www.railnation.ru\/#world","townNamePackage":"7","townNameOffset":0,"map":"map_1.dat",
"config":"default","parameter":[],"availableLanguages":["ru-RU","en-GB"],"enabledResources":[4,2,1,5,9,6,3,7],
"lastConsumption":-451}}
 */

case class ServerInfo(
  worldName: String,
  gameSpeed: String,
  worldSelection: String,
  townNamePackage: String,
  townNameOffset: Int,
  map: String,
  config: String,
  parameter: Array[AnyRef],
  availableLanguages: Array[String],
  enabledResources: Array[Int],
  lastConsumption: Int
) {}

case class ServerInfoMessage(Body: ServerInfo) extends Message {}
