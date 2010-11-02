package com.bopango.website.snippet.wizard

import net.liftweb.http.{S, LiftScreen}

/**
 *
 *
 * @author Juan Uys
 */
class ChooseGeoScreen extends LiftScreen {
  object geo extends ScreenVar()

  override def screenTop =
  <b>geo screen</b>

  val flavor = field(S ? "What's your favorite Ice cream flavor", "")

  def finish() = {}
}