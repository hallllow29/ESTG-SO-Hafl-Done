package lib.trees;

import lib.PriorityQueueNode;


public class PriorityQueue<T> extends ArrayHeap<PriorityQueueNode<T>> {

	public PriorityQueue() {
		super();
	}

	public void addElement(T element, int priority) {
		PriorityQueueNode<T> priorityQueueNode = new PriorityQueueNode<T>(element, priority);

		super.addElement(priorityQueueNode);
	}

}
