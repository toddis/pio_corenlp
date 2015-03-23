package org.template.barebone

import edu.stanford.nlp.trees.Tree

case class PreparedData(trainingTrees: List[Tree]) extends Serializable
