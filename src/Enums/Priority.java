package Enums;

public enum Priority {
	HIGH(0),
	MEDIUM(1),
	LOW(2);

	private final int priority;

	Priority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
}
