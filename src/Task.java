public class Task {

	private final String name;
	private Status status;

	public Task(String name) {
		this.name = name;
		this.status = Status.CREATED;
	}

	public String getName() {
		return this.name;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
