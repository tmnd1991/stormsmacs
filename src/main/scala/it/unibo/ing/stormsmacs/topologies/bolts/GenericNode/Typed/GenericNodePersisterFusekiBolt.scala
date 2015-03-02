package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.FusekiPersister
import org.eclipse.jetty.client.HttpClient

/**
 * Created by tmnd91 on 24/12/14.
 */
class GenericNodePersisterFusekiBolt(fusekiEndpoint : FusekiNodeConf)
  extends GenericNodePersisterBolt(fusekiEndpoint) with FusekiPersister
{
  private var httpClient: HttpClient = _

  setup {
    httpClient = new HttpClient()
    httpClient.setConnectTimeout(1000)
    httpClient.setMaxRedirects(1)
    httpClient.start()
  }
  shutdown{
    if (httpClient.isStarted)
      httpClient.stop()
    httpClient = null
  }
  /*
  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val str = s"INSERT DATA { GRAPH $graphName { $dataAsString } }"

    val exchange = new ContentExchange()
    exchange.setURI(new URI(fusekiEndpoint.url / "update"))
    exchange.setMethod("POST")
    exchange.setRequestContentType("application/sparql-update")
    exchange.setRequestContent(new ByteArrayBuffer(str))
    httpClient.send(exchange)
    val state = exchange.waitForDone()
    if ((exchange.getResponseStatus/100) != 2){
      logger.info(str)
      throw new Exception(s"Cannot sparql update: ${exchange.getResponseStatus} -> ${exchange.getResponseContent}")
    }
  }
  */
  override protected def writeToRDF(graphName: String, data: Model) : Unit = writeToRDFStore(fusekiEndpoint, httpClient, graphName, data)
}