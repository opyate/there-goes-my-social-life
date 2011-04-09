package br.com.gfuture.solrscala.http

import java.net.{HttpURLConnection, URL}

/*
* Implementa um post http
*
* User: Jeosadache Galvão
* Date: 2/9/11
* Time: 12:02 PM
*/
object HttpPost extends HttpPost

trait HttpPost extends br.com.gfuture.solrscala.log.Logged {

  def post(url: URL, xml: scala.xml.Elem): Response = {
    val data: String = xml.mkString
    this.post(url, data, "text/xml")
  }

  def post(url: URL, data: String, contentType: String): Response = {
    this.post(url, data, data.length, contentType)
  }

  protected def post(url: URL, data: String, length: Int, contentType: String): Response = {
    debug({
      "posting " + url.toString + ", data: " + data
    })
    val conn: HttpURLConnection = url.openConnection.asInstanceOf[HttpURLConnection]
    length match {
      case a: Int =>
        conn.setRequestProperty("Content-Length", length.toString)
      case _ =>
    }
    conn.setRequestMethod("POST")
    conn.setRequestProperty("Content-Type", contentType)
    conn.setDoOutput(true)
    try {
      conn.getOutputStream.write(data.getBytes)
      conn.getOutputStream.close
      new Response(conn.getResponseCode, scala.io.Source.fromInputStream(conn.getInputStream).mkString)
    } catch {
      case e: Exception =>
        error("error ", e)
        new Response(conn.getResponseCode, scala.io.Source.fromInputStream(conn.getErrorStream).mkString)
    }
  }

}