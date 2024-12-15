import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedList;
import lib.lists.LinkedOrderedList;
import lib.queues.LinkedQueue;
import lib.trees.PriorityQueue;

import java.util.Iterator;

public class Kernel {

	private CPU cpu;
	private Mem mem;
	private Devices devices;
	private Server server;
	LinkedList<Task> taskLinkedList;
	PriorityQueue<Task> taskPriorityQueue;
	LinkedQueue<Task> taskLinkedQueue;
	boolean isRunning;
	private final int MAX_TASK_CAPACITY = 5;

	public Kernel() {
		this.taskLinkedList = new LinkedList<>();
		this.taskPriorityQueue = new PriorityQueue<Task>();
		this.taskLinkedQueue = new LinkedQueue<Task>();
		this.cpu = new CPU();
		this.mem = new Mem();
		try {
			this.devices = new Devices();
		} catch (EmptyCollectionException e) {
			System.err.println(e.getMessage());
		}
		// this.server = new Server();
		this.isRunning = false;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public synchronized void start() {

		if (this.isRunning) {
			System.out.println("KERNEL ALREADY STARTED");
			return;
		}

		System.out.println("KERNEL STARTING...");

		cpu.start();
		mem.start();
		devices.start();
		// server.start();

		this.isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	public synchronized void stop() {

		if (!this.isRunning) {
			System.out.println("KERNEL ALREADY STOPPED");
		}

		System.out.println("KERNEL STOPPING...");

		cpu.stop();
		mem.stop();
		devices.stop();
		// server.stop();

		this.isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	public synchronized void addTask(Task task) {

		/*if (this.isRunning == false) {
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
		}*/

		if (this.isRunning == true) {
			this.taskPriorityQueue.addElement(task, task.getPriority());
			System.out.println("New task " + task.getName() + " was added.");

		}
	}

	public synchronized boolean validateRessources(Task task) {

		if (!cpu.isAvailable()) {
			System.out.println("CPU IS NOT AVAILABLE");
			return false;
		}

		if (!mem.hasAvailableMemory(task.getMemorySize())) {
			System.out.println("MEMORY IS NOT AVAILABLE");
			return false;
		}

		if (!devices.isDeviceAvailable(task.getDeviceRequired().name())) {
			System.out.println("DEVICE IS NOT AVAILABLE");
			return false;
		}

		return true;
	}

	public synchronized void executeOneTask(Task task) {

		if (!taskValid(task)) {
			System.out.println("TASK IS NOT VALID");
			return;
		}

		if (!validateRessources(task)) {
			System.out.println("RESOURCES ARE NOT AVAILABLE");
			return;
		}

		task.setStatus(Status.RUNNING);

		cpu.executeTaskDuration(task, task.getDuration());

		task.setStatus(Status.COMPLETED);

	}

	private synchronized boolean taskValid(Task task) {

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return false;
		}

		return true;
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
				cpu.executeTaskDuration(nextTask, nextTask.getDuration());

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

				if (duration < cpu.getTIMESLICE()) {
					Thread.sleep(duration);
					nextTask.setStatus(Status.COMPLETED);
					System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus());
				} else {
					Thread.sleep(cpu.getTIMESLICE());
					// TODO: Pause the Task
					nextTask.setDuration(duration - cpu.getTIMESLICE());
					nextTask.setStatus(Status.PAUSED);
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
			System.out.println("CPU IS NOT RUNNING");
			return;
		}

		if (taskLinkedQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		LinkedOrderedList<Task> taskLinkedOrderedList = new LinkedOrderedList<Task>();
		Task nextTask = null;

		while (!this.taskLinkedQueue.isEmpty()) {
			try {
				nextTask = this.taskLinkedQueue.dequeue();
				nextTask.setStatus(Status.READY);

				System.out.println("TASK " + nextTask.getName() +
					" is " + nextTask.getStatus() +
					" with a duration: " + nextTask.getDuration() + "ms");

				taskLinkedOrderedList.add(nextTask);
			} catch (EmptyCollectionException | NotElementComparableException e) {
				System.err.println(e.getMessage());
			}
		}

		Iterator<Task> taskLinkedOrderedListIterator = taskLinkedOrderedList.iterator();

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
			System.out.println("CPU IS NOT RUNNING");
			return;
		}

		if (taskLinkedQueue.isEmpty()) {
			System.out.println("taskLinkedQueue is empty");
			return;
		}

		Task nextTask = null;

		while (!taskLinkedQueue.isEmpty()) {
			try {
				nextTask = taskLinkedQueue.dequeue();
				nextTask.setStatus(Status.RUNNING);
				System.out.println("TASK " + nextTask.getName() + " is " + nextTask.getStatus());

				long duration = Math.min(cpu.getTIMESLICE(), nextTask.getDuration());

				cpu.executeTaskDuration(nextTask, duration);

				nextTask.setDuration(nextTask.getDuration() - duration);

				if (nextTask.getDuration() == 0) {
					nextTask.setStatus(Status.COMPLETED);

				} else {
					nextTask.setStatus(Status.READY);
					taskLinkedQueue.enqueue(nextTask);
				}

			} catch (EmptyCollectionException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public synchronized void processNextTask() {
		if (taskPriorityQueue.isEmpty()) {
			return;  // Não há tarefas para processar
		}

		try {
			// Remove a próxima tarefa da fila de prioridade
			Task nextTask = taskPriorityQueue.removeElement();

			// Executa a tarefa no CPU
			if (cpu.isAvailable()) {
				cpu.executeTaskDuration(nextTask, nextTask.getDuration());
			} else {
				// Se CPU não está disponível, coloca de volta na fila
				taskPriorityQueue.addElement(nextTask, nextTask.getPriority());
			}

		} catch (EmptyCollectionException e) {
			System.err.println("Error processing next task: " + e.getMessage());
		}
	}

}

// TODO: when adding a Task, validate if the system is running, then validate the Task itself and if it fits the Kernel resources.
