import lib.lists.LinkedList;

public class Kernel {


	boolean isRunning;

	public Kernel() {

	}

	public void start() {
		System.out.println("KERNEL STARTING...");
		isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	public void stop() {
		System.out.println("KERNEL STOPPING...");
		isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	public void addTask() {
		// Linked List for tasks?
	}

}
