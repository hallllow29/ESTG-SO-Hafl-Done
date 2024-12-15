import lib.exceptions.EmptyCollectionException;
import lib.lists.ArrayUnorderedList;

import java.util.concurrent.Semaphore;
public class CreepingSystem implements Runnable {

	private static final int QUAD_THREADS = 4;
	private Kernel kernel;
	private Semaphore semaphore;
	private Thread thread;
	private ArrayUnorderedList<Thread> processingThreads;
	private boolean isRunning;

	public CreepingSystem(Kernel kernel) {
		this.kernel = kernel;
		this.isRunning = false;
		this.processingThreads = new ArrayUnorderedList<>();
		this.semaphore = new Semaphore(QUAD_THREADS);
	}

	public synchronized void start() {
		if (this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY RUNNING");
			return;
		}

		System.out.println("CREEPING SYSTEM STARTING...");

		this.isRunning = true;

		this.kernel.start();

		// // ISTO È UMA..
		// this.thread = new Thread(this);
		// this.thread.start();

		// ISTO SAO VÀRIAS
		// Inicia múltiplas threads de processamento
		for (int i = 0; i < QUAD_THREADS; i++) {
			Thread thread = new Thread(this, "ProcessingThread-" + i);
			thread.start();
			processingThreads.addToRear(thread);
		}

		System.out.println("CREEPING SYSTEM STARTED");
		System.out.println("WITH THIS MUCH THREADS: " + processingThreads.size());

	}

	public synchronized void stop() {
		if (!this.isRunning) {
			System.out.println("CREEPING SYSTEM ALREADY STOPPED");
			return;
		}

		System.out.println("CREEPING SYSTEM STOPPING...");
		this.isRunning = false;

		for (Thread thread : processingThreads) {
			try {
				thread = processingThreads.removeFirst();
				thread.join();
			} catch (InterruptedException | EmptyCollectionException e) {
				System.err.println(e.getMessage());
			}
		}

		kernel.stop();

		System.out.println("CREEPING SYSTEM STOPPED");
	}

	public void run() {


		while (this.isRunning) {
			try {
				semaphore.acquire();
				try {
					this.kernel.processNextTask();
				} finally {
					semaphore.release();
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage() + "INTERRUPTED");
			}
		}
	}

	public synchronized void addTask(Task task) {
		synchronized (this.kernel) {
			this.kernel.addTask(task);
		}
	}
}
