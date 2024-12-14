import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedOrderedList;
import lib.queues.LinkedQueue;
import lib.trees.PriorityQueue;
import lib.lists.LinkedList;

public class CPU {

	private final static long TIME_SLICE = 1000;

	// CPU has a taskLinkedQueue of tasks to be executed
	private PriorityQueue<Task> taskPriorityQueue;
	private LinkedQueue<Task> taskLinkedQueue;
	private boolean isAvailable;
	private boolean isRunning;


	public CPU() {
		this.taskLinkedQueue = new LinkedQueue<Task>();
		this.taskPriorityQueue = new PriorityQueue<Task>();
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

		System.out.println("SCHEDULING TASK: " + task.getName() + " with priority: " + task.getPriority());
		taskPriorityQueue.addElement(task, task.getPriority());

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

	public synchronized void schedulePreemptive() {

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
			return;
		}

		if (this.taskPriorityQueue.isEmpty()) {
			System.out.println("taskPriorityQueue is empty");
			return;
		}

		while (!this.taskPriorityQueue.isEmpty()) {
			try {
				Task nextTask = this.taskPriorityQueue.removeElement();
				nextTask.setStatus(Status.RUNNING);
				System.out.println("TASK " + nextTask.getName() + " with Priority: " + nextTask.getPriority() + " is " + nextTask.getStatus());

				long duration = nextTask.getDuration();

				if (duration < TIME_SLICE) {
					Thread.sleep(duration);
					nextTask.setStatus(Status.COMPLETED);
					System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus());
				} else {
					Thread.sleep(TIME_SLICE);
					// TODO: Pause the Task
					nextTask.setDuration(duration - TIME_SLICE);
					nextTask.setStatus(Status.PAUSED);
					System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus() + " with a duration: " + nextTask.getDuration() +"ms");
					this.taskPriorityQueue.addElement(nextTask, nextTask.getPriority());
				}

			} catch (EmptyCollectionException | InterruptedException e) {
				System.err.println(e.getMessage());
				break;
			}

		}
	}

	public synchronized void scheduleSFJ() {
		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
			return;
		}

		if (taskPriorityQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		LinkedOrderedList<Task> taskLinkedOrderedList = new LinkedOrderedList<Task>();

		while(!this.taskLinkedQueue.isEmpty()) {
			try {
				Task nextTask = this.taskLinkedQueue.dequeue();
				nextTask.setStatus(Status.READY);

				System.out.println("TASK " + nextTask.getName() +
					" is " + nextTask.getStatus() +
					" with a duration: " + nextTask.getDuration() + "ms");

				taskLinkedOrderedList.add(nextTask);
			} catch (EmptyCollectionException | NotElementComparableException e) {
				System.err.println(e.getMessage());
			}
		}

		for ( Task nextTask : taskLinkedOrderedList) {
			System.out.println("TASK " + nextTask.getName() +
				" is " + nextTask.getStatus());
			executeTask(nextTask);
		}


	}
}

// Do we need now scheduling algorithms?
// TODO: TIME_SLICE static or dynamic? (static = is fixed) (dynamic = is flexible) Thinking...
// TODO: Shortest-Job-First algorithm for scheduling.
