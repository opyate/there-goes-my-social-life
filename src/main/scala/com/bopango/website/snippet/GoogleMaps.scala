package com.bopango.website.snippet

import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Props
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JE._
import xml.NodeSeq
import net.liftweb.util.BindHelpers._
import net.liftweb.http.{RequestVar, SHtml, S}
import net.liftweb.common.{Full, Loggable}
import org.apache.solr.client.solrj.impl._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.{SolrServerException, SolrQuery}
import org.apache.solr.common.{SolrDocument, SolrDocumentList}

/**
 * Renders an input box and a Google Map. The input is geoCoded on the client,
 * the resulting long/lat is sent to the proximity server, and the result
 * is rendered in a DIV with id 'sidebar'.
 *
 * @author Juan Uys
 */

class GoogleMaps extends Loggable {
  //private val apiKey = Props.get("gmaps.api") openOr "undefined"
  private lazy val src = "http://maps.google.com/maps/api/js?sensor=false&amp;key=" + (Props.get("gmaps.api") match {
    case Full(apiKey) => apiKey
    case _ => "undefined"
  })

  def head:NodeSeq = <head>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
        <script
          type="text/javascript"
          src={src}>
        </script>
        <lift:with-resource-id>
          <script
            type="text/javascript"
            src={"/scripts/bopango_googlemaps.js"}>
          </script>
        </lift:with-resource-id>
        <script src="/scripts/util.js"/>
      </head>

  def render(xhtml: NodeSeq): NodeSeq = {
    S.skipDocType = true

    val searchTerm = S.param("search") openOr {S.warning("Empty search term. Try again."); S.redirectTo(S.referer openOr "/")}

    // use the search term and see if we index any special results against it
    logger.debug("Search query [%s]".format(searchTerm))
    val solrQuery = new SolrQuery()
    solrQuery.setQuery(searchTerm)
    solrQuery.setRows(10)

    val return_xml = try {
      val queryResponse: QueryResponse = SolrServer.query(solrQuery)
      val documentList: SolrDocumentList = queryResponse.getResults()

      import scala.collection.JavaConversions._

      /**
       * Example Solr doc:
       *
       * SolrDocument[
       * {id=1,
       * venue_name=Panache @ Oxford Street,
       * venue_description=The Oxford Street branch of Panache,
       * restaurant_url=http://bopango.net:8080/bopango/book?restaurant_id=1,
       * chain_name=Panache,
       * chain_description=Panache serves the best of French cuisine in a relaxed, casual environment. We pride ourselves of serving the best quality ingredients, cooked in the traditional Ffashion with a British twist added to it.,
       * cuisine_description=French,
       * cuisine_name=French}]
       */

      documentList.foreach((s: SolrDocument) => logger.info("Found a solr doc [%s]".format(s)))

      // TODO render the Solr search results, and put each restaurant in the search results
      // on the neighbouring Google Map using a placeMarker function etc

      renderDeferToGoogleMapsAPIWithLatLong(51.51158, 0)
    }
    catch {
      case s: SolrServerException => {
        logger.error("Solr query error, defering to Google Maps API")

        renderDeferToGoogleMapsAPI(searchTerm)
      }
    }

    return_xml
  }

  /**
   * TODO this is good headway, but break the Javascript into something that accepts an array
   * of google.maps.LatLng objects
   */
  private def renderDeferToGoogleMapsAPIWithLatLong(lat: Double, lng: Double): NodeSeq = {

    // call something like this from Scala code:
    // centreAndSearchLocationsNear(new google.maps.LatLng(51.51158, 0, true))

    val toCall = "centreAndSearchLocationsNear(new google.maps.LatLng(%s, %s, true));".format(lat, lng)

    <lift:children>
      {head}
      {SHtml.ajaxText("blah", (s) => {Call("codeAddress", s)})}
      <div id="map_canvas" style="width: 460px; height: 345px"></div>
      {Script(OnLoad(JsRaw("google_maps_init(); ")) & OnLoad(JsRaw(toCall)))}
    </lift:children>
  }

  private def renderDeferToGoogleMapsAPI(searchTerm: String): NodeSeq = {
    <lift:children>
      {head}
      {SHtml.ajaxText(searchTerm, (s) => {Call("codeAddress", s)})}
      <div id="map_canvas" style="width: 460px; height: 345px"></div>
      {Script(OnLoad(JsRaw("google_maps_init(); ")) & Call("codeAddress", searchTerm))}
    </lift:children>
  }

}

object SolrServer {

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