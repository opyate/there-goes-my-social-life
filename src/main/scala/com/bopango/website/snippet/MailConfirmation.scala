package com.bopango.website.snippet

import xml.NodeSeq
import scala.collection.mutable.{Map => MMap}
import com.bopango.website.model.{User, Dish, Reservation}
import net.liftweb.http.S

/**
 * TODO Javadoc here...
 *
 * @author Juan Uys
 */

object MailConfirmation {

  def guest(reservation: Reservation, guest_selection: MMap[Int, MMap[Dish, Int]]): NodeSeq = {
    <html>
      <head>
        <title>Bopango Order Confirmation</title>
      </head>
      <body>
        <table bgcolor="white" style="color:black;border=0;valign=top;align=left;">
          <thead>
            <tr>
              <td colspan="3" style="font-size:biggest;color:black;border=0;valign=top;align=left;">Bopango Order Confirmation</td>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td colspan="3" style="font-size:bigger;color:black;border=0;valign=top;align=left;">Reservation details</td>
            </tr>

          {display_reservation_details(reservation)}

            <tr>
              <td colspan="3" style="font-size:bigger;color:black;border=0;valign=top;align=left;">Guest selections</td>
            </tr>

          {display_guest_selection(guest_selection)}
          </tbody>
        </table>
      </body>
    </html>
  }

  private def display_reservation_details(reservation: Reservation): NodeSeq = {
    <lift:children>
        <tr>
          <td colspan="2" style="color:black;border=0;valign=top;align=left;">Reservation made by</td><td style="color:black;border=0;valign=top;align=left;">{User.currentUser.open_!.email.is}</td>
        </tr>
        <tr>
          <td colspan="2" style="color:black;border=0;valign=top;align=left;">Date of reservation</td><td style="color:black;border=0;valign=top;align=left;">{java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, S.locale).format(reservation.when.is)}</td>
        </tr>
        <tr>
          <td colspan="2" style="color:black;border=0;valign=top;align=left;">Time of reservation</td><td style="color:black;border=0;valign=top;align=left;">{java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, S.locale).format(reservation.what_time.is)}</td>
        </tr>
        <tr>
          <td colspan="2" style="color:black;border=0;valign=top;align=left;">Duration</td><td style="color:black;border=0;valign=top;align=left;">{reservation.how_much_time_in_minutes.is}</td>
        </tr>
        <tr>
          <td colspan="2" style="color:black;border=0;valign=top;align=left;">Number of guests</td><td style="color:black;border=0;valign=top;align=left;">{reservation.number_of_guests.is}</td>
        </tr>
    </lift:children>
  }

  private def display_guest_selection(guest_selections: MMap[Int, MMap[Dish, Int]]): NodeSeq = {
    val totals = MMap.empty[Int, Double]
    var total = 0.0
    guest_selections.map{case (guest_number:Int, dishesAndQuantities:MMap[Dish,Int]) => {
      val guest_total: Double = dishesAndQuantities.foldLeft(0.0){case (a, (d:Dish, v:Int)) => {
        a + (v * d.cost.is)
      }}
      totals(guest_number) = guest_total
      total += guest_total

      val selection = guest_selections(guest_number)
      if (selection.isEmpty) {
          <tr>
            <td colspan="2">Guest #{guest_number+1}</td>
            <td>No selection</td>
          </tr>
      } else {
          <lift:children>
              <tr>
                <td colspan="3">Guest #{guest_number+1}</td>
              </tr>
              <tr>
                  <td style="color:black;border=0;valign=top;align=left;">Dish</td>
                  <td style="color:black;border=0;valign=top;align=left;">Quantity</td>
                  <td style="color:black;border=0;valign=top;align=left;">Cost</td>
              </tr>
            {selection.map{case (dish,q) =>
              <tr>
                <td style="color:black;border=0;valign=top;align=left;">{dish.name}</td>
                <td style="color:black;border=0;valign=top;align=left;">{q}</td>
                <td style="color:black;border=0;valign=top;align=left;">{String.format("%3.2f", double2Double(dish.cost))}</td>
              </tr>}}
              <tr>
                <td style="color:black;border=0;valign=top;align=left;">Guest total</td>
                <td style="color:black;border=0;valign=top;align=left;">&nbsp;</td>
                <td style="color:black;border=0;valign=top;align=left;">{String.format("%3.2f", double2Double(guest_total))}</td>
              </tr>

          </lift:children>
      }
    }}.toSeq ++ <tr><td colspan="3" style="font-size:biggest;color:black;border=0;valign=top;align=left;">Total: {String.format("%3.2f", double2Double(total))}</td></tr>
  }
}