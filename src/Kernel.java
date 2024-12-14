import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedList;

public class Kernel {


	private CPU cpu;
	LinkedList<Task> taskLinkedList;
	boolean isRunning;
	private final int MAX_TASK_CAPACITY = 100;

	public Kernel() {
		// TODO: CPU, MEMORY, DEVICES, SERVER
		this.taskLinkedList = new LinkedList<Task>();
		this.cpu = new CPU();
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

		cpu.start();

		isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	public void stop() {

		if (!this.isRunning) {
			System.out.println("KERNEL ALREADY STOPPED");
		}

		System.out.println("KERNEL STOPPING...");
		// TODO: MEMORY, DEVICES, SERVER
		cpu.stop();
		isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	public void addTask(Task task) throws NotElementComparableException {

		if (this.isRunning == false) {
			System.out.println("KERNEL DID NOT STARTED YET");
			return;
		}

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return;
		}

		if (taskLinkedList.size() >= MAX_TASK_CAPACITY) {
			System.out.println("MAX CAPACITY OF TASKS IN KERNEL REACHED");
			return;
		}

		if (this.isRunning == true) {
			this.taskLinkedList.add(task);
			System.out.println("New task " + task.getName() + " was added.");
		}
	}

	private void validateRessources(Task task) {

		if (!cpu.isAvailable()) {
			System.out.println("CPU IS NOT AVAILABLE");
		}

		// TODO: validate Memory resource for a Task


		// TODO: validate Devices resource for a task

	}

	private void executeOneTask(Task task) {

		task.setStatus(Status.RUNNING);

		// TODO: Execute task? maybe cpu.excuteTask(task)

	}

}

// TODO: validate resources for a Task.
// TODO: when adding a Task, validate if the system is running, then validate the Task itself and if it fits the Kernel resources.
