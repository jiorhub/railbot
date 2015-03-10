package ru.proslon.railbot.client

import java.io.BufferedReader

import com.google.inject.Inject
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import ru.proslon.railbot.ConfigApplication

import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 21:30
 */
class BotSiteClient @Inject()(httpClient: BotHttpClient, config: ConfigApplication) {
  private var samSess: String = null

  def getSamSess: String = {
    if (samSess == null) {
      val sam = httpClient.findCookie("sam_sess")
      if (sam.isDefined)
        samSess = sam.get.getValue
    }
    samSess
  }

  def login(userName: String, password: String): Boolean = {
    val data = Map(
      "className" -> "login ",
      "remember_me" -> "0",
      "submit" -> "Вход",
      "controller" -> "iframe",
      "consumer" -> "railnation-ru-meta",
      "applicationLanguage" -> "ru-RU",
      "module" -> "default",
      "email" -> userName,
      "password" -> password
    )

    val headers = Map(
      "Referer" -> "https://railnation-sam.traviangames.com/iframe/login/consumer/railnation-ru-meta/applicationLanguage/ru-RU"
    )

    val loginUrl = ConfigApplication.get(config, "client.url.login")
    val response = httpClient.post(loginUrl, data, headers)
    response.close()

    httpClient.addCookie("_ym_visorc_22363723", "b", ".railnation.ru")
    httpClient.findCookie("sam_sess").isDefined
  }

  def getConsumersId(region: String, language: String, serverName: String): Option[String] = {
    val pattern = "var server = \\{(.*)\\};".r

    val worldUrl = ConfigApplication.get(config, "client.url.world")
    val response = httpClient.get(worldUrl)
    val html: String = io.Source.fromInputStream(response.getEntity.getContent, "UTF-8").mkString
    response.close()

    val res: Option[Regex.Match] = pattern.findFirstMatchIn(html)

    if (res.isDefined) {
      val json = "{" + res.get.group(1) + "}"

      val mapper: ObjectMapper = new ObjectMapper
      val rootNode: JsonNode = mapper.readValue[JsonNode](json, classOf[JsonNode])
      val serverNode: JsonNode = rootNode.get(region).get(language).get("avatars").get(serverName)
      Option(serverNode.get("consumers_id").asText())
    } else
      Option(null)
  }

  def getWorldUrl(customerId: String): Option[String] = {
    val pattern = "window.top.location.href=\"(.*)\";".r

    val selectUrl = ConfigApplication.get(config, "client.url.select")
    val data = Map(
      "world" -> customerId,
      "sam_sess" -> getSamSess
    )

    val response = httpClient.post(selectUrl, data, Map[String, String]())
    val html: String = io.Source.fromInputStream(response.getEntity.getContent, "UTF-8").mkString
    response.close()

    val res: Option[Regex.Match] = pattern.findFirstMatchIn(html)
    if (res.isDefined) {
      Option(res.get.group(1))
    } else
      Option(null)
  }

  def getWorldPageContent(worldUrl: String): String = {
    val response = httpClient.get(worldUrl)
    io.Source.fromInputStream(response.getEntity.getContent, "UTF-8").mkString
  }

  def getWorldParams(content: String): Map[String, String] = {
    val patternKey = """(?s)function load_swf\(([^\}\{]*?)\)""".r
    val patternVal = """(?s)load_swf\(([^\}\{]*?)\);""".r

    val keysMatch = patternKey.findFirstMatchIn(content)
    val valuesMatch = patternVal.findFirstMatchIn(content)

    if (keysMatch.isDefined && valuesMatch.isDefined) {
      val keys = keysMatch.get.group(1).trim.split(",").map((s: String) => s.trim())
      val values = valuesMatch.get.group(1).trim.split(",").map((s: String) => {
        val st = s.trim()
        st.substring(1, st.length - 1)
      })
      keys.zip(values).toMap
    } else
      Map()
  }

  def getFlashVars(content: String, params: Map[String, String]): String = {
    val pattern = """(?s)var flashvars\s*?=\s*?\{(.*?)\};""".r

    val vars = pattern.findFirstMatchIn(content)
    if (vars.isDefined) {
      vars.get.group(1).trim.split(",").map((s: String) => {
        val Array(key, value) = s.trim().split(":")
        String.format("%s=%s", key, params.getOrElse(value.trim(), ""))
      }).mkString("&")
    } else
      ""
  }

  def reset(): Unit = {
    samSess = null
    httpClient.reset()
  }
}
