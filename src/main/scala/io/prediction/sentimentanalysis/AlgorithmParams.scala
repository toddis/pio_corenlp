package io.prediction.sentimentanalysis

import io.prediction.controller.Params

case class AlgorithmParams(parseModelPath: String, sentimentModelPath: String) extends Params
