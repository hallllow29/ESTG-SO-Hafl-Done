package Core;
import Enums.Priority;
import Enums.Status;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;
import lib.lists.LinkedUnorderedList;

public class  Task implements Comparable<Task> {

	private final String name;
	private final Priority priority;
	private long duration;
	private int memorySize;
	private LinkedUnorderedList<String> deviceList;
	private Status status;

	public Task(String name, Priority priority) {
		this.name = name;
		this.priority = priority;
		this.duration = 1000;
		this.memorySize = 0;
		this.deviceList = new LinkedUnorderedList<>();
		// Each task has WAITING status as default value
		this.status = Status.WAITING;
	}

	public String getName() {
		return this.name;
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

	public Priority getPriority() {
		return this.priority;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public void addDevice(String deviceName) {
		try {
			if (!this.deviceList.contains(deviceName)) {
				this.deviceList.addToRear(deviceName);
			}
		} catch (EmptyCollectionException e) {
			System.out.println("TASK " + getName() + " ALREADY CONTAINS " + deviceName);
		}
	}

	 public void removeRequiredDevice(String deviceName) {
        try {
			this.deviceList.remove(deviceName);
		} catch (EmptyCollectionException | ElementNotFoundException e) {
			System.out.println();
		}
    }

	@Override
	public int compareTo(Task other) {
		return this.priority.ordinal() - other.getPriority().ordinal();
	}

}
