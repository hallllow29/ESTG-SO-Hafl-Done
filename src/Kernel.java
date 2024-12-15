import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedList;

public class Kernel {


	private CPU cpu;
	private Mem mem;
	private Devices devices;
	private Server server;
	LinkedList<Task> taskLinkedList;
	boolean isRunning;
	private final int MAX_TASK_CAPACITY = 5;

	public Kernel() {
		this.taskLinkedList = new LinkedList<Task>();
		this.cpu = new CPU();
		this.mem = new Mem();
		try {
			this.devices = new Devices();
		} catch (EmptyCollectionException e) {
			System.err.println(e.getMessage());
		}
		// this.server = new Server();
		this.isRunning = false;
	}


	public boolean isRunning() {
		return this.isRunning;
	}


	public synchronized void start() {

		if (this.isRunning) {
			System.out.println("KERNEL ALREADY STARTED");
			return;
		}

		System.out.println("KERNEL STARTING...");

		cpu.start();
		mem.start();
		devices.start();
		// server.start();

		this.isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	public synchronized void stop() {

		if (!this.isRunning) {
			System.out.println("KERNEL ALREADY STOPPED");
		}

		System.out.println("KERNEL STOPPING...");

		cpu.stop();
		mem.stop();
		devices.stop();
		// server.stop();

		this.isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	public synchronized void addTask(Task task) {

		if (this.isRunning == false) {
			System.out.println("KERNEL DID NOT STARTED YET");
			return;
		}

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return;
		}

		if (taskLinkedList.size() >= MAX_TASK_CAPACITY) {
			System.out.println("MAX CAPACITY OF TASKS IN KERNEL REACHED");
			return;
		}

		if (this.isRunning == true) {
			try {
				this.taskLinkedList.add(task);
				System.out.println("New task " + task.getName() + " was added.");
			} catch (NotElementComparableException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public synchronized boolean validateRessources(Task task) {

		if (!cpu.isAvailable()) {
			System.out.println("CPU IS NOT AVAILABLE");
			return false;
		}

		if (!mem.hasAvailableMemory(task.getMemorySize())) {
			System.out.println("MEMORY IS NOT AVAILABLE");
			return false;
		}

		if (!devices.isDeviceAvailable(task.getDeviceRequired().name())) {
			System.out.println("DEVICE IS NOT AVAILABLE");
			return false;
		}

		return true;
	}

	public synchronized void executeOneTask(Task task) {

		if (!taskValid(task)) {
			System.out.println("TASK IS NOT VALID");
			return;
		}

		if (!validateRessources(task)) {
			System.out.println("RESOURCES ARE NOT AVAILABLE");
			return;
		}

		task.setStatus(Status.RUNNING);

		cpu.executeTask(task);

		task.setStatus(Status.COMPLETED);

	}

	private synchronized boolean taskValid(Task task) {

		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return false;
		}

		try {
			if (taskDuplicate(task)) {
				System.out.println("TASK CANNOT BE A DUPLICATE");
				return false;
			}
		} catch (EmptyCollectionException e) {
			System.out.println(e.getMessage());
		}

		return true;

	}

	private synchronized boolean taskDuplicate(Task task) throws EmptyCollectionException {
		return this.taskLinkedList.contains(task);
	}

}

// TODO: when adding a Task, validate if the system is running, then validate the Task itself and if it fits the Kernel resources.
