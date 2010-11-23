package com.bopango.website.snippet

import net.liftweb.http.S
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Props
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JE._
import net.liftweb.common.Loggable

/**
 * Google Maps rendering
 *
 * @author Juan Uys
 */

class GoogleMaps extends Loggable {

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
        src={"/scripts/bopango_googlemaps.js?uniq=" + System.currentTimeMillis.toString}>
      </script>
      <script src="/scripts/util.js"/>
    </head>
    <div id="map_canvas" style="width: 460px; height: 345px"></div> ++=
    {
      Script(OnLoad(JsRaw("google_maps_init(); ")))

    } /*++=
    {
      render_restaurant_data("", "")
    }*/

  }

  def render_restaurant_data(lat: String, lng: String) = {
    logger.debug("render_restaurant_data("+lat+", "+lng+")")
    Script(JsCrVar("restaurant_data",
      JsArray(
        JsArray(Str("Costa"), Num(51.548982), Num(-0.148573), Num(4), Str("<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>")),
        JsArray(Str("Wagamama"), Num(51.551876), Num(-0.145873), Num(1), Str("<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"))
        )
      ))
  }

  def render_restaurant_dummy_data = {
    Script(JsRaw("""
    
      var restaurant_data = [
        ['Costa', 51.548982, -0.148573, 4, "<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"],
        ['Wagamama', 51.549873, -0.147573, 5, "<img src=\"images/restaurants/wagamama.png\"/><br/><br/><strong>Address:</strong><br/>66 Elm Street<br/>London<br/>NW5 6HH<br/>Phone: 0207 998 5544<br/>Email: <a href=\"#\">contact@wagamama.com</a><br/><br/>"],
        ['Costa', 51.547874, -0.146573, 3, "<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"],
        ['Wagamama', 51.550875, -0.145573, 2, "<img src=\"images/restaurants/wagamama.png\"/><br/><br/><strong>Address:</strong><br/>66 Elm Street<br/>London<br/>NW5 6HH<br/>Phone: 0207 998 5544<br/>Email: <a href=\"#\">contact@wagamama.com</a><br/><br/>"],
        ['Costa', 51.551876, -0.145873, 1, "<img src=\"images/restaurants/costa.png\"/><br/><br/><strong>Address:</strong><br/>21 Jump Street<br/>London<br/>NW5 3XG<br/>Phone: 0207 555 1234<br/>Email: <a href=\"#\">contact@costa-vista.com</a><br/><br/>"]
      ];

    """))
  }
}