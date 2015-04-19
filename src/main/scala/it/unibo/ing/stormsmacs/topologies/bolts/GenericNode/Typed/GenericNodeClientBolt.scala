package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.net.URI
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
class GenericNodeClientBolt(val node : GenericNodeConf, val pollTime: Long)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false, "Node","GraphName","MonitData")
  with Logging
{
  require (pollTime > 0)
  override def execute(t: Tuple) : Unit = t matchSeq {
    case Seq(date: Date) =>{
      val url: URI = node.url.toURI / (date.getTime - pollTime).toString / date.getTime.toString
      try{
        val data = httpClient.doGET(url , node.readTimeout)
        if (data.isSuccess){
          import spray.json.DefaultJsonProtocol._
          val convertedData = data.content.parseJson.convertTo[Seq[SigarMeteredData]]
          for (d <- convertedData)
            using anchor t emit (node, date, d)
        }
        else
          logger error (url + ": response code not successful")
      }
      catch{
        case r : RuntimeException => logger.error(r.getMessage,r)
        case e : Throwable => logger.error(e.getMessage,e)
      }
      t ack
    }
  }
}