package it.unibo.ing.stormsmacs.topologies.bolts.Debug

import java.io.PrintWriter
import java.util.Date
import java.net.URL
import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, OpenStackNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{OpenStackSampleData}
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackSampleData
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackSampleDataRdfFormat._
import it.unibo.ing.rdf._
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource, Statistics}
import storm.scala.dsl.{Logging, StormTuple, TypedBolt}
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}

/**
 * Created by tmnd91 on 22/12/14.
 */
class osWriteToFileBolt(file: String)
  extends TypedBolt[(OpenStackNodeConf, Date, Resource, Sample), Nothing]
  with Logging{
  private var _writer : PrintWriter = null
  setup{
    _writer = new PrintWriter(file)
  }
  shutdown{
    _writer.close
  }
  override def typedExecute(t: (OpenStackNodeConf, Date, Resource, Sample), st: Tuple) : Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val url = new URL(t._1.ceilometerUrl.getProtocol + "://" +t._1.ceilometerUrl.getHost)
      val data = OpenStackSampleData(url, t._3, t._4)
      val model = data.toRdf()
      writeToRDFStore(graphName, model)
      st.ack
    }
    catch{
      case e : Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }

  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    data.write(_writer,"N-TRIPLE")
  }

}