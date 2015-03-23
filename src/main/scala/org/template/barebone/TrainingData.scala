package org.template.barebone

import edu.stanford.nlp.trees.Tree

case class TrainingData(trainingTrees: List[Tree]) extends Serializable
