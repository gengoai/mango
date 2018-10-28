package com.gengoai.graph.algorithms;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public interface GraphVisitor<V> {

   Iterator<V> iterator(V startingPoint);

}//END OF Visitor
