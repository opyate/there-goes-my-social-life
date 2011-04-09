package com.bopango.website.lib

import net.liftweb.http.{XmlResponse, Req, GetRequest}
import xml.{NodeSeq, Node}
import net.liftweb.mapper.DB
import net.liftweb.common.{Loggable, Full}

/**
 * Accepts 3 parameters: latitude, longitude, and distance from.
 *
 * Returns X amount of venues in a "distance from" radius to point "latitude, longitude" in a
 * custom XML format which can be fed into the search result renderer.
 *
 * @author Juan Uys
 */

class VenueLocatorAPI extends Loggable {

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
      " c.description as description," +
      "( 6371 * acos( cos( radians(" + lat + ") ) * cos( radians( va.latitude ) ) * cos( radians( va.longitude ) - radians(" + lng + ") ) + sin( radians(" + lat + ") ) * sin( radians( va.latitude ) ) ) ) AS distance, " +
      " v.id as id" +
      " " +
      " " +
      " " +
      " " +
      " " +
      " FROM  venueaddress va " +
      " INNER JOIN venue v ON v.id = va.venue_id" +
      " INNER JOIN chain c ON v.chain_id = c.id" +
      " HAVING distance < " + radius +
      " ORDER BY distance LIMIT 0 , 8";
    val x = DB.runQuery(query, Nil)

    logger.debug("columns: %s".format(x._1))
    x._2.map(row => {
      logger.debug("row: %s".format(row))
    })

    <markers>
      {
        x._2.map(row => {
          <marker
            name={row(0)}
            address={"%s, %s, %s".format(row(1), row(2), row(3))}
            lat={row(4)}
            lng={row(5)}
            description={row(6)}
            distance={row(7)}
            id={row(8)}
          />
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