package ru.proslon.railbot.client

import com.google.inject.Inject
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost, HttpRequestBase}
import org.apache.http.client.{CookieStore, HttpClient}
import org.apache.http.cookie.Cookie
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{BasicCookieStore, HttpClientBuilder, LaxRedirectStrategy}
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.message.{BasicHeader, BasicNameValuePair}
import org.apache.http.{Header, HttpEntity, HttpResponse}
import ru.proslon.railbot.ConfigApplication

import scala.collection.JavaConversions.{asJavaCollection, asScalaBuffer, bufferAsJavaList}
import scala.collection.mutable.ArrayBuffer

/**
 * Author: oleg.lelenkov
 * Date: 09.03.15
 * Time: 15:27
 */
class BotHttpClient @Inject()(config: ConfigApplication) {
  private val cookies: CookieStore = new BasicCookieStore()
  private val httpClient: HttpClient = createHttpClient(cookies)

  private def createHttpClient(cookies: CookieStore): HttpClient = {
    val clientBuilder: HttpClientBuilder = HttpClientBuilder.create()
    clientBuilder.setDefaultCookieStore(cookies)
    val userAgent = config("client.useragent").getOrElse(BotHttpClient.DEFAULT_USER_AGENT)
    clientBuilder.setUserAgent(userAgent)
    clientBuilder.setRedirectStrategy(new LaxRedirectStrategy)
    clientBuilder.setDefaultHeaders(getDefaultHeaders)
    clientBuilder.build()
  }

  private def getDefaultHeaders: List[Header] = {
    val headers = ArrayBuffer[Header]()
    for ((key, configKey) <- BotHttpClient.HEADERS_MAP) {
      val value = config(configKey)
      if (value.isDefined)
        headers += new BasicHeader(key, value.get)
    }
    headers.toList
  }

  def addCookie(cookie: Cookie): Unit = {
    cookies.addCookie(cookie)
  }

  def addCookie(key: String, value: String, domain: String = null): Unit = {
    val cookie: BasicClientCookie = new BasicClientCookie(key, value)
    if (domain != null) cookie.setDomain(domain)
    addCookie(cookie)
  }

  def findCookie(key: String): Option[Cookie] = {
    cookies.getCookies.find((c: Cookie) => c.getName == key)
  }

  def request(req: HttpRequestBase, headers: Map[String, String] = Map()): CloseableHttpResponse = {
    for ((name, value) <- headers) {
      req.addHeader(name, value)
    }
    httpClient.execute(req).asInstanceOf[CloseableHttpResponse]
  }

  def post(url: String, entity: HttpEntity, headers: Map[String, String]): CloseableHttpResponse = {
    val method: HttpPost = new HttpPost(url)
    method.setEntity(entity)
    request(method, headers)
  }

  def post(url: String, data: Map[String, String], headers: Map[String, String]): CloseableHttpResponse = {
    val pairs = data.map { case (key, value) => new BasicNameValuePair(key, value)}.toBuffer
    post(url, new UrlEncodedFormEntity(pairs), headers)
  }

  def post(url: String, data: String, headers: Map[String, String] = Map()): CloseableHttpResponse = {
    post(url, new StringEntity(data), headers)
  }

  def postJson(url: String, data: String, headers: Map[String, String]=Map()): CloseableHttpResponse = {
    val e = new StringEntity(data)
    e.setContentType("application/json; charset = utf-8")
    post(url, e, headers.updated("Accept", "application/json"))
  }

  def get(url: String, headers: Map[String, String] = Map()): CloseableHttpResponse = {
    val method: HttpGet = new HttpGet(url)
    request(method, headers)
  }

  def reset(): Unit = {
    cookies.clear()
  }
}


object BotHttpClient {
  val DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:27.0) Gecko/20100101 Firefox/27.0"
  val HEADERS_MAP = Map(
    "Accept" -> "client.headers.accept"
  )
}