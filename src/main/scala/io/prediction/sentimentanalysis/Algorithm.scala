package io.prediction.sentimentanalysis

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import io.prediction.controller.P2LAlgorithm
import org.apache.spark.SparkContext

import scala.collection.JavaConversions._

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  def train(sc: SparkContext, data: PreparedData): Model = {
    val pipelineProps = new Properties()
    val tokenizerProps = new Properties()
    pipelineProps.setProperty("sentiment.model", ap.sentimentModelPath)
    pipelineProps.setProperty("parse.model", ap.parseModelPath)
    pipelineProps.setProperty("annotators", "parse, sentiment")
    pipelineProps.setProperty("enforceRequirements", "false")
    pipelineProps.setProperty("ssplit.eolonly", "true")
    tokenizerProps.setProperty("annotators", "tokenize, ssplit")

    val tokenizer = new StanfordCoreNLP(tokenizerProps)
    val pipeline = new StanfordCoreNLP(pipelineProps)

    Model(tokenizer, pipeline)
  }

  def predict(model: Model, query: Query): PredictedResult = {
    val annotation = model.tokenizer.process(query.sentence)
    model.pipeline.annotate(annotation)

    val sentence = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])(0)
    val sentiment = sentence.get(classOf[SentimentCoreAnnotations.ClassName])

    PredictedResult(sentiment)
  }
}
