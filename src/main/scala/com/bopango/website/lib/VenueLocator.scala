package com.bopango.website.lib

import net.liftweb.common.Full
import net.liftweb.http.{XmlResponse, Req, GetRequest}
import xml.{NodeSeq, Node}
import net.liftweb.mapper.DB

/**
 * TODO Javadoc here...
 *
 * @author Juan Uys
 */

class VenueLocatorAPI {
  def get2(lat: String, lng: String, radius: String): Node = {
    println("api was called")
    <markers>
      <marker name="Round Table Pizza: Mountain View" address="570 N Shoreline Blvd, Mountain View, CA" lat="37.402653" lng="-122.079353" distance="0.38091455044131"/>
      <marker name="Kapp's Pizza Bar and  Grill" address="191 Castro St, Mountain View, CA" lat="37.393887" lng="-122.078918" distance="0.5596115438175"/>
      <marker name="Amici's East Coast Pizzeria" address="790 Castro St, Mountain View, CA" lat="37.387138" lng="-122.083237" distance="1.0796074495809"/>
      <marker name="Frankie Johnnie and  Luigo Too" address="939 W El Camino Real, Mountain View, CA" lat="37.386337" lng="-122.085823" distance="1.2044231336188"/>
      <marker name="Tony and  Alba's Pizza and  Pasta" address="619 Escuela Ave, Mountain View, CA" lat="37.394012" lng="-122.095528" distance="1.3156538737837"/>
      <marker name="Round Table Pizza: Sunnyvale-Mary-Central Expy" address="415 N Mary Ave, Sunnyvale, CA" lat="37.390038" lng="-122.042030" distance="1.84565061776"/>
      <marker name="Oregano's Wood-Fired Pizza" address="4546 El Camino Real, Los Altos, CA" lat="37.401726" lng="-122.114647" distance="2.2887425990519"/>
    </markers>
  }

  /**
   * TODO limit below based on viewport size.
   *
   * http://localhost.local:8080/bopango/api/venues/51.51158/-0.12462/25
   * yields:
   * (List(id, distance),List(List(1, 0.0), List(3, 0.493663093126805), List(6, 0.633158299660592), List(5, 0.961363053962538), List(2, 2.0427943406994), List(4, 3.14647599494914)))
   */
  def get(lat: String, lng: String, radius: String): Node = {
    val query = "SELECT v.name as name," +
      " va.address1 as address1," +
      " va.postcode as postcode," +
      " va.city as city," +
      " va.latitude as lat," +
      " va.longitude as lng," +
      " " +
      " " +
      " " +
      " " +
      " va.id, "+
      "( 6371 * acos( cos( radians(" + lat + ") ) * cos( radians( va.latitude ) ) * cos( radians( va.longitude ) - radians(" + lng + ") ) + sin( radians(" + lat + ") ) * sin( radians( va.latitude ) ) ) ) AS distance " +
      " FROM  venueaddress va " +
      " INNER JOIN venue v ON v.id = va.venue_id" +
      " HAVING distance < " + radius +
      " ORDER BY distance LIMIT 0 , 8";
    val x = DB.runQuery(query, Nil)

    println("columns: " + x._1)
    x._2.map(row => {
      println("row: " + row)
    })

    <markers>
      {
        x._2.map(row => {
          <marker name={row(0)} address={row(1)+", "+row(2)+", "+row(3)} lat={row(4)} lng={row(5)} distance={row(6)}/>
        })
      }
    </markers>
  }
}


object VenueLocatorAPI extends WsEndpoint {
  val api = new VenueLocatorAPI()

  override def wsDispatchRules =
  {
    case Req("api" :: "venues" :: lat :: lng :: radius :: _, _, GetRequest) => () => api.get(lat, lng, radius)
  }
}


trait WsEndpoint {
  def wsDispatchRules: PartialFunction[Req, () => Node]

  def dispatchRules: PartialFunction[Req, () => Full[XmlResponse]] = {
    new MyAdapter(wsDispatchRules)
  }
  abstract class PartialFunctionAdapter[F, T1, T2](adaptee:
  PartialFunction[F, T1]) extends PartialFunction[F, T2] {
    override def isDefinedAt(r: F) = adaptee.isDefinedAt(r)

    override def apply(r: F) = {
      converter(adaptee.apply(r))
    }

    def converter(x: T1): T2
  }
  class MyAdapter(adaptee: PartialFunction[Req, () => Node])
          extends PartialFunctionAdapter[Req, () => Node, () =>
                  Full[XmlResponse]](adaptee) {
    override def converter(x: () => Node) = {
      () => Full(XmlResponse(x()))
    }
  }
}