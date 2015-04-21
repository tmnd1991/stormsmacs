package it.unibo.ing.stormsmacs.topologies.reliable.bolts.OpenStack

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.additions.Logging
import storm.scala.dsl.StormBolt

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Storm Bolt that lists all the samples of a given Resource that will be monitored
 */
class OpenStackSampleBolt(pollTime: Long)
  extends StormBolt(List("NodeName", "GraphName", "Resource", "Sample"))
  with Logging {
  override def execute(input: Tuple) = {
    input matchSeq {
      case Seq(node: OpenStackNodeConf, date: Date, resource: Resource) =>
        val cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
        val start = new Date(date.getTime - pollTime)
        cclient.tryGetSamplesOfResource(resource.resource_id, start, date) match {
          case Some(Nil) =>
            logger.info("ack - no samples")
            input ack //no samples for this resource, we just ack the tuple
          case Some(samples: Seq[Sample]) =>
            for (s <- samples)
              using anchor input emit(node, date, resource, s)
            logger.info("ack - some samples")
            input ack
          case _ =>
            //if we get None as a result, something bad happened, we need to replay the tuple
            logger.info("fail - ceilometerclient error")
            input fail
        }
    }
  }
}