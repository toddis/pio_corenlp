package io.prediction.sentimentanalysis

import io.prediction.controller.{Engine, EngineFactory}

object EngineFactory extends EngineFactory {
  def apply() = {
    new Engine(
      classOf[DataSource],
      classOf[Preparator],
      Map("algo" -> classOf[Algorithm]),
      classOf[Serving])
  }
}
