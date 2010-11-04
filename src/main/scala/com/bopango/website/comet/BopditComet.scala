package com.bopango.website.comet

import net.liftweb.actor.LiftActor
import net.liftweb.util._
import Helpers._
import net.liftweb.http.{SHtml, CometListener, ListenerManager, CometActor}
import net.liftweb.util.ActorPing
import xml.NodeSeq

/**
 * "They've Bod' It!" section that feeds recent bops.
 *
 * @author Juan Uys
 */

class BopditComet extends CometActor with CometListener {
  private var msgs: List[NodeSeq] = Nil

  def registerWith = BopditServer

  override def lowPriority = {
    case m: List[NodeSeq] => {
      msgs = m
      reRender(false)
    }
  }

  def render = {
    <div>
        {msgs.reverse.map(m => m)}
    </div>
  }
}

object BopditServer extends LiftActor with ListenerManager {
  private var messages: List[NodeSeq] = List()

  def createUpdate = messages

  override def lowPriority = {
    case s: NodeSeq => {
      messages = messages ++ s
      messages = messages.takeRight(5)
      updateListeners()

      val msg =
        <div>
          <span style="color:#884499;">{randomString(6)} &amp; 2 other guests are on their way to</span>
          <span style="color:#8888FF;">Costa Vista @ Piccadily Circus, London</span>
        </div>
      ActorPing.schedule(BopditServer, msg, 3 seconds)
    }
  }
}