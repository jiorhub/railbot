package ru.proslon.railbot.client

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.google.inject.Inject
import ru.proslon.railbot.ConfigApplication
import ru.proslon.railbot.entity.{Build, Train}
import ru.proslon.railbot.messages._

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 22:11
 */
class RailClient @Inject()(rpcClient: BotRPCClient, siteClient: BotSiteClient, config: ConfigApplication) extends ClientObserverable {
  var worldKey: String = null
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def login(): Boolean = {
    val email = config("client.account.email")
    val passwd = config("client.account.password")

    if (email.isDefined && passwd.isDefined) {
      siteClient.login(email.get, passwd.get)
    } else
      false
  }

  def getConsumersId: Option[String] = {
    val region = config("client.world.region")
    val language = config("client.world.language")
    val name = config("client.world.name")

    if (region.isDefined && language.isDefined && name.isDefined) {
      siteClient.getConsumersId(region.get, language.get, name.get)
    } else
      Option(null)
  }

  def logIntoWorld(): Unit = {
    if (!login())
      throw new ClientException("Login failed")

    val customersId = getConsumersId
    if (customersId.isEmpty)
      throw new ClientException("Failed to get customers_id")

    val worldUrl = siteClient.getWorldUrl(customersId.get)
    if (worldUrl.isEmpty)
      throw new ClientException("Failed to get world url")

    val content: String = siteClient.getWorldPageContent(worldUrl.get)

    rpcClient.worldParams = siteClient.getWorldParams(content)
    rpcClient.flashVars = siteClient.getFlashVars(content, rpcClient.worldParams)
    rpcClient.setWorldInfo(worldUrl.get)
    worldKey = rpcClient.worldKey
  }

  def request[T: Manifest](interface: String, method: String, params: Array[AnyRef]): T = {
    mapper.readValue[T](rpcClient.callMethod(interface, method, params))
  }

  def getUserId: String = {
    request[LoginInfo]("AccountInterface", "isLoggedIn", Array(worldKey)).Body
  }

  def getUnreadMessage: UnreadMail = {
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"d751713988987e9331980363e24189ce","parameters":[],"client":1}
    request[UnreadMailMessage]("GUIInterface", "getUnread", Array()).Body
  }

  def getBuilds(userId: String): Array[Build] = {
    // {"checksum":"6ba0163ea35a6a82c37acb1d6b8f4baf","parameters":["ef7fdc94-a864-9a59-fb2a-4b99815f3380"],"hash":"539911f7eb211b9a9eb02da5c03376c6","client":1}
    request[BuildsMessage]("BuildingInterface", "getBuildings", Array(userId)).Body
  }

  def buildLevelUp(buildId: Integer): Build = {
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"3835304d181b9abc95bca19c3604ae2b","parameters":[8],"client":1}
    //{"type":"6","userID":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","level":"4","buildDateTime":"-262988","durationLeft":4968,"lastDurationUpdate":0,"lastProductionUpdate":"-12","productionTimeLeft":"5850","hasUpgraded":0,"costOfLastUpgrade":225000}
    request[BuildMessage]("BuildingInterface", "build", Array(buildId)).Body
  }

  def collectBuild(buildId: Integer, userId: String): Build = {
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"27b3f189929b2bbafe31caf337d0cf01","parameters":[8,"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10"],"client":1}
    request[BuildMessage]("BuildingInterface", "collect", Array(buildId, userId)).Body
  }

  def getUserInfo: InterfaceInfo = {
    // interface	GUIInterface
    // method	get
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"d751713988987e9331980363e24189ce","parameters":[],"client":1}
    // {"Server":"","Errorcode":0,"Infos":{"Resources":"{\"0\":\"166117\",\"1\":\"406\",\"2\":\"734\",\"3\":\"0\",\"9\":\"0\",\"10\":\"1\",\"69\":\"1\",\"70\":\"6\",\"71\":\"8\"}","Position":-1,"Server-Time":0.17456102371216},"Body":{"resources":[{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"0","amount":"166117","limit":"800000"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"1","amount":"406","limit":"-1"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"2","amount":"734","limit":"-1"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"3","amount":"0","limit":"9"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"9","amount":"0","limit":"-1"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"10","amount":"1","limit":"-1"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"69","amount":"1","limit":"1"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"70","amount":"6","limit":"6"},{"userId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","resourceId":"71","amount":"8","limit":"8"}],"cityRewards":[],"lastJoinedCorporation":1426097621,"chatBanned":0,"openBonuses":0}}
    request[InterfaceInfoMessage]("GUIInterface", "get", Array.empty).Body
  }

  def getTrains(userId: String): Array[Train] = {
    //  interface	TrainInterface
    //  method	getTrainsOfUser
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"aa2f00b1dc29de8e5d6d4cf202d0d0a6","parameters":["29ffc9f2-b12f-5917-69cc-8fa0e77d4a10"],"client":1}
    request[TrainAllMessage]("TrainInterface", "getTrainsOfUser", Array(userId)).Body
  }

  def trainRepair(trainId: String): Train = {
    //interface	TrainInterface
    //method	repair
    //{"checksum":"1a39615460c262a8dda5527e13403570","hash":"8cbeabe8d5873479e7f87677609e5eb6","parameters":["e5031ea1-67d2-4374-a715-bed984cc5362"],"client":1}
    request[TrainMessage]("TrainInterface", "repair", Array(trainId)).Body
  }
}

/*
Изучение технологии
interface	TrainInterface
method	getTrainInfo
{"checksum":"1a39615460c262a8dda5527e13403570","hash":"8cbeabe8d5873479e7f87677609e5eb6","parameters":["e5031ea1-67d2-4374-a715-bed984cc5362"],"client":1}
{"Server":"","Errorcode":0,"Infos":{"Resources":"{\"0\":\"165868\",\"1\":\"406\",\"2\":\"734\",\"3\":\"0\",\"9\":\"0\",\"10\":\"1\",\"69\":\"1\",\"70\":\"6\",\"71\":\"8\"}","Position":-1,"Server-Time":0.025371789932251},"Body":{"Id":"e5031ea1-67d2-4374-a715-bed984cc5362","Type":"110300","LastStop":"692e286a-cd09-4600-bb65-58e2efc1b231","NextStop":"ea478e7d-c9ed-4856-94c5-fda4c545b2d9","ArrivalTime":35,"UserId":"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10","UpgradeIds":[210301,210303,210304],"Reliability":97.555129,"StartTime":0,"Next":4,"Schedule":[{"Resources":{"4":4},"TargetPosition":"ea478e7d-c9ed-4856-94c5-fda4c545b2d9","NextTarget":3},{"Resources":{"4":-4,"5":4},"TargetPosition":"692e286a-cd09-4600-bb65-58e2efc1b231","NextTarget":4},{"Resources":{"1":0},"TargetPosition":"ea478e7d-c9ed-4856-94c5-fda4c545b2d9","NextTarget":5},{"Resources":{"5":-4},"TargetPosition":"580e7238-d85d-4509-84fd-afe17043e224","NextTarget":2}],"RevenueLastHour":5708,"RevenueToday":90532,"AccelerationTime":5.6907158583333,"Speed":18.969052861111,"BoughtTime":-110391,"Boni":[],"Name":""}}
 */
