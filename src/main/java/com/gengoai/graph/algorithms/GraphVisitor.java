package com.gengoai.graph.algorithms;

import com.gengoai.graph.Graph;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public interface GraphVisitor {

   <V> Iterator<V> iterator(Graph<V> graph, V startingPoint);

}//END OF Visitor
