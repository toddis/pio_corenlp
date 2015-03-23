package org.template.barebone

import io.prediction.controller.Params

case class AlgorithmParams(parseModelPath: String, epoch: Int) extends Params
