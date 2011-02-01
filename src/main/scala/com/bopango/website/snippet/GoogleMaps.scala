package com.bopango.website.snippet

import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Props
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JE._
import xml.NodeSeq
import net.liftweb.util.BindHelpers._
import net.liftweb.http.{RequestVar, SHtml, S}
import net.liftweb.common.{Full, Loggable}

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

    <lift:children>
      {head}
      {SHtml.ajaxText(searchTerm, (s) => {Call("codeAddress", s)})}
      <div id="map_canvas" style="width: 460px; height: 345px"></div>
      {Script(OnLoad(JsRaw("google_maps_init(); ")) & Call("codeAddress", searchTerm))}
    </lift:children>
  }

}