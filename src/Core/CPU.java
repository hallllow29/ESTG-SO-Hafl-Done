package Core;
import Enums.Status;

public class CPU {

	// Core.CPU has a taskLinkedQueue of tasks to be executed
	private boolean isAvailable;
	private boolean isRunning;

	public CPU() {
		this.isRunning = false;
		this.isAvailable = true;
	}

	public boolean isAvailable() {
		return this.isRunning && this.isAvailable;
	}

	public synchronized	void start() {

		if (this.isRunning) {
			System.out.println("Core.CPU ALREADY STARTED");
			return;
		}

		System.out.println("Core.CPU STARTING...");

		isAvailable = true;
		isRunning = true;
		System.out.println("Core.CPU STARTED");
	}

	public synchronized void stop() {

		if (!this.isRunning) {
			System.out.println("Core.CPU ALREADY STOPPED");
			return;
		}

		System.out.println("Core.CPU STOPPING");

		isAvailable = false;
		isRunning = false;
		System.out.println("Core.CPU STOPPED");
	}

	public synchronized void executeOneTask(Task task) {

		if (!this.isRunning) {
			System.out.println("Core.CPU IS NOT RUNNING");
		}

		this.isAvailable = false;

		System.out.println("Core.CPU " + Thread.currentThread().getName() + " EXECUTING TASK " + task.getName());
		task.setStatus(Status.RUNNING);

		try {
			Thread.sleep(task.getDuration());
		} catch (InterruptedException e) {
			task.setStatus(Status.PAUSED);
			System.err.println("Core.CPU " + Thread.currentThread().getName() + " PAUSED TASK " + task.getName());
		} finally {
			this.isAvailable = true;
		}

		if (task.getStatus() != Status.PAUSED) {
			task.setStatus(Status.COMPLETED);
			System.out.println("Core.CPU COMPLETED TASK " + task.getName());
		}
	}
}