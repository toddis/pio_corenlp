package org.template.barebone

import java.util.Properties

import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentModel
import io.prediction.controller.IPersistentModel
import io.prediction.controller.IPersistentModelLoader
import org.apache.spark.SparkContext

case class Model(
  tokenizer: StanfordCoreNLP,
  pipeline: StanfordCoreNLP,
  sentimentModel: SentimentModel) extends IPersistentModel[AlgorithmParams] with Serializable {

  override def save(id: String, params: AlgorithmParams, sc: SparkContext): Boolean = {
    sentimentModel.saveSerialized(s"${Model.ModelPath}/sentiment-$id.ser.gz")

    true
  }
}

object Model extends IPersistentModelLoader[AlgorithmParams, Model] {
  val ModelPath = "./data"

  override def apply(id: String, params: AlgorithmParams, sc: Option[SparkContext]): Model = {
    model(s"$ModelPath/sentiment-$id.ser.gz", params.parseModelPath)
  }

  def model(sentimentModelPath: String, parseModelPath: String): Model = {
    val pipelineProps = new Properties()
    val tokenizerProps = new Properties()
    pipelineProps.setProperty("sentiment.model", sentimentModelPath)
    pipelineProps.setProperty("parse.model", parseModelPath)
    pipelineProps.setProperty("annotators", "parse, sentiment")
    pipelineProps.setProperty("enforceRequirements", "false")
    pipelineProps.setProperty("ssplit.eolonly", "true")
    tokenizerProps.setProperty("annotators", "tokenize, ssplit")

    val tokenizer = new StanfordCoreNLP(tokenizerProps)
    val pipeline = new StanfordCoreNLP(pipelineProps)

    Model(tokenizer, pipeline, SentimentModel.loadSerialized(sentimentModelPath))
  }
}
