import Enums.DeviceType;
import Enums.Status;

/**
 * Represents a Task in the Creeping System.
 * Implements Comparable to allow task comparison based on duration.
 * Implements Runnable to allow task execution in a separate thread.
 */
public class Task implements Comparable<Task>, Runnable {

	private final String name;
	private int priority;
	private long duration;
	private final int memorySize;
	private DeviceType deviceRequired;
	private Status status;

	/**
	 * Constructs a new Task with the specified parameters.
	 *
	 * @param name the name of the task
	 * @param priority the priority of the task
	 * @param duration the duration of the task in milliseconds
	 * @param memorySize the memory size required by the task in MB
	 * @param deviceRequired the device type required by the task
	 */
	public Task(String name, int priority, long duration, int memorySize, DeviceType deviceRequired) {
		this.name = name;
		this.priority = priority;
		this.duration = duration;
		this.memorySize = memorySize;
		this.deviceRequired = deviceRequired;
		this.status = Status.WAITING;
	}

	/**
	 * Gets the name of the task.
	 *
	 * @return the name of the task
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the priority of the task.
	 *
	 * @return the priority of the task
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * Gets the memory size required by the task.
	 *
	 * @return the memory size in MB
	 */
	public int getMemorySize() {
		return this.memorySize;
	}

	/**
	 * Gets the duration of the task.
	 *
	 * @return the duration in milliseconds
	 */
	public long getDuration() {
		return this.duration;
	}

	/**
	 * Gets the current status of the task.
	 *
	 * @return the status of the task
	 */
	public Status getStatus() {
		return this.status;
	}

	/**
	 * Sets the duration of the task.
	 *
	 * @param duration the new duration in milliseconds
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Gets the device type required by the task.
	 *
	 * @return the device type required by the task
	 */
	public DeviceType getDeviceRequired() {
		return this.deviceRequired;
	}

	/**
	 * Sets the priority of the task.
	 *
	 * @param priority the new priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Sets the status of the task.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * Compares this task with another task based on duration.
	 *
	 * @param other the other task to compare to
	 * @return a negative integer, zero, or a positive integer as this task's duration
	 *         is less than, equal to, or greater than the other task's duration
	 */
	@Override
	public int compareTo(Task other) {
		return Long.compare(this.duration, other.getDuration());
	}

	/**
	 * Runs the task, simulating its execution.
	 * Changes the status to RUNNING, waits for the duration of the task,
	 * and then changes the status to COMPLETED.
	 */
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