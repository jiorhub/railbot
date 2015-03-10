package ru.proslon.railbot.client

import com.google.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.http.client.methods.CloseableHttpResponse
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import ru.proslon.railbot.ConfigApplication

import scala.collection.JavaConversions._

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 23:26
 */
class BotRPCClient @Inject()(httpClient: BotHttpClient, config: ConfigApplication) {
  var worldParams: Map[String, String] = null
  var flashVars: String = null
  var worldKey: String = null
  var worldUrl: String = null


  def setWorldInfo(value: String) = {
    val res = parseKey(value)
    worldUrl = res._1
    worldKey = res._2
  }

  def parseKey(url: String): (String, String) = {
    val pattern = """(.*?)\?key=(.*)""".r
    val res = pattern.findFirstMatchIn(url)
    if (res.isDefined) {
      (res.get.group(1), res.get.group(2))
    } else
      ("", "")
  }

  def getRPCUrl: String = {
    worldParams.getOrElse("gs", "") + "flash.php"
  }

  def getSWFUrl: String = {
    String.format("%s%s/%s?%s", worldUrl, worldParams.getOrElse("assets_dir", ""), "Railnation.swf", flashVars)
  }

  def callMethod(interface: String, method: String, params: Array[AnyRef]): Option[JsonNode] = {
    val url = String.format("%s?interface=%s&method=%s", getRPCUrl, interface, method)

    val mapper: ObjectMapper = new ObjectMapper
    val paramsJson = mapper.writeValueAsString(params)

    val data = mapper.writeValueAsString(mapAsJavaMap(Map(
      "checksum" -> "1a39615460c262a8dda5527e13403570",
      "hash" -> DigestUtils.md5Hex(paramsJson),
      "parameters" -> params,
      "client" -> 1
    )))

    val response: CloseableHttpResponse = httpClient.postJson(url, data, Map("Referer" -> getSWFUrl))
    val json: String = io.Source.fromInputStream(response.getEntity.getContent).mkString
    response.close()

    Option(mapper.readValue[JsonNode](json, classOf[JsonNode]))
  }
}
