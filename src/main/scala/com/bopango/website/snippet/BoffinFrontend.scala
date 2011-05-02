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
 * Frontend search code responsible for rendering and doing client-side API calls.
 *
 * Heavily augmented with Google Maps.
 *
 * @author Juan Uys
 */

class BoffinFrontend extends Loggable {
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
            src={"/scripts/boffin_frontend.js"}>
          </script>
          <script src="/scripts/util.js"/>
        </lift:with-resource-id>
      </head>

  def render(xhtml: NodeSeq): NodeSeq = {
    S.skipDocType = true

    val searchTerm = S.param("search") openOr {S.warning("Empty search term. Try again."); S.redirectTo(S.referer openOr "/")}

    _render(searchTerm)
  }

  private def _render(searchTerm: String): NodeSeq = {
    <div id="boffin_map">
      {head}

      <form method="post" action="/search">
          <label for="search" class="bop-hide">Search term:</label>
          {SHtml.ajaxText(searchTerm, (s) => {Call("BoffinFrontend.search", s)}, "id" -> "search", "name" -> "search", "class" -> "searchbox", "role" -> "search", "autocomplete" -> "off")}
          <input type="submit" class='searchsubmit' value=""/>
      </form>


      <div id="map_canvas"></div>
      {Script(OnLoad(JsRaw("BoffinFrontend.init(); ")) & OnLoad(Call("BoffinFrontend.search", searchTerm)))}
    </div>
  }

}