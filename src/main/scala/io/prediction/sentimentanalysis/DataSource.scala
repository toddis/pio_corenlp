package io.prediction.sentimentanalysis

import io.prediction.controller.{EmptyActualResult, EmptyEvaluationInfo, PDataSource}
import org.apache.spark.SparkContext

class DataSource(val dsp: DataSourceParams) extends PDataSource[
  TrainingData,
  EmptyEvaluationInfo,
  Query,
  EmptyActualResult] {

  override def readTraining(sc: SparkContext): TrainingData = {
    TrainingData()
  }
}

