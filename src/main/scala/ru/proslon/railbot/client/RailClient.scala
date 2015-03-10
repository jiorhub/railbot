package ru.proslon.railbot.client

import com.google.inject.Inject
import org.codehaus.jackson.JsonNode
import ru.proslon.railbot.ConfigApplication

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 22:11
 */
class RailClient @Inject()(rpcClient: BotRPCClient, siteClient: BotSiteClient, config: ConfigApplication) extends ClientObserverable {
  var worldKey: String = null

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

  def request(interface: String, method: String, params: Array[AnyRef]): Option[JsonNode] = {
    val json = rpcClient.callMethod(interface, method, params)
    emit(json)
    json
  }

  def getUserId: String = {
    val json = request("AccountInterface", "isLoggedIn", Array(worldKey))
    if (json.isDefined)
      json.get.get("Body").asText()
    else
      ""
  }

  def getUnreadMessage: String = {
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"d751713988987e9331980363e24189ce","parameters":[],"client":1}
    val json = request("GUIInterface", "getUnread", Array())
    if (json.isDefined)
      json.get.get("Body").toString
    else
      ""
  }

  def getBuilds(userId: String): Option[JsonNode] = {
    // {"checksum":"6ba0163ea35a6a82c37acb1d6b8f4baf","parameters":["ef7fdc94-a864-9a59-fb2a-4b99815f3380"],"hash":"539911f7eb211b9a9eb02da5c03376c6","client":1}
    request("BuildingInterface", "getBuildings", Array(userId))
  }

  def buildLevelUp(buildId: String): Option[JsonNode] = {
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"3835304d181b9abc95bca19c3604ae2b","parameters":[8],"client":1}
    request("BuildingInterface", "build", Array(buildId))
  }

  def collectBuild(buildId: Integer, userId: String): Option[JsonNode] = {
    // {"checksum":"1a39615460c262a8dda5527e13403570","hash":"27b3f189929b2bbafe31caf337d0cf01","parameters":[8,"29ffc9f2-b12f-5917-69cc-8fa0e77d4a10"],"client":1}
    request("BuildingInterface", "collect", Array(buildId, userId))
  }

}
