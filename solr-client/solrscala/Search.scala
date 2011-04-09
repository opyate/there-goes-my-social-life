package br.com.gfuture.solrscala

/**
 * Implementa operações de indexação e busca no solr
 *
 * User: Jeosadache Galvão
 * Date: 2/8/11
 * Time: 11:06 AM
 */
class Search[T](val schemaClass: Class[T]) {

  def query(query: String): Query[T] = {
    new Query[T](query, schemaClass)
  }

}