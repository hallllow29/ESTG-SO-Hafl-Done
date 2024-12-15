public class Main {

	public static void main(String[] args) {


		Kernel kernel = new Kernel();
		CreepingSystem creepingSystem = new CreepingSystem(kernel);

		creepingSystem.start();

		Task task_1 = new Task("Task_1", 1, 0, 24, DeviceType.INPUT);
		Task task_2 = new Task("Task_2", 2, 0, 200, DeviceType.OUTPUT);
		Task task_3 = new Task("Task_3", 3, 0, 50, DeviceType.INPUT);

		creepingSystem.addTask(task_1);
		creepingSystem.addTask(task_2);
		creepingSystem.addTask(task_3);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		creepingSystem.stop();

		System.out.println("Task " + task_1.getName() + " Status " + task_1.getStatus());
		System.out.println("Task " + task_2.getName() + " Status " + task_2.getStatus());
		System.out.println("Task " + task_3.getName() + " Status " + task_3.getStatus());
	}


}
