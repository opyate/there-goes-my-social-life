package br.com.gfuture.solrscala.http

import br.com.gfuture.solrscala.log.Logged

object HttpGet extends HttpGet

/**
 * Implementa um Http get simplificado
 * User: Jeosadache Galvão
 * Date: 2/13/11
 * Time: 12:02 AM
 */
trait HttpGet extends Logged {

  import java.io.InputStream;
  import java.net.URL;

  def request(url: URL): (Boolean, InputStream) =
    try {
      debug({
        "http get: " + url.toString
      })
      val body = url.openStream
      (true, body)
    }
    catch {
      case ex: Exception =>
        error(ex.getMessage, ex)
        (false, null)
    }

}