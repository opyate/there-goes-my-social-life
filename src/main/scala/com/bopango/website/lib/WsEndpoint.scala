package com.bopango.website.lib

import net.liftweb.common.Full
import net.liftweb.http.{XmlResponse, Req}
import xml.Node

/**
 * Web service endpoint.
 *
 * @author Juan Uys
 */

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