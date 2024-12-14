import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedList;

public class Kernel {

	LinkedList<Task> taskLinkedList;
	boolean isRunning;

	public Kernel() {

		this.taskLinkedList = new LinkedList<Task>();
		this.isRunning = true;
	}


	public boolean isRunning() {
		return this.isRunning;
	}


	public void start() {

		if (this.isRunning) {
			System.out.println("KERNEL ALREADY STARTED");
			return;
		}

		System.out.println("KERNEL STARTING...");

		isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	public void stop() {

		if (!this.isRunning) {
			System.out.println("KERNEL ALREADY STOPPED");
		}

		System.out.println("KERNEL STOPPING...");
		// TODO: CPU, MEMORY, DEVICES, SERVER
		isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	public void addTask(Task task) throws NotElementComparableException {

		if (!this.isRunning) {
			System.out.println("KERNEL DID NOT STARTED YET");
			return;
		}

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return;
		}


		if (this.isRunning) {
			this.taskLinkedList.add(task);
			System.out.println("New task " + task.getName() + " was added.");
		}
	}

}
