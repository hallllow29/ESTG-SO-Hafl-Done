package Core;
import lib.exceptions.EmptyCollectionException;
import lib.trees.PriorityQueue;


public class Kernel {

	private CPU cpu;
	private Mem memory;
	private Devices devices;
	private Server server;
	PriorityQueue<Task> taskPriorityQueue;
	boolean isRunning;

	public Kernel() {
		this.taskPriorityQueue = new PriorityQueue<Task>();
		this.cpu = new CPU();
		this.memory = new Mem();
		this.devices = new Devices();
		this.server = new Server();
		this.isRunning = false;
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
		memory.start();
		devices.start();
		server.start();

		this.isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	public void stop() {

		if (!this.isRunning) {
			System.out.println("KERNEL ALREADY STOPPED");
		}

		System.out.println("KERNEL STOPPING...");

		cpu.stop();
		memory.stop();
		devices.stop();
		server.stop();

		this.isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	public synchronized void addTask(Task task) {

		if (!this.isRunning) {
			System.out.println("KERNEL DID NOT STARTED YET");
			return;
		}

		if (taskValid(task)) {
			taskPriorityQueue.addElement(task, task.getPriority().ordinal());
			System.out.println("NEW TASK " + task.getName() + " WAS ADDED TO PRIORITY QUEUE.");
		}

	}

	public synchronized void processNextTask() {

		// Either I leave here or in addTask(Core.Task)...
		if (!this.isRunning) {
			System.out.println("KERNEL DID NOT STARTED YET");
			return;
		}

		if (this.taskPriorityQueue.isEmpty() || this.taskPriorityQueue == null) {
			System.out.println("TASK PRIORITY QUEUE IS EMPTY");
			return;
		}

		try {
			Task nextTask = this.taskPriorityQueue.removeElement();
			if (validateRessources(nextTask)) {

				cpu.executeOneTask(nextTask);

				releaseResources(nextTask);

			} else {
				taskPriorityQueue.addElement(nextTask, nextTask.getPriority().ordinal());
			}
		} catch (EmptyCollectionException e) {
			System.err.println("TASK PRIORITY QUEUE IS EMPTY");
		}
	}

	public boolean validateRessources(Task task) {

		if (!cpu.isAvailable()) {
			System.out.println("Core.CPU IS NOT AVAILABLE");
			return false;
		}

		if (!memory.hasAvailableMemory(task.getMemorySize())) {
			System.out.println("MEMORY IS NOT AVAILABLE");
			return false;
		}

		if (!memory.allocateFF(task)) {
			return false;
		}

		return true;
	}

	private boolean taskValid(Task task) {

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return false;
		}

		if (task.getDuration() <= 0) {
			System.out.println("TASK DURATION CANNOT BE ZERO");
			return false;
		}

		if (task.getName() == null) {
			System.out.println("TASK NAME CANNOT BE NULL");
			return false;
		}

		return true;
	}

	private void releaseResources(Task task) {
		try {
			this.memory.freeMemory(task.getName());
		} catch (Exception e) {
			System.err.println("Error releasing memory: " + e.getMessage());
		}
	}

}

/*

	public synchronized void scheduleTask(Core.Task task) {

		if (!this.isRunning) {
			System.out.println("Core.CPU IS NOT RUNNING");
		}

		System.out.println("SCHEDULING TASK: " + task.getName() + " with priority: " + task.getPriority());
		taskPriorityQueue.addElement(task, task.getPriority());

	}

	public synchronized void scheduleFCFS() {

		if (!this.isRunning) {
			System.out.println("Core.CPU IS NOT RUNNING");
			return;
		}

		if (taskLinkedQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		// As long as the taskLinkedQueue is not empty...
		while (!taskLinkedQueue.isEmpty()) {

			try {
				Core.Task nextTask = taskLinkedQueue.dequeue();
				System.out.println("TASK " + nextTask.getName() + " ADDED TO QUEUE");
				cpu.executeTaskDuration(nextTask, nextTask.getDuration());

			} catch (EmptyCollectionException e) {
				System.err.println(e.getMessage());
				break;
			}
		}
	}

	public synchronized void schedulePreemptive() {

		if (!this.isRunning) {
			System.out.println("Core.CPU IS NOT RUNNING");
			return;
		}

		if (this.taskPriorityQueue.isEmpty()) {
			System.out.println("taskPriorityQueue is empty");
			return;
		}

		while (!this.taskPriorityQueue.isEmpty()) {
			try {
				Core.Task nextTask = this.taskPriorityQueue.removeElement();
				nextTask.setStatus(Enums.Status.RUNNING);
				System.out.println("TASK " + nextTask.getName() + " with Priority: " + nextTask.getPriority() + " is " + nextTask.getStatus());

				long duration = nextTask.getDuration();

				if (duration < cpu.getTIMESLICE()) {
					Thread.sleep(duration);
					nextTask.setStatus(Enums.Status.COMPLETED);
					System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus());
				} else {
					Thread.sleep(cpu.getTIMESLICE());
					// TODO: Pause the Core.Task
					nextTask.setDuration(duration - cpu.getTIMESLICE());
					nextTask.setStatus(Enums.Status.PAUSED);
					System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus() + " with a duration: " + nextTask.getDuration() + "ms");
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
			System.out.println("Core.CPU IS NOT RUNNING");
			return;
		}

		if (taskLinkedQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		LinkedOrderedList<Core.Task> taskLinkedOrderedList = new LinkedOrderedList<Core.Task>();
		Core.Task nextTask = null;

		while (!this.taskLinkedQueue.isEmpty()) {
			try {
				nextTask = this.taskLinkedQueue.dequeue();
				nextTask.setStatus(Enums.Status.READY);

				System.out.println("TASK " + nextTask.getName() +
					" is " + nextTask.getStatus() +
					" with a duration: " + nextTask.getDuration() + "ms");

				taskLinkedOrderedList.add(nextTask);
			} catch (EmptyCollectionException | NotElementComparableException e) {
				System.err.println(e.getMessage());
			}
		}

		Iterator<Core.Task> taskLinkedOrderedListIterator = taskLinkedOrderedList.iterator();

		while (taskLinkedOrderedListIterator.hasNext()) {
			nextTask = taskLinkedOrderedListIterator.next();
			taskLinkedOrderedListIterator.remove();
			System.out.println("TASK " + nextTask.getName() +
				" is " + nextTask.getStatus());
			cpu.executeTaskDuration(nextTask, nextTask.getDuration());
		}
	}

	public synchronized void scheduleRR() {

		if (!this.isRunning) {
			System.out.println("Core.CPU IS NOT RUNNING");
			return;
		}

		if (taskLinkedQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		Core.Task nextTask = null;

		while (!taskLinkedQueue.isEmpty()) {
			try {
				nextTask = taskLinkedQueue.dequeue();
				nextTask.setStatus(Enums.Status.RUNNING);
				System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus());

				long duration = Math.min(cpu.getTIMESLICE(), nextTask.getDuration());

				cpu.executeTaskDuration(nextTask, duration);

				nextTask.setDuration(nextTask.getDuration() - duration);

				if (nextTask.getDuration() == 0) {
					nextTask.setStatus(Enums.Status.COMPLETED);

				} else {
					nextTask.setStatus(Enums.Status.READY);
					taskLinkedQueue.enqueue(nextTask);
				}

			} catch (EmptyCollectionException e) {
				System.err.println(e.getMessage());
			}
		}
	}
*/