package br.com.gfuture.solrscala.schema

/**
 * Prover funcionalidades genéricas para indexação de objetos no solr
 *
 * User: Jeosadache Galvão
 * Date: 2/8/11
 * Time: 11:22 AM
 */

import java.net.URL
import br.com.gfuture.solrscala.http.HttpPost
import br.com.gfuture.solrscala.configuration.Configuration
import br.com.gfuture.solrscala.log.Logged
import br.com.gfuture.solrscala.SolrException
import br.com.gfuture.solrscala.reflect.ReflectUtil
import br.com.gfuture.solrscala.annotations.SolrField

trait Schema extends Logged {

  @SolrField
  var id: String = null

  @SolrField
  val contentType: String = getClass.getSimpleName

  protected def hostSearch = Configuration.solrHostSearch

  protected def hostIndex = Configuration.solrHostIndex

  protected def core = Configuration.solrcore

  private def urlSearch = new URL(hostSearch + "/" + core)

  private def urlIndex = new URL(hostIndex + "/" + core + "/update?commit=true")

  /**
   * Indexa o schema no solr
   */
  def index = {

    val xml =
      <add>
        <doc>
          {for (field <- ReflectUtil.loadFieldsRecursively(getClass)) yield {
          if(field.isAnnotationPresent(classOf[SolrField])){
          <field name={field.getName}>{field.setAccessible(true)
          field.get(this)}</field>
          }
        }}
        </doc>
      </add>

    val response = HttpPost.post(urlIndex, xml)

    response.codeResponse match {
      case 200 =>
        debug({
          getClass.getSimpleName + " index successfully " + xml.mkString
        })
      case _ =>
        error({
          "error Index " + getClass.getSimpleName + ", body response: \\n" + response.bodyResponse
        })
        throw new SolrException("Erro ao indexar o objeto")
    }
  }

  /**
   * Exclui o indice no solr
   */
  def deleteIndex = {
    val xml = <delete>
      <id>{id}</id>
    </delete>
    val response = HttpPost.post(urlIndex, xml)
    response.codeResponse match {
      case 200 =>
        debug({
          "delete successfully " + getClass.getSimpleName + "[id=" + id + "]" + xml.mkString
        })
      case _ =>
        error({
          "error deleting index: " + xml.text + ", body response: \\n" + response.bodyResponse
        })
        throw new SolrException("Erro ao excluir o index")
    }
  }

  /**Deleta os índices que atenda a query passada como parametro
   *
   */
  def deleteByQuery(query: String) {
    val xml = <delete>
      <query>{query}</query>
    </delete>
    val response = HttpPost.post(urlIndex, xml)
    response.codeResponse match {
      case 200 =>
        debug({
          "delete successfully " + getClass.getSimpleName + "[query=" + query + "]" + xml.mkString
        })
      case _ =>
        error({
          "error deleting index: " + xml.text + ", body response: \\n" + response.bodyResponse
        })
        throw new SolrException("Erro ao excluir os indexes")
    }
  }

}