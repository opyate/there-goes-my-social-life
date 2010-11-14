package com.bopango.website.snippet

import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.util.BindHelpers._
import net.liftweb.http.SHtml._
import xml.{Text, NodeSeq}
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, TemplateFinder, StatefulSnippet, SHtml}
import net.liftweb.util.Mailer

/**
 * Wizard for main flow.
 *
 * @author Juan Uys
 */
class CoreStepsWizard extends StatefulSnippet {
  val fromWhence = S.referer openOr "/"
  var dispatch: DispatchIt = {case _ => xhtml => select_geo}

  var geo = ""
  var restaurant = "wizard.default.restaurant"
  var booking_details = "wizard.default.booking"
  var order_details = "wizard.default.order"
  var payment_details = "wizard.default.payment"

  def select_geo = {
    def doSubmit () {
      registerThisSnippet
      println("Geo = " + geo)
      dispatch = {case _ => xhtml => select_restaurant}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "geo")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> FocusOnLoad(SHtml.text(geo, geo = _)),
        "submit" -> SHtml.submit("Go now!", doSubmit))
    ) openOr NodeSeq.Empty
  }

//  def select_geo2(xhtml: NodeSeq): NodeSeq = {
//    bind("form", xhtml,
//      "geo" -> ajaxText("Enter a postcode, area, city...", {g => println(g); geo = g; S.fmapFunc(() => registerThisSnippet)(binding => JsCmds.RedirectTo("/coresteps/restaurant?" + binding + "=_"))
//    }))
//  }

  def select_restaurant = {
    def doSubmit () {
      registerThisSnippet
      println("Restaurant = " + restaurant)
      dispatch = {case _ => xhtml => book}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "restaurant")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> SHtml.hidden({g => geo = g}, geo, ("id", "address")),
        "restaurant" -> SHtml.text(restaurant, restaurant = _),
        "submit" -> SHtml.submit("Bop it!", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def book = {
    def doSubmit () {
      registerThisSnippet
      println("Booking = " + booking_details)
      dispatch = {case _ => xhtml => order}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "book")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> Text(geo),
        "restaurant" -> Text(restaurant),
        "booking" -> SHtml.text(booking_details, booking_details = _),
        "submit" -> SHtml.submit("Continue", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def order = {
    def doSubmit () {
      registerThisSnippet
      println("Order = " + order_details)
      dispatch = {case _ => xhtml => pay}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "order")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> Text(geo),
        "restaurant" -> Text(restaurant),
        "booking" -> Text(booking_details),
        "order" -> SHtml.text(order_details, order_details = _),
        "submit" -> SHtml.submit("Continue", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def pay = {
    def doSubmit () {
      registerThisSnippet
      println("Payment = " + payment_details)
      dispatch = {case _ => xhtml => confirmation}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "pay")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> Text(geo),
        "restaurant" -> Text(restaurant),
        "booking" -> Text(booking_details),
        "order" -> Text(order_details),
        "payment" -> SHtml.text(payment_details, payment_details = _),
        "submit" -> SHtml.submit("Continue", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def confirmation = {
    println("confirmation")

//    Mailer.sendMail(
//      From("Bopango <confirmation@bopango.net>"),
//      Subject("Confirmation of your reservation")
//      )

    TemplateFinder.findAnyTemplate(List("coresteps", "confirmation")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> Text(geo),
        "restaurant" -> Text(restaurant),
        "booking" -> Text(booking_details),
        "order" -> Text(order_details),
        "payment" -> Text(payment_details))
    ) openOr NodeSeq.Empty
  }
}