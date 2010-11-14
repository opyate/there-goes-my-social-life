package com.bopango.website.snippet

import net.liftweb.http.S
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Props

/**
 * Google Maps rendering
 *
 * @author Juan Uys
 */

class GoogleMaps {

  def render = {
    S.skipDocType = true
    val apiKey = Props.get("gmaps.api").open_!
    val src = "http://maps.google.com/maps/api/js?sensor=false&amp;key="+apiKey

    <head>
      <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
      <script
        type="text/javascript"
        src={src}>
      </script>
      <script
        type="text/javascript"
        src="/scripts/bopango_googlemaps.js">
      </script>
    </head>
    <div id="map_canvas" style="width: 460px; height: 345px"></div> ++=
    {Script(OnLoad(JsRaw("google_maps_init(); codeAddress()")))}
  }
}