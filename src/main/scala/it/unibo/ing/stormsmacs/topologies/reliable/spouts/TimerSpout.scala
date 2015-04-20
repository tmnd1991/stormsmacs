package it.unibo.ing.stormsmacs.topologies.reliable.spouts

import it.unibo.ing.stormsmacs.topologies.facilities.DefaultFailHandler
import storm.scala.dsl.additions.Logging
import storm.scala.dsl.{StormSpout}

import backtype.storm.utils.Utils
import java.util.Date

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Spout that gives the clock to the entire stormsmacs topology
 */

class TimerSpout(pollTime : Long) extends StormSpout(List("GraphName")) with Logging{
  val failHandler = new DefaultFailHandler(2)
  override def nextTuple = {
    Utils.sleep(pollTime)
    val now = new Date()
    using msgId(now.getTime) emit (now)
  }
  override def ack(messageId: Any): Unit = {
    failHandler.acked(messageId.asInstanceOf[Long])
    logger info "acked " + new Date(messageId.asInstanceOf[Long])
  }
  override def fail(messageId: Any) : Unit = {
    val msgId = messageId.asInstanceOf[Long]
    val dateToBeReplayed = new Date(msgId)
    if (failHandler shouldBeReplayed msgId){
      failHandler replayed msgId
      using msgId(dateToBeReplayed.getTime) emit (dateToBeReplayed)
      logger info s"replay n° ${failHandler.failCount(msgId)+1} $dateToBeReplayed"
    }
    else{
      logger info "not replayed " + dateToBeReplayed
      failHandler acked msgId
    }

  }
}