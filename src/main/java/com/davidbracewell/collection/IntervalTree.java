package com.davidbracewell.collection;

import com.davidbracewell.conversion.Cast;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public class IntervalTree<T extends Span> implements Serializable, Collection<T> {

   protected static final Node NULL = new Node(null, null, null, new Span(0, Integer.MAX_VALUE));
   private static final long serialVersionUID = 1L;
   protected Node root = Cast.as(NULL);
   protected int size = 0;

   private boolean isRed(Node node) {
      return !isNull(node) && node.isRed;
   }

   @Override
   public boolean add(T T) {
      if (T == null) {
         return false;
      }

      Node z = new Node(T);

      //Empty tree, add this and return
      if (isNull(root)) {
         size++;
         root = z;
         root.setBlack();
         return true;
      }

      Node<T> iNode = root;
      Node<T> parent = Cast.as(NULL);
      while (!isNull(iNode)) {
         parent = iNode;
         if (T.start() == iNode.span.start() && T.end() == iNode.span.end()) {
            //Same span, so we will add it to this node
            if (iNode.items.add(T)) {
               //Increment the size if the T was added to the node's set.
               size++;
            }
            return true;
         } else if (T.start() <= iNode.span.start()) {
            //Need to go to the left because the T's doesn't have the same span as the iNode and its
            // start is <= the iNode's
            iNode = iNode.left;
         } else {
            //Need to go to the right because the T's doesn't have the same span as the iNode and its
            // start is >= the iNode's
            iNode = iNode.right;
         }
      }

      //At this point we did not found a matching span and traveled to the bottom of the tree
      // We will set our the parent to last non-null iNode and increment the size.
      z.parent = parent;
      size++;
      if (isNull(parent)) {
         //Safety check that we are not at the root
         root = z;
      } else if (T.start() <= parent.span.start()) {
         //the new node will go to the left of its parent
         parent.left = z;
      } else {
         //the new node will go to the right of its parent
         parent.right = z;
      }

      //Need to update and re-balance
      update(z);
      balance(z);
      return true;
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      if (c == null) {
         return false;
      }
      return c.stream()
              .allMatch(this::add);
   }

   private void balance(Node z) {
      Node y;

      while (!isNull(z) && z != root && !isNull(z.getParent()) && isRed(z.getParent())) {
         if (z.getParent() == z.getGrandparent().left) {
            y = z.getGrandparent().right;
            if (isRed(y)) {
               z.getParent().setBlack();
               y.setBlack();
               z.getGrandparent().setRed();
               z = z.getGrandparent();
            } else {
               if (z == z.getParent().right) {
                  z = z.getParent();
                  rotateLeft(z);
               }
               z.getParent().setBlack();
               z.getGrandparent().setBlack();
               rotateRight(z.getGrandparent());
            }
         } else {
            y = z.getGrandparent().left;
            if (isRed(y)) {
               z.getParent().setBlack();
               y.setBlack();
               z.getGrandparent().setRed();
               z = z.getGrandparent();
            } else {
               if (z == z.getParent().left) {
                  z = z.getParent();
                  rotateRight(z);
               }
               z.getParent().setBlack();
               z.getGrandparent().setRed();
               rotateLeft(z.getGrandparent());
            }
         }
      }
      root.setBlack();
   }

   @Override
   public void clear() {
      this.root = Cast.as(NULL);
   }

   @Override
   public boolean contains(Object o) {
      if (o != null && o instanceof Span) {
         Node n = findNode(root, Cast.as(o));
         return !n.isNull() && n.items.contains(o);
      }
      return false;
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      if (c == null) {
         return false;
      }
      for (Object o : c) {
         if (!contains(o)) {
            return false;
         }
      }
      return true;
   }

   private Node<T> findNode(Node<T> n, Span span) {
      while (!n.isNull()) {
         if (n.span.start() == span.start() && n.span.end() == span.end()) {
            return n;
         }
         if (span.start() <= n.span.start()) {
            n = n.left;
         } else {
            n = n.right;
         }
      }
      return Cast.as(NULL);
   }

   @Override
   public boolean isEmpty() {
      return root == null || root.isNull();
   }

   private boolean isNull(Node node) {
      return node == null || node.isNull();
   }

   @Override
   public Iterator<T> iterator() {
      return new NodeIterator(root);
   }

   /**
    * Gets all Ts that overlap the given span
    *
    * @param span the span
    * @return the list of overlapping T
    */
   public List<T> overlapping(Span span) {
      if (span == null || root.isNull()) {
         return Collections.emptyList();
      }
      return overlapping(root, span, new ArrayList<>());
   }

   private List<T> overlapping(Node node, Span span, List<T> results) {
      if (isNull(node)) {
         return results;
      }
      if (node.span.overlaps(span)) {
         results.addAll(node.items);
      }
      if (!isNull(node.left) && node.left.max >= span.start()) {
         overlapping(node.left, span, results);
      }
      if (!isNull(node.right) && node.right.min <= span.end()) {
         overlapping(node.right, span, results);
      }
      return results;
   }

   @Override
   public boolean remove(Object o) {
      if (o instanceof Span) {
         T T = Cast.as(o);
         Node n = findNode(root, T);

         //Not in the tree
         if (isNull(n) || !n.items.remove(T)) {
            return false;
         }
         size--;

         if (n.items.size() > 0) {
            return true;
         }

         Node x = Cast.as(NULL);
         Node y = Cast.as(NULL);

         //Leaf Node
         if (isNull(n.left) && isNull(n.left)) {
            if (y.getParent().left == y) {
               y.getParent().left = Cast.as(NULL);
            } else {
               y.getParent().right = Cast.as(NULL);
            }
            return true;
         }

         if (isNull(n.left) || isNull(n.left)) {
            y = n;
         }

         if (!isNull(n.left)) {
            x = y.left;
         } else {
            x = y.right;
         }

         x.parent = y.parent;

         if (isNull(y.parent)) {
            root = x;
         } else if (!isNull(y.getParent().left) && y.getParent().left == y) {
            y.getParent().left = x;
         } else if (!isNull(y.getParent().right) && y.getParent().right == y) {
            y.getParent().right = x;
         }

         update(x);
         update(y);

         return true;
      }
      return false;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      if (c == null) {
         return false;
      }
      boolean removed = true;
      for (Object o : c) {
         if (!this.remove(o)) {
            removed = false;
         }
      }
      return removed;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      if (c != null) {
         Collection<T> toRemove = com.davidbracewell.collection.list.Lists.difference(toList(), Cast.cast(c));
         toRemove.forEach(this::remove);
         return containsAll(c);
      }
      return false;
   }

   private void rotateLeft(Node x) {
      Node y = x.right;
      x.right = y.left;

      if (!isNull(y)) {
         y.left.parent = x;
      }
      y.parent = x.parent;

      if (x == root) {
         root = y;
      } else if (x.getParent().left == x) {
         x.parent.left = y;
      } else {
         x.parent.right = y;
      }

      y.left = x;
      x.parent = y;
      update(x);
   }

   private void rotateRight(Node x) {
      Node y = x.left;
      x.left = y.right;

      if (!isNull(y)) {
         y.right.parent = x;
      }

      y.parent = x.parent;

      if (x == root) {
         root = y;
      } else if (x.getParent().right == x) {
         x.parent.right = y;
      } else {
         x.parent.left = y;
      }

      y.right = x;
      x.parent = y;
      update(x);
   }

   @Override
   public int size() {
      return size;
   }

   @Override
   public Object[] toArray() {
      return Streams.asStream(iterator()).toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return toList().toArray(a);
   }

   private List<T> toList() {
      return Lists.newArrayList(Collect.asIterable(this.iterator()));
   }

   private void update(Node node) {
      while (!node.isNull()) {
         node.max = Math.max(Math.max(node.left.max, node.right.max), node.span.end());
         node.min = Math.min(Math.min(node.left.min, node.right.min), node.span.start());
         node = node.parent;
      }
   }

   protected static class Node<T extends Span> implements Serializable {
      private static final long serialVersionUID = 1L;
      /**
       * The Ts.
       */
      public final Set<T> items = new LinkedHashSet<>();
      /**
       * The Is red.
       */
      public boolean isRed;
      /**
       * The Span.
       */
      public Span span;
      /**
       * The Left.
       */
      public Node<T> left;
      /**
       * The Right.
       */
      public Node<T> right;
      /**
       * The Parent.
       */
      public Node<T> parent;
      /**
       * The Min.
       */
      public int min;
      /**
       * The Max.
       */
      public int max;

      /**
       * Instantiates a new Node.
       *
       * @param left   the left
       * @param right  the right
       * @param parent the parent
       * @param span   the span
       */
      public Node(Node<T> left, Node<T> right, Node<T> parent, Span span) {
         this.left = left;
         this.max = span.start();
         this.min = span.end();
         this.parent = parent;
         this.right = right;
         this.span = span;
         this.isRed = true;
      }

      /**
       * Instantiates a new Node.
       *
       * @param T the T
       */
      public Node(T T) {
         this.left = NULL;
         this.right = NULL;
         this.parent = NULL;
         this.min = T.start();
         this.max = T.end();
         this.span = T;
         this.items.add(T);
         this.isRed = true;
      }

      /**
       * Gets grandparent.
       *
       * @return the grandparent
       */
      public Node<T> getGrandparent() {
         return parent == null ? NULL : parent.parent == null ? NULL : parent.parent;
      }

      /**
       * Gets parent.
       *
       * @return the parent
       */
      public Node<T> getParent() {
         return parent == null ? NULL : parent;
      }

      /**
       * Is null boolean.
       *
       * @return the boolean
       */
      public boolean isNull() {
         return this == NULL;
      }

      /**
       * Sets black.
       */
      public void setBlack() {
         this.isRed = false;
      }

      /**
       * Sets red.
       */
      public void setRed() {
         this.isRed = true;
      }

      @Override
      public String toString() {
         return "(" + span.start() + ", " + span.end() + ")";
      }

   }

   /**
    * The type Node iterator.
    */
   static class NodeIterator<T extends Span> implements Iterator<T> {
      private Deque<Node> stack = new LinkedList<>();
      private Span targetSpan;
      private boolean goLeft;
      private LinkedList<T> Ts = new LinkedList<>();


      /**
       * Instantiates a new Node iterator.
       *
       * @param node the node
       */
      public NodeIterator(Node node) {
         this(node, -1, Integer.MAX_VALUE, true);
      }

      /**
       * Instantiates a new Node iterator.
       *
       * @param node   the node
       * @param min    the min
       * @param max    the max
       * @param goLeft the go left
       */
      public NodeIterator(Node node, int min, int max, boolean goLeft) {
         this.goLeft = goLeft;
         this.targetSpan = new Span(min, max);
         while (node != null && !node.isNull()) {
            stack.push(node);
            node = goLeft ? node.left : node.right;
         }
      }

      private boolean advance() {
         while (Ts.isEmpty()) {
            if (stack.isEmpty()) {
               return false;
            }
            Node node = stack.pop();
            if (goLeft) {
               if (node.right != null && !node.right.isNull()) {
                  Node nr = node.right;
                  while (!nr.isNull()) {
                     stack.push(nr);
                     nr = nr.left;
                  }
               }
            } else {
               if (node.left != null && !node.left.isNull()) {
                  Node nr = node.left;
                  while (!nr.isNull()) {
                     stack.push(nr);
                     nr = nr.right;
                  }
               }
            }


            if (node.span.overlaps(targetSpan)) {
               Ts.addAll(node.items);
            }
         }

         return Ts.size() > 0;
      }

      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public T next() {
         if (!advance()) {
            throw new NoSuchElementException();
         }
         return Ts.removeFirst();
      }
   }

   static {
      NULL.left = Cast.as(NULL);
      NULL.right = Cast.as(NULL);
      NULL.parent = Cast.as(NULL);
   }


   /**
    * Floor T.
    *
    * @param T the T
    * @return the T
    */
   public T floor(T T) {
      if (T == null) {
         return null;
      }
      NodeIterator<T> iterator = new NodeIterator<>(root, -1, T.start(), false);
      if (iterator.hasNext()) {
         return iterator.next();
      }
      return null;
   }


   /**
    * Ceiling T.
    *
    * @param T the T
    * @return the T
    */
   public T ceiling(T T) {
      if (T == null) {
         return null;
      }
      NodeIterator<T> iterator = new NodeIterator<>(root, T.end(), Integer.MAX_VALUE, true);
      if (iterator.hasNext()) {
         return iterator.next();
      }
      return null;
   }


}//END OF IntervalTree
