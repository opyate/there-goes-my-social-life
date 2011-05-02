package com.bopango.website.snippet

import net.liftweb.http.S
import xml.NodeSeq

/**
 *  Use like this:
 *
 * <div class="lift:browserDetection.iphone">
  Only shown to iPhone users.
</div>
 *
 * @author Juan Uys
 */

class BrowserDetection {
  def iphone(in: NodeSeq) : NodeSeq =
    S.request.toList.filter( _.isIPhone ).flatMap( _ => in)

  def other(in: NodeSeq) : NodeSeq =
    S.request.toList.filter(req => {!req.isIPhone}).flatMap( _ => in)
}
