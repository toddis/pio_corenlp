package org.template.barebone

import edu.stanford.nlp.sentiment.SentimentUtils
import io.prediction.controller.{EmptyActualResult, EmptyEvaluationInfo, PDataSource}
import org.apache.spark.SparkContext

import collection.JavaConversions._

class DataSource(val dsp: DataSourceParams) extends PDataSource[
    TrainingData,
    EmptyEvaluationInfo,
    Query,
    EmptyActualResult] {

  override def readTraining(sc: SparkContext): TrainingData = {
    val trainingTrees = SentimentUtils.readTreesWithGoldLabels(dsp.trainingPath)

    TrainingData(trainingTrees.toList)
  }
}

