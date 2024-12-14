import lib.exceptions.EmptyCollectionException;
import lib.queues.LinkedQueue;
public class CPU {

	// CPU has a taskLinkedQueue of tasks to be executed
	private LinkedQueue<Task> taskLinkedQueue;
	private boolean isAvailable;
	private boolean isRunning;


	public CPU() {
		this.taskLinkedQueue = new LinkedQueue<Task>();
		this.isRunning = false;
		this.isAvailable = true;
	}


	public boolean isReady() {
		return !isRunning && isAvailable;
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
			return;
		}

		System.out.println("CPU STOPPING");

		isAvailable = false;
		isRunning = false;
		System.out.println("CPU STOPPED");
	}

	public synchronized void executeTask(Task task) {

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
			return;		}

		// CPU gets busy
		isAvailable = false;

		System.out.println("CPU EXECUTING TASK " + task.getName());
		task.setStatus(Status.RUNNING);

		try {
			// Sleep the task for one second...
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
			return;
		}

		task.setStatus(Status.COMPLETED);
		System.out.println("CPU COMPLETED TASK " + task.getName());

		// CPU is not busy anymore
		isAvailable = true;
	}

	public synchronized void scheduleTask(Task task) {

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
		}

		System.out.println("SCHEDULING TASK: " + task.getName());
		taskLinkedQueue.enqueue(task);

	}

	public synchronized void scheduleFCFS() {

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
			return;
		}

		if (taskLinkedQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		// As long as the taskLinkedQueue is not empty...
		while (!taskLinkedQueue.isEmpty()) {

			try {
				Task nextTask = taskLinkedQueue.dequeue();
				System.out.println("TASK " + nextTask.getName() + " ADDED TO QUEUE");
				executeTask(nextTask);

			} catch (EmptyCollectionException e) {
				System.err.println(e.getMessage());
				break;
			}
		}
	}
}

// Do we need now scheduling algorithms?
// TODO: Preemptive Scheduling.