public class Task implements Comparable<Task>, Runnable {

	private final String name;
	private int priority;
	private long duration;
	private final int memorySize;
	private DeviceType deviceRequired;
	private Status status;

	public Task(String name, int priority, long duration, int memorySize, DeviceType deviceRequired) {
		this.name = name;
		this.priority = priority;
		this.duration = duration;
		this.memorySize = memorySize;
		this.deviceRequired = deviceRequired;
		// Each task has WAITING status as default value
		this.status = Status.WAITING;
	}

	public String getName() {
		return this.name;
	}

	public int getPriority() {
		return this.priority;
	}

	public int getMemorySize() {
		return this.memorySize;
	}

	public long getDuration() {
		return this.duration;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public DeviceType getDeviceRequired() {
		return this.deviceRequired;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int compareTo(Task other) {
		return Long.compare(this.duration, other.getDuration());
	}

	@Override
	public void run() {
		this.status = Status.RUNNING;
		System.out.println("TASK " + this.name + " STARTED");

		try {
			Thread.sleep(this.duration);
		} catch (InterruptedException e) {
			this.status = Status.PAUSED;
			System.err.println("TASK " + this.name + " PAUSED");
			return;
		}

		this.status = Status.COMPLETED;
		System.out.println("TASK " + this.name + " COMPLETED");
	}
}
