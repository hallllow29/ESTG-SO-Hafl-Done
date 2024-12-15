import lib.exceptions.EmptyCollectionException;
import lib.trees.PriorityQueue;
public class CreepingSystem implements Runnable {

	private Kernel kernel;
	private Thread thread;
	private PriorityQueue<Task> taskPriorityQueue;
	private boolean isRunning;

	public CreepingSystem(Kernel kernel) {
		this.kernel = kernel;
		this.isRunning = false;
	}

	public void start() {
		if (this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY RUNNING");
			return;
		}

		System.out.println("CREEPING SYSTEM STARTING...");

		this.isRunning = true;
		this.thread = new Thread(this);
		this.thread.start();

		System.out.println("CREEPING SYSTEM STARTED");

	}

	public void stop(){
		if (!this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY STOPPED");
			return;
		}

		System.out.println("CREEPING SYSTEM STOPPING...");
		this.isRunning = false;

		try {
			this.thread.join();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		System.out.println("CREEPING SYSTEM STOPPED");
	}

	public void run() {
		this.kernel.start();

		while (this.isRunning) {
			Task task = null;

			synchronized (this.taskPriorityQueue) {
				if (!this.taskPriorityQueue.isEmpty()) {
					try {
						task = this.taskPriorityQueue.removeElement();
					} catch (EmptyCollectionException e) {
						System.err.println(e.getMessage());
					}

					if (task != null) {
						this.kernel.executeOneTask(task);
					}

				}
			}
		}

		kernel.stop();
	}

	public void addTask(Task task) {
		synchronized (this.taskPriorityQueue) {
			this.taskPriorityQueue.addElement(task, task.getPriority());
		}
	}
}
