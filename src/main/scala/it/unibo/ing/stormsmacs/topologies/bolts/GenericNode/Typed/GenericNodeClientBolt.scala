package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.GenericNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import spray.json._
import it.unibo.ing.utils._
import it.unibo.ing.sigar.restful.model.SigarMeteredDataFormat._
import storm.scala.dsl.additions.Logging

/**
 * @author Antonio Murgia
 * @version 18/11/2014
 * Storm Bolt that gets Sample Data from given node
 */
class GenericNodeClientBolt(val node : GenericNodeConf)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false, "Node","GraphName","MonitData")
  with Logging
{
  override def execute(t: Tuple) : Unit = t matchSeq {
    case Seq(date: Date) =>{
      val url = node.url.toURI / date.getTime.toString
      try{
        val data = httpClient.doGET(url , node.readTimeout)
        if (data.isSuccess)
          using anchor t emit (node, date, data.content.parseJson.convertTo[SigarMeteredData])
        else
          logger error (url + ": response code not successful")
      }
      catch{
        case r : RuntimeException => logger.error(r.getMessage,r)
        case e : Throwable => logger.error(e.getMessage,e)
      }
      finally {
        t ack
      }
    }
  }
}