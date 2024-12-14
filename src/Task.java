public class Task implements Comparable<Task>, Runnable {

	private final String name;
	private int priority;
	private long duration;
	private Status status;

	public Task(String name, int priority, long duration) {
		this.name = name;
		this.priority = priority;
		this.duration = duration;
		// Each task has WAITING status as default value
		this.status = Status.WAITING;
	}

	public String getName() {
		return this.name;
	}

	public int getPriority() {
		return this.priority;
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
