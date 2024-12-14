public class CPU {


	private boolean isAvailable;
	private boolean isRunning;



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
}

// Do we need now scheduling algorithms?
// TODO: First Come, First Served (FCFS) scheduling.