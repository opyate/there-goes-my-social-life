package com.bopango.website.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.{SHtml, CometListener, ListenerManager, CometActor}

/**
 * "They've Bod' It!" section that feeds recent bops.
 *
 * @author Juan Uys
 */

class BopditComet extends CometActor with CometListener {
  private var msgs: List[String] = Nil

  def registerWith = BopditServer

  override def lowPriority = {
    case m: List[String] => msgs = m; reRender(false)
  }

  def render = {
    <div>
     TODO "They've Bop'd It!"
    </div>
  }
}

object BopditServer extends LiftActor with ListenerManager {
  private var messages = List("Bopdit server")

  def createUpdate = messages

  override def lowPriority = {
    case s: String => messages ::= s; updateListeners()
  }
}