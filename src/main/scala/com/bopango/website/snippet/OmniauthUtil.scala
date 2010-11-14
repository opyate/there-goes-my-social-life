package com.bopango.website.snippet

import xml.NodeSeq
import net.liftweb.common.{Failure, Empty, Full}
import com.bopango.website.view.Omniauth

class OmniauthUtil {
  def info(xhtml: NodeSeq) = {
    Omniauth.currentAuthMap match {
      case Full(omni) => omni.map { s => <p>{s}</p> } toSeq
      case Empty => NodeSeq.Empty
      case Failure(_,_,_) => NodeSeq.Empty
    }  
  }

  def as(xhtml: NodeSeq) = {
    Omniauth.currentAuthMap match {
      case Full(omni) => {
        val name = omni.get(Omniauth.UserInfo) match {
          case Some(info:Map[String,Any]) => {
            info.get(Omniauth.Name)
          }
          case _ => None
        }

        name match {
          case Some(s) => <span>{s}</span>
          case _ => <span>(private)</span>
        }
      }
      case Empty => NodeSeq.Empty
      case Failure(_,_,_) => NodeSeq.Empty
    }
  }
}

object OmniauthUtil {
  def logged_in():Boolean = {
    Omniauth.currentAuthMap match {
      case Full(omni) => true
      case _ => false
    }
  }
}