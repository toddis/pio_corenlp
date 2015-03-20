package org.template.barebone

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import grizzled.slf4j.Logger
import io.prediction.controller.P2LAlgorithm
import org.apache.spark.SparkContext

import scala.collection.JavaConversions._

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  @transient lazy private val logger = Logger[this.type]

  def train(sc: SparkContext, data: PreparedData): Model = {
    /*
    val rnnOptions = new RNNOptions()
    val trainingTrees = SentimentUtils.readTreesWithGoldLabels("./data/train.txt")
    val model = new SentimentModel(rnnOptions, trainingTrees)

    SentimentTraining.train(model, null, trainingTrees, null)
    model.saveSerialized(modelPath)
*/
    val pipelineProps = new Properties()
    val tokenizerProps = new Properties()
    pipelineProps.setProperty("sentiment.model", "./data/sentiment.ser.gz")
    pipelineProps.setProperty("parse.model", "./data/englishPCFG.ser.gz")
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
