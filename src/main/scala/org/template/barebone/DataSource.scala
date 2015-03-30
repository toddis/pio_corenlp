package org.template.barebone

import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations.GoldClass
import edu.stanford.nlp.sentiment.SentimentUtils
import edu.stanford.nlp.trees.Tree
import io.prediction.controller.{EmptyActualResult, EmptyEvaluationInfo, PDataSource}
import io.prediction.data.storage.Storage
import org.apache.spark.SparkContext

class DataSource(val dsp: DataSourceParams) extends PDataSource[
  TrainingData,
  EmptyEvaluationInfo,
  Query,
  EmptyActualResult] {

  override def readTraining(sc: SparkContext): TrainingData = {
    val events = Storage.getPEvents().find(appId = dsp.appId, entityType = Some("tree"))(sc)

    val trainingTrees = events.map { event =>
      val tree = Tree.valueOf(event.properties.get[String]("tree"))
      SentimentUtils.attachLabels(tree, classOf[GoldClass])

      tree
    }.collect().toList

    TrainingData(trainingTrees)
  }
}

