package br.com.gfuture.solrscala.log

import org.slf4j.LoggerFactory

/**
 * Abstrai o log da aplicação
 *
 * User: Jeosadache Galvão
 */
trait Logged {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

  def error(message: String) = logger.error(message)

  def error(message: Unit) = logger.debug(message.toString)

  def error(throwable: Throwable) = logger.error(throwable.getMessage, throwable)

  def error(message: String, throwable: Throwable) = logger.error(message, throwable)

  def info(message: String) = logger.info(message)

  def debug(message: String) = {
    if (logger.isDebugEnabled)
      logger.debug(message)
  }

  def debug(message: Unit) = {
    if (logger.isDebugEnabled)
      logger.debug(message.toString)
  }

}