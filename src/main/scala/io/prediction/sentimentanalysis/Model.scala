package io.prediction.sentimentanalysis

import edu.stanford.nlp.pipeline.StanfordCoreNLP
import io.prediction.controller.PersistentModel
import org.apache.spark.SparkContext

case class Model(tokenizer: StanfordCoreNLP, pipeline: StanfordCoreNLP)
  extends PersistentModel[AlgorithmParams] with Serializable {

  override def save(id: String, params: AlgorithmParams, sc: SparkContext): Boolean = {
    false
  }

}