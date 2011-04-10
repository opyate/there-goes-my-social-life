package com.bopango.website.lib

import net.liftweb.common.Loggable
import scala.xml.{NodeSeq, Node}
import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST.JValue
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.{SolrDocument, SolrDocumentList}
import org.apache.solr.client.solrj.{SolrServerException, SolrQuery}
import net.liftweb.util.Props
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer

/**
 * Bopango Finder, aka Boffin.
 *
 * @author Juan Uys
 */

class BoffinHelper extends Loggable {

  private def g(searchTerm: String): Node = {

    // use the search term and see if we index any special results against it
    logger.debug("Search query [%s]".format(searchTerm))
    val solrQuery = new SolrQuery()
    solrQuery.setQuery(searchTerm)
    solrQuery.setRows(10)

    val return_xml = try {
      val queryResponse: QueryResponse = BopangoSolrServer.query(solrQuery)
      val documentList: SolrDocumentList = queryResponse.getResults()

      import scala.collection.JavaConversions._

      <markers>
      {
        documentList.map((s: SolrDocument) => {
          val lat = s.getFieldValue("latitude").asInstanceOf[Double].toString
          val lng = s.getFieldValue("longitude").asInstanceOf[Double].toString

          <marker
            name={s.getFieldValue("venue_name") match {
              case x: String => x
              case _ => throw new ClassCastException
            }}
            address={"venue address"}
            lat={lat}
            lng={lng}
            description={s.getFieldValue("venue_description") match {
              case x: String => x
              case _ => throw new ClassCastException
            }}
            distance={""}
            id={s.getFieldValue("id") match {
              case x: String => x
              case _ => throw new ClassCastException
            }}
          />
        })
      }
    </markers>


    }
    catch {
      case s: SolrServerException => {
        logger.error("No Solr results.")
        <markers></markers>
      }
    }

    return_xml
  }

  def get(query: String): Node = {

    logger.info("Boffin Search term: %s".format(query))

    g(query)
  }


  // use like: val str = iWant[String](getSomething("key")).getOrElse("")
  private def iWant[A](v: Object)(implicit m: scala.reflect.Manifest[A]): Option[A] = {
    if (m.erasure.isInstance(v)) {
      return Some(v.asInstanceOf[A])
    } else {
      return None
    }
  }
}


object BoffinAPI extends RestHelper {
  val api = new BoffinHelper()

  serve {
    // this seems to be greedy:
    //case "api" :: "search" :: "restaurants" :: Nil XmlGet _ => api.get("*:*"): Node
    // TODO no 'q' throws exception on .head below (head on empty list)
    case "api" :: "search" :: "restaurants" :: q XmlGet _ => {
      (for {
        searchString <- q ::: S.params("q")
        res <- api.get(searchString)
      } yield res).distinct.head: Node
    }
  }

}

class RestaurantSearchResult {

}

object BopangoSolrServer {

  // constructor takes server url up to the core bit as an argument, e.g.
  // http://bopango.net:8983/solr/restaurants

  val solrServer = new CommonsHttpSolrServer("http://%s:%s/%s/%s".format(
      Props.get("solr.server", "localhost"),
      Props.get("solr.port", "8983"),
      Props.get("solr.webapp", "solr"),
      Props.get("solr.core", "restaurants")
    ))
  solrServer.setSoTimeout(10000)
  solrServer.setAllowCompression(true)
  solrServer.setDefaultMaxConnectionsPerHost(20)
  solrServer.setFollowRedirects(false)
  solrServer.setMaxRetries(1)

  def query(query: SolrQuery): QueryResponse = {
    solrServer.query(query)
  }
}