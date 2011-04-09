package br.com.gfuture.solrscala.configuration

import java.io.{InputStream, File, FileInputStream, IOException}
import br.com.gfuture.solrscala.log.Logged

/**
 * Carrega as configurações da aplicação
 * User: Jeosadache Galvão
 * Date: 2/11/11
 * Time: 9:34 PM
 */
object Configuration extends Configuration {

  protected def propertyFile = "/solr-scala.properties"

  protected def pickJarBasedOn = classOf[Configuration]

}

trait Configuration extends Logged {

  protected def propertyFile: String

  protected def pickJarBasedOn: Class[_]

  lazy val properties: java.util.Properties = loadProperties

  protected def loadProperties:java.util.Properties = {
    val props = new java.util.Properties
    var stream: InputStream = null
    System.getProperty("solr-scala.configuration") match {
      case configFile: String =>
        stream = new FileInputStream(new File(configFile))
        debug({
          "carregado configuracoes de: " + configFile
        })
      case _ =>
        stream = pickJarBasedOn getResourceAsStream propertyFile
        debug({
          "carregado configuracoes do pacote: " + propertyFile
        })
    }
    if (stream ne null)
      quietlyDispose(props load stream, stream.close)
    props
  }

  protected def quietlyDispose(action: => Unit, disposal: => Unit) = {
    try {
      action
    }
    finally {
      try {
        disposal
      }
      catch {
        case e: IOException =>
          logger.error("erro loading properties " + propertyFile, e)
      }
    }
  }

  def solrHostSearch = properties.get("solr.host.search").toString

  def solrHostIndex = properties.get("solr.host.index").toString

  def solrcore = properties.get("solr.core").toString

}