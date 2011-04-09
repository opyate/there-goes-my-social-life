package br.com.gfuture.solrscala

import collection.mutable.Builder
import collection.immutable.List
import configuration.Configuration
import http.HttpGet
import java.net.URL
import xml.{Node, XML}
import java.lang.reflect.Field

/**
 * Interface de consulta no solr

 * by Jeosadache Galvão, josa.galvao@gmail.com
 */
class Query[T](val query: String, val schemaClass: Class[T]) {

  private val url = Configuration.solrHostSearch + "/" + Configuration.solrcore + "/select/?q="

  def result: List[T] = {
    val builder: Builder[T, List[T]] = List.newBuilder[T]
    val (true, body) = HttpGet.request(new URL(url + java.net.URLEncoder.encode(query, "UTF-8")))
    val elem = XML.load(body)
    (elem \\ "response" \ "result").foreach({
      result =>
        result.child.foreach({
          doc => builder += makeSchemaForDoc(doc)
        })
    })
    builder.result
  }

  def uniqueResult: T = {
    result.size match {
      case 0 =>
        throw new NoResultException()
      case _ =>
        result(0)
    }
  }

  private def makeSchemaForDoc(doc: Node): T = {
    val schema: T = schemaClass.newInstance
    (doc \\ "doc" \ "str").foreach({
      element =>
        try {
          val field = findField((element \\ "@name").text, schemaClass)
          field.setAccessible(true)
          field.set(schema, element.text)
        } catch {
          case e: java.lang.NoSuchFieldException =>
        }
    })
    schema
  }

  /**Pesquisa um field da classe e superclasses reculsirvamente
   *
   * @param o nome do field
   * @param a classe da entidade
   *
   */
  def findField[T](name: String, entityClass: Class[T]): Field = {
    try {
      entityClass.getDeclaredField(name)
    } catch {
      case e: java.lang.NoSuchFieldException =>
        entityClass.getSuperclass match {
          case x: Class[T] =>
            findField(name, entityClass.getSuperclass)
          case _ =>
            throw new RuntimeException("field not found: " + entityClass.getName + "[" + name + "]")
        }

    }
  }

}