package it.unibo.ing.stormsmacs.topologies.bolts

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.utils._
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}

/**
 * Created by Antonio on 02/03/2015.
 */
trait VirtuosoPersister {
  protected def writeToRDFStore(virtuosoEndPoint : FusekiNodeConf, graphName: String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val set: VirtGraph = new VirtGraph (virtuosoEndPoint.url, virtuosoEndPoint.username, virtuosoEndPoint.password)
    val str = s"INSERT DATA { GRAPH ${graphName} { ${dataAsString} } }"
    val vur = VirtuosoUpdateFactory.create(str, set)
    vur.exec()
  }
}
