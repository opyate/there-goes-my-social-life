package com.bopango.website.snippet

import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.util.BindHelpers._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, TemplateFinder, StatefulSnippet, SHtml}
import net.liftweb.util.Mailer
import net.liftweb.common.Loggable
import net.liftweb.http.js.{JsCmd, JsCmds}
import xml.{NodeSeq, Text}
import net.liftweb.http.js.JE._

/**
 * Wizard for main flow.
 *
 * @author Juan Uys
 */
class CoreStepsWizard extends StatefulSnippet with Loggable {
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
      logger.debug("Geo = " + geo)
      dispatch = {case _ => xhtml => select_restaurant}
    }

    TemplateFinder.findAnyTemplate(List("coresteps", "geo")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> FocusOnLoad(SHtml.text(geo, geo = _)),
        "submit" -> SHtml.submit("Go now!", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def select_restaurant = {
    def doSubmit () {
      registerThisSnippet
      logger.debug("Restaurant = " + restaurant)
      dispatch = {case _ => xhtml => book}
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

    def q = {
      val query = "SELECT  va.id,  ( 6371 * acos( cos( radians(@lat) ) * cos( radians( va.latitude ) ) * cos( radians( va.longitude ) - radians(@long) ) + sin( radians(@lat) ) * sin( radians( va.latitude ) ) ) ) AS distance FROM  venueaddress va HAVING distance < 25 ORDER BY distance LIMIT 0 , 20";
      //xDB.runQuery(query, List(n.itemN))
    }

    def ajaxFunc2(str: String) : JsCmd = {
      //println("Received " + str)
      JsRaw("codeAddress(null);")
    }

    //render_restaurant_data("", "")

    TemplateFinder.findAnyTemplate(List("coresteps", "restaurant")).map(xhtml =>
      bind("form", xhtml,
        "geo" -> SHtml.hidden({g => geo = g}, geo, ("id", "address")),
//        "geo" -> FocusOnLoad(ajaxText(geo, false, { v:String => println("submitting from Ajax: " + v); Run("codeAddress('"+v+"');"); }, ("id", "address"))),
//        "js" -> render_restaurant_data("", ""),
        "x" -> Script(OnLoad(SHtml.ajaxCall(Str("Rendering map from initial submission."), ajaxFunc2 _)._2)),
        "restaurant" -> SHtml.text(restaurant, restaurant = _),
        "submit" -> SHtml.submit("Bop it!", doSubmit))
    ) openOr NodeSeq.Empty
  }

  def book = {
    def doSubmit () {
      registerThisSnippet
      logger.debug("Booking = " + booking_details)
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
      logger.debug("Order = " + order_details)
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
      logger.debug("Payment = " + payment_details)
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
    logger.debug("confirmation")

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