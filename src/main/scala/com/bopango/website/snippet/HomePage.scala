package com.bopango.website.snippet

import xml.NodeSeq
/**
 * Home page
 *
 * @author Juan Uys
 */

class HomePage {
  def render(in: NodeSeq): NodeSeq = {
    <lift:embed what="template/home/_home"/>
  }
}