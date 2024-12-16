package Core;
public class CreepingSystem implements Runnable {

	private static final int QUAD_THREADS = 4;

	private Kernel kernel;
	private Thread thread;
	private boolean isRunning;

	public CreepingSystem(Kernel kernel) {
		this.kernel = kernel;
		this.isRunning = false;
	}

	public synchronized void start() {
		if (this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY RUNNING");
			return;
		}

		System.out.println("CREEPING SYSTEM STARTING...");

		this.isRunning = true;
		this.thread = new Thread(this);
		thread.start();

		kernel.start();

		System.out.println("CREEPING SYSTEM STARTED");

	}

	public synchronized void stop() {
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

		kernel.stop();

		System.out.println("CREEPING SYSTEM STOPPED");
	}

	public void run() {
		while (this.isRunning) {
			this.kernel.processNextTask();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void addTask(Task task) {
		this.kernel.addTask(task);
	}
}
