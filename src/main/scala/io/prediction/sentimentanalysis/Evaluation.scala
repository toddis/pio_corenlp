package org.template.classification

import io.prediction.controller.AverageMetric
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EngineParams
import io.prediction.controller.EngineParamsGenerator
import io.prediction.controller.Evaluation
import io.prediction.controller.OptionAverageMetric

import io.prediction.sentimentanalysis.PredictedResult
import io.prediction.sentimentanalysis.ActualResult
import io.prediction.sentimentanalysis.Query
import io.prediction.sentimentanalysis.EngineFactory

case class Precision(label: Double)
  extends OptionAverageMetric[EmptyEvaluationInfo, Query, PredictedResult, ActualResult] {
  def calculate(query: Query, predicted: PredictedResult, actual: ActualResult)
  : Option[Double] = {
    if (predicted.sentiment == "good") {  // TODO: FIX
      if (predicted.sentiment == "good") {  // TODO: FIX
        Some(1.0)  // True positive
      } else {
        Some(0.0)  // False positive
      }
    } else {
      None  // Unrelated case for calcuating precision
    }
  }
}

object PrecisionEvaluation extends Evaluation {
  engineMetric = (EngineFactory(), new Precision(label = 1.0))
}


