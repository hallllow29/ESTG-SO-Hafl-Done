package Core;

/**
 * The {@code CreepingSystem} class represents a system that manages the execution
 * of tasks in conjunction with an instance of the {@code Kernel}. It operates
 * in a loop, processing tasks and interacting with the kernel for task management.
 * This class is implemented as a runnable and runs its processing logic in a separate thread.
 */
public class CreepingSystem implements Runnable {

	private static final int THREAD_SLEEP_TIME = 1000; // Tempo de espera no loop (ms)
	private final Kernel kernel;                      // Instância do Kernel
	private Thread thread;                            // Thread principal do sistema
	private volatile boolean isRunning;               // Estado de execução do sistema

	/**
	 * Construtor do sistema CreepingSystem.
	 *
	 * @param kernel Instância do kernel.
	 */
	public CreepingSystem(Kernel kernel) {
		if (kernel == null) {
			throw new IllegalArgumentException("Kernel cannot be null.");
		}
		this.kernel = kernel;
		this.isRunning = false;
	}

	/**
	 * Inicia o sistema CreepingSystem.
	 */
	public synchronized void start() {
		if (this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY RUNNING");
			return;
		}

		System.out.println("[" + System.currentTimeMillis() + "] CREEPING SYSTEM STARTING...");
		this.isRunning = true;

		this.thread = new Thread(this, "CreepingSystem-Thread");
		this.thread.start();

		kernel.start();
		System.out.println("[" + System.currentTimeMillis() + "] CREEPING SYSTEM STARTED");
	}

	/**
	 * Para o sistema CreepingSystem.
	 */
	public synchronized void stop() {
		if (!this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY STOPPED");
			return;
		}

		System.out.println("[" + System.currentTimeMillis() + "] CREEPING SYSTEM STOPPING...");
		this.isRunning = false;

		if (this.thread != null) {
			this.thread.interrupt();
			try {
				this.thread.join();
			} catch (InterruptedException e) {
				System.err.println("ERROR STOPPING CREEPING SYSTEM: " + e.getMessage());
				Thread.currentThread().interrupt();
			}
		}

		kernel.stop();
		System.out.println("[" + System.currentTimeMillis() + "] CREEPING SYSTEM STOPPED");
	}

	/**
	 * Loop principal de execução do sistema.
	 */
	@Override
	public void run() {
		while (this.isRunning && !Thread.currentThread().isInterrupted()) {
			try {
				this.kernel.processNextTask();
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				System.err.println("CREEPING SYSTEM INTERRUPTED: " + e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Adiciona uma tarefa ao Kernel.
	 *
	 * @param task Tarefa a ser adicionada.
	 */
	public void addTask(Task task) {
		if (task == null) {
			throw new IllegalArgumentException("Task cannot be null.");
		}
		this.kernel.addTask(task);
		System.out.println("TASK " + task.getName() + " ADDED TO CREEPING SYSTEM");
	}
}
