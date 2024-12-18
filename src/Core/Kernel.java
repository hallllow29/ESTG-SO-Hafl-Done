package Core;

import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.trees.PriorityQueue;

public class Kernel {

	private final CPU cpu;
	private final Mem memory;
	private final Devices devices;
	private final Server server;
	private final PriorityQueue<Task> taskPriorityQueue;
	private boolean isRunning;

	public Kernel() {
		this.taskPriorityQueue = new PriorityQueue<>();
		this.cpu = new CPU();
		this.memory = new Mem();
		this.devices = new Devices();
		this.server = new Server();
		this.isRunning = false;
	}

	public CPU getCpu() {
		return this.cpu;
	}

	public Mem getMemory() {
		return this.memory;
	}

	public synchronized boolean isRunning() {
		return this.isRunning;
	}

	/**
	 * Inicializa o Kernel e seus componentes.
	 */
	public synchronized void start() {
		if (this.isRunning) {
			System.out.println("KERNEL ALREADY STARTED");
			return;
		}

		System.out.println("KERNEL STARTING...");
		this.cpu.start();
		this.memory.start();
		this.devices.start();
		this.server.start();

		this.isRunning = true;
		System.out.println("KERNEL STARTED");
	}

	/**
	 * Encerra o Kernel e seus componentes.
	 */
	public synchronized void stop() {
		if (!this.isRunning) {
			System.out.println("KERNEL ALREADY STOPPED");
			return;
		}

		System.out.println("KERNEL STOPPING...");
		this.cpu.stop();
		this.memory.stop();
		this.devices.stop();
		this.server.stop();

		this.isRunning = false;
		System.out.println("KERNEL STOPPED");
	}

	/**
	 * Adiciona uma nova tarefa à fila de prioridade.
	 */
	public synchronized void addTask(Task task) {
		if (!this.isRunning) {
			System.out.println("KERNEL IS NOT RUNNING. START THE KERNEL FIRST.");
			return;
		}

		if (taskValid(task)) {
			taskPriorityQueue.addElement(task, task.getPriority().ordinal());
			System.out.println("NEW TASK '" + task.getName() + "' ADDED TO PRIORITY QUEUE.");
		}
	}

	/**
	 * Processa a próxima tarefa da fila de prioridade.
	 */
	public synchronized void processNextTask() {
		if (!this.isRunning) {
			System.out.println("KERNEL IS NOT RUNNING.");
			return;
		}

		while (!taskPriorityQueue.isEmpty()) {
			try {
				Task nextTask = taskPriorityQueue.removeElement();
				new Thread(() -> {
					if (validateResources(nextTask)) {
						cpu.executeOneTask(nextTask);
						releaseResources(nextTask);
					} else {
						System.out.println("RESOURCES NOT AVAILABLE FOR TASK: " + nextTask.getName());
						synchronized (taskPriorityQueue) {
							if (!memory.hasAvailableMemory(nextTask.getMemorySize())) {
								System.out.println("TASK '" + nextTask.getName() + "' REQUIRES MORE MEMORY THAN AVAILABLE");
							} else {
								taskPriorityQueue.addElement(nextTask, nextTask.getPriority().ordinal());
							}

						}
					}
				}).start();
			} catch (EmptyCollectionException e) {
				System.err.println("TASK QUEUE ERROR: " + e.getMessage());
			}
		}
	}

	/**
	 * Valida os recursos necessários para uma tarefa.
	 */
	private boolean validateResources(Task task) {
		if (!cpu.isAvailable()) {
			System.out.println("CPU IS NOT AVAILABLE FOR TASK '" + task.getName() + "'");
			return false;
		}

		if (!memory.hasAvailableMemory(task.getMemorySize())) {
			System.out.println("MEMORY IS NOT AVAILABLE FOR TASK '" + task.getName() + "'");
			return false;
		}

		return memory.allocateFF(task);
	}

	/**
	 * Libera os recursos após a execução de uma tarefa.
	 */
	private void releaseResources(Task task) {
		try {
			this.memory.freeMemory(task.getName());
			System.out.println("RESOURCES RELEASED FOR TASK '" + task.getName() + "'");
			this.memory.printMemoryStatus();
		} catch (Exception e) {
			System.err.println("ERROR RELEASING RESOURCES FOR TASK '" + task.getName() + "': " + e.getMessage());
		} catch (NotElementComparableException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Valida se uma tarefa é válida para adição.
	 */
	private boolean taskValid(Task task) {
		if (task == null) {
			System.out.println("TASK CANNOT BE NULL");
			return false;
		}
		if (task.getDuration() <= 0) {
			System.out.println("TASK '" + task.getName() + "' HAS INVALID DURATION");
			return false;
		}
		if (task.getName() == null || task.getName().isEmpty()) {
			System.out.println("TASK NAME CANNOT BE NULL OR EMPTY");
			return false;
		}
		return true;
	}

	public Devices getDevices() {
		return devices;
	}
}
