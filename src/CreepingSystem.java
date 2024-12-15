
public class CreepingSystem {

	Kernel kernel;
	boolean isRunning;

	public CreepingSystem(Kernel kernel) {
		this.kernel = kernel;
		this.isRunning = false;
	}

	public void start(){
		this.isRunning = true;
		this.kernel.start();

	}

	public void stop(){
		this.isRunning = false;
		this.kernel.stop();
	}

	public void addTask(Task task) {

		if (kernel.validateRessources(task)) {
			this.kernel.addTask(task);
		}

	}

}
