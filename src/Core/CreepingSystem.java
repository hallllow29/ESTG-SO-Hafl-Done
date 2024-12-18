package Core;

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

		// Inicializa a thread principal do sistema
		this.thread = new Thread(this, "CreepingSystem-Thread");
		this.thread.start();

		// Inicia o kernel
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

		// Solicita interrupção da thread
		if (this.thread != null) {
			this.thread.interrupt();
			try {
				this.thread.join(); // Aguarda o término da thread
			} catch (InterruptedException e) {
				System.err.println("ERROR STOPPING CREEPING SYSTEM: " + e.getMessage());
				Thread.currentThread().interrupt();
			}
		}

		// Para o kernel
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
				Thread.currentThread().interrupt(); // Restaura o estado de interrupção
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

	/**
	 * Retorna se o sistema está em execução.
	 *
	 * @return true se o sistema estiver em execução.
	 */
	public synchronized boolean isRunning() {
		return this.isRunning;
	}

	public Kernel getKernel() {
		return kernel;
	}
}