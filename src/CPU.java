import lib.queues.LinkedQueue;
public class CPU {


	private LinkedQueue<Task> taskLinkedQueue;
	private boolean isAvailable;
	private boolean isRunning;


	public CPU() {
		this.taskLinkedQueue = new LinkedQueue<Task>();
		this.isRunning = false;
		this.isAvailable = true;
	}


	public boolean isAvailable() {
		return this.isRunning && this.isAvailable;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void start() {

		if (this.isRunning) {
			System.out.println("CPU ALREADY STARTED");
			return;
		}

		System.out.println("CPU STARTING...");

		isAvailable = true;
		isRunning = true;
		System.out.println("CPU STARTED");
	}

	public void stop() {

		if (!this.isRunning) {
			System.out.println("CPU ALREADY STOPPED");
		}

		System.out.println("CPU STOPPING");

		isRunning = false;
		System.out.println("CPU STOPPED");
	}

	public void excuteTask(Task task) {

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
		}

		// CPU gets busy
		isAvailable = false;

		System.out.println("CPU EXECUTING TASK " + task.getName());
		task.setStatus(Status.RUNNING);

		task.setStatus(Status.COMPLETED);
		System.out.println("CPU COMPLETED TASK " + task.getName());

		// CPU is not busy anymore
		isAvailable = true;
	}

	/*public void firstComeFirstServed() {

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
		}

		// Wait... I need a
	}*/
}

// Do we need now scheduling algorithms?
// TODO: First Come, First Served (FCFS) scheduling.