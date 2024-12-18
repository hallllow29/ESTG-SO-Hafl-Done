package Core;

import Enums.CPUState;
import Enums.Status;

public class CPU {

	private CPUState state; // Estado da CPU (RUNNING, STOPPED, etc.)
	private boolean isAvailable; // Define se a CPU está disponível
	private int completedTasks; // Contador de tarefas concluídas

	public CPU() {
		this.state = CPUState.STOPPED;
		this.isAvailable = true;
		this.completedTasks = 0;
		Logger.log("CPU INITIALIZED - STATE: STOPPED, AVAILABLE: TRUE");
	}

	public synchronized boolean isAvailable() {
		return this.isAvailable;
	}

	public synchronized void start() {
		if (this.state == CPUState.RUNNING) {
			Logger.log("CPU ALREADY STARTED");
			System.out.println("[" + System.currentTimeMillis() + "] CPU ALREADY STARTED");
			return;
		}

		Logger.log("CPU STARTING...");
		System.out.println("[" + System.currentTimeMillis() + "] CPU STARTING...");
		this.state = CPUState.RUNNING;
		this.isAvailable = true;
		Logger.log("CPU STARTED");
		System.out.println("[" + System.currentTimeMillis() + "] CPU STARTED");
	}

	public synchronized void stop() {
		if (this.state == CPUState.STOPPED) {
			Logger.log("CPU ALREADY STOPPED");
			System.out.println("[" + System.currentTimeMillis() + "] CPU ALREADY STOPPED");
			return;
		}

		Logger.log("CPU STOPPING...");
		System.out.println("[" + System.currentTimeMillis() + "] CPU STOPPING...");
		this.state = CPUState.STOPPED;
		this.isAvailable = false;
		Logger.log("CPU STOPPED");
		System.out.println("[" + System.currentTimeMillis() + "] CPU STOPPED");
	}

	private synchronized void runTask(Task task, long duration) {
		if (task == null) {
			System.out.println("[" + System.currentTimeMillis() + "] TASK CANNOT BE NULL");
			Logger.log("TASK CANNOT BE NULL");
			return;
		}

		if (this.state != CPUState.RUNNING) {
			Logger.log("CPU IS NOT RUNNING - TASK " + task.getName() + " CANNOT BE EXECUTED");
			System.out.println("[" + System.currentTimeMillis() + "] CPU IS NOT RUNNING");
			return;
		}

		this.isAvailable = false;
		task.setStatus(Status.RUNNING);
		Logger.log("TASK " + task.getName() + " EXECUTING");
		System.out.println("[" + System.currentTimeMillis() + "] CPU EXECUTING TASK " + task.getName());

		Thread taskThread = new Thread(() -> {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				task.setStatus(Status.PAUSED);
				Logger.log("TASK " + task.getName() + " INTERRUPTED");
				System.err.println("[" + System.currentTimeMillis() + "] TASK " + task.getName() + " INTERRUPTED");
				Thread.currentThread().interrupt();
			}
		});

		taskThread.start();
		try {
			taskThread.join(2000); // Timeout de 2 segundos
			if (taskThread.isAlive()) {
				taskThread.interrupt();
				Logger.log("TIMEOUT: TASK " + task.getName() + " INTERRUPTED");
				System.out.println("[" + System.currentTimeMillis() + "] TIMEOUT: TASK " + task.getName() + " INTERRUPTED");
				task.setStatus(Status.PAUSED);
			} else {
				task.setStatus(Status.COMPLETED);
				Logger.log("TASK " + task.getName() + " COMPLETED");
				System.out.println("[" + System.currentTimeMillis() + "] TASK " + task.getName() + " COMPLETED");
				this.completedTasks++;
			}
		} catch (InterruptedException e) {
			Logger.log("TASK " + task.getName() + " INTERRUPTED DURING EXECUTION");
			System.err.println("[" + System.currentTimeMillis() + "] TASK " + task.getName() + " INTERRUPTED DURING EXECUTION");
			Thread.currentThread().interrupt();
		} finally {
			this.isAvailable = true;
			Logger.log("CPU AVAILABLE");
		}
	}

	public synchronized void executeOneTask(Task task) {
		runTask(task, task.getDuration());
	}

	public synchronized void executeTaskDuration(Task task, long duration) {
		runTask(task, duration);
	}

	public synchronized int getCompletedTasks() {
		return this.completedTasks;
	}

	public synchronized CPUState getState() {
		return this.state;
	}
}
