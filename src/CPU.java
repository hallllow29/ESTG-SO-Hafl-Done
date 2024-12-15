public class CPU {

	private final static long TIME_SLICE = 1000;

	// CPU has a taskLinkedQueue of tasks to be executed
	private boolean isAvailable;
	private boolean isRunning;
	private int completedTasks;

	public CPU() {
		this.isRunning = false;
		this.isAvailable = true;
		this.completedTasks = 0;
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

	public synchronized void executeTaskDuration(Task task, long duration) {

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return;
		}

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
			return;
		}

		// CPU gets busy
		isAvailable = false;

		System.out.println("CPU " + Thread.currentThread().getName() + " EXECUTING TASK " + task.getName());
		task.setStatus(Status.RUNNING);

		try {
			// Sleep the thread for a certain duration...
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			task.setStatus(Status.PAUSED);
			System.err.println("CPU " + Thread.currentThread().getName() + " PAUSED TASK " + task.getName());
		} finally {
			// Even if Task gets interrupted
			this.isAvailable = true;

		}

		if (task.getStatus() != Status.PAUSED) {
			task.setStatus(Status.COMPLETED);
			System.out.println("CPU COMPLETED TASK " + task.getName());
			this.completedTasks++;
		}

	}

	public synchronized void executeTask(Task task) {

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return;
		}

		if (!this.isRunning) {
			System.out.println("CPU IS NOT RUNNING");
			return;
		}


		// CPU gets busy
		this.isAvailable = false;

		System.out.println("CPU " + Thread.currentThread().getName() + " EXECUTING TASK " + task.getName());
		task.setStatus(Status.RUNNING);

		try {
			// Sleep the thread for a second...
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			task.setStatus(Status.PAUSED);
			System.err.println("CPU " + Thread.currentThread().getName() + " PAUSED TASK " + task.getName());
			System.err.println(e.getMessage());
		} finally {
			// Even if Task gets interrupted
			this.isAvailable = true;
			System.out.println("CPU IS NOT BUSY");
		}

		if (task.getStatus() != Status.PAUSED) {
			task.setStatus(Status.COMPLETED);
			System.out.println("CPU COMPLETED TASK " + task.getName());
			this.completedTasks++;
		}
	}



	private int getCompletedTasks() {
		return this.completedTasks;
	}

	public int getTIMESLICE() {
		return (int) TIME_SLICE;
	}
}

// TODO: Switch the while loops as long as the cpu is running, a scheduler waits for tasks.
