package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.ClientBolt
import spray.json._
import storm.scala.dsl.Logging


/**
 * @author Antonio Murgia
 * @version 18/11/14
 */
class CloudFoundryNodeClientBolt(val node : CloudFoundryNodeConf)
    extends ClientBolt(List("Node","GraphName","MonitData"), node.connectTimeout, node.readTimeout)
    with Logging
{
  override def emitData(t : Tuple, graphName : Date) = {
    val response = httpClient.GET(node.url.toURI)
    val body = response.getContentAsString
    logger.info(body)
    import spray.json.DefaultJsonProtocol._
    val data = body.parseJson.convertTo[Seq[MonitInfo]]
    for(d <- data)
      using anchor t emit (node, graphName, d)
    //no need to ack because it's already acked in ClientBolt if not failed obviously
  }
}