package io.prediction.sentimentanalysis

import io.prediction.controller.{EmptyEvaluationInfo, PDataSource}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import io.prediction.data.store.PEventStore

import io.prediction.sentimentanalysis.ActualResult

import grizzled.slf4j.Logger

class DataSource(val dsp: DataSourceParams) extends PDataSource[
  TrainingData,
  EmptyEvaluationInfo,
  Query,
  ActualResult] {
  
  @transient lazy val logger = Logger[this.type]

  override def readTraining(sc: SparkContext): TrainingData = {
    TrainingData()
  }
  
  override
  def readEval(sc: SparkContext)
  : Seq[(TrainingData, EmptyEvaluationInfo, RDD[(Query, ActualResult)])] = {

    // The following code reads the data from data store. It is equivalent to
    // the readTraining method. We copy-and-paste the exact code here for
    // illustration purpose, a recommended approach is to factor out this logic
    // into a helper function and have both readTraining and readEval call the
    // helper.
    val labeledPoints: RDD[TweetSentiment] = PEventStore.aggregateProperties(
      appName = "Tweet_Sentiment_Analysis",
      entityType = "user",
      // only keep entities with these required properties defined
      required = Some(List("tweet", "sentiment")))(sc)
      // aggregateProperties() returns RDD pair of
      // entity ID and its aggregated properties
      .map { case (entityId, properties) =>
        try {
          new TweetSentiment(properties.get[String]("tweet"),
            properties.get[Int]("sentiment")
          )
        } catch {
          case e: Exception => {
            logger.error(s"Failed to get properties ${properties} of" +
              s" ${entityId}. Exception: ${e}.")
            throw e
          }
        }
      }.cache()
      // End of reading from data store

    val indexedPoints: RDD[(TweetSentiment, Long)] = labeledPoints.zipWithIndex()
    val evalK = indexedPoints.count().toInt
    (0 until evalK).map { idx =>
      val trainingPoints = indexedPoints.filter(_._2 % evalK == idx).map(_._1)
      val testingPoints = indexedPoints.filter(_._2 % evalK != idx).map(_._1)

      (
        TrainingData(),
        new EmptyEvaluationInfo(),
        testingPoints.map {
          p => (new Query(p.tweet), new ActualResult(p.sentiment))
        }
      )
    }
  }
}

class TweetSentiment(val tweet: String, val sentiment: Int) extends Serializable

