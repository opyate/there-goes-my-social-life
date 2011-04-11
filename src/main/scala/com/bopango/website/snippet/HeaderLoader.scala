


package com.bopango.website.snippet

import xml.{NodeSeq}
import net.liftweb._
import common.Full
import util.Props
import util.Helpers._

/**
 * See http://code.google.com/apis/loader/
 *
 * TODO read the above documentation and see if there's any other funky things we can do.
 *
 * Use local or CDN jQuery:
 *
 * <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.9/jquery-ui.min.js"></script>
    <script src="http://cdn.jquerytools.org/1.2.5/all/jquery.tools.min.js"></script>

Local is better for off-line development.
 */
object HeaderLoader {
	def render(xhtml: NodeSeq): NodeSeq = {
    val src = "https://www.google.com/jsapi?key=" + (Props.get("gmaps.api") match {
      case Full(apiKey) => apiKey
      case _ => "undefined"
    })

//    "*" #> <lift:with-resource-id>
//      <script type="text/javascript" src={src}></script>
//      <script type="text/javascript" src="/scripts/jquery/jquery.min.js"></script>
//      <script type="text/javascript" src="/scripts/jquery/jquery-ui.min.js"></script>
//      <script src="/scripts/jquery/jquery.tools.min.js"></script>
//      <script id="json" src="/classpath/json.js" type="text/javascript"></script>
//    </lift:with-resource-id>
    
    <lift:children>
      <script id="googlejsapi" type="text/javascript" src={src}></script>
    </lift:children>
  }
}