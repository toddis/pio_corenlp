package org.template.barebone

import edu.stanford.nlp.pipeline.StanfordCoreNLP

case class Model(tokenizer: StanfordCoreNLP, pipeline: StanfordCoreNLP) extends Serializable
