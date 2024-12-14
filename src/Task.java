public class Task {

	private final String name;
	private int priority;
	private long duration;
	private Status status;

	public Task(String name, int priority, long duration) {
		this.name = name;
		this.priority = priority;
		this.duration = duration;
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
}
