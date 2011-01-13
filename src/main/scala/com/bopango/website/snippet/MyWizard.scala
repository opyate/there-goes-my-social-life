package com.bopango.website.snippet

/**
 * TODO Javadoc here...
 *
 * @author Juan Uys
 */

import net.liftweb.wizard._

object MyWizard extends Wizard {

  val theScreens = (1 to 100).toList.map {
    i => new Screen {
      val name = field("name", "")
      val another = field("Add Another?", false)

      override def nextScreen = if (another) super.nextScreen else lastScreen
    }
  }

  val lastScreen = new Screen {
    val done = field("Done?", "")
  }

  def finish() {
  }

}