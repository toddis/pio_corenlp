package io.prediction.sentimentanalysis

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
import io.prediction.sentimentanalysis.DataSourceParams
import io.prediction.sentimentanalysis.AlgorithmParams

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

object EngineParamsList extends EngineParamsGenerator {
  // Define list of EngineParams used in Evaluation

  // First, we define the base engine params. It specifies the appId from which
  // the data is read, and a evalK parameter is used to define the
  // cross-validation.
  private[this] val baseEP = EngineParams(
    dataSourceParams = DataSourceParams())

  // Second, we specify the engine params list by explicitly listing all
  // algorithm parameters. In this case, we evaluate 3 engine params, each with
  // a different algorithm params value.
  engineParamsList = Seq(
    baseEP.copy(algorithmParamsList = Seq(("algo", AlgorithmParams("./data/englishPCFG.ser.gz", "./data/sentiment.ser.gz"))))
   )
}


