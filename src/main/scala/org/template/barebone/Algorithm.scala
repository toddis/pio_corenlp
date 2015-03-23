package org.template.barebone

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.sentiment.RNNOptions
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.sentiment.SentimentModel
import edu.stanford.nlp.sentiment.SentimentTraining
import grizzled.slf4j.Logger
import io.prediction.controller.P2LAlgorithm
import org.apache.spark.SparkContext

import scala.collection.JavaConversions._

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  @transient lazy private val logger = Logger[this.type]
  val TempModelPath = "/tmp/sentiment.ser.gz"

  def train(sc: SparkContext, data: PreparedData): Model = {
    // train and save corenlp sentiment model file
    val rnnOptions = new RNNOptions()
    rnnOptions.setOption(Array("-epochs", ap.epoch.toString), 0)
    val model = new SentimentModel(rnnOptions, data.trainingTrees)
    SentimentTraining.train(model, null, data.trainingTrees, null)
    model.saveSerialized(TempModelPath)

    // create pio model
    Model.model(TempModelPath, ap.parseModelPath)
  }

  def predict(model: Model, query: Query): PredictedResult = {
    val annotation = model.tokenizer.process(query.sentence)
    model.pipeline.annotate(annotation)

    val sentence = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])(0)
    val sentiment = sentence.get(classOf[SentimentCoreAnnotations.ClassName])

    PredictedResult(sentiment)
  }
}
