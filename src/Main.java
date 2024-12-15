public class Main {

	public static void main(String[] args) {

		Kernel kernel = new Kernel();

		CreepingSystem creepingSystem = new CreepingSystem(kernel);

		creepingSystem.start();

		Task task_1 = new Task("Task_1", 5, 2000, 24, DeviceType.INPUT);
		Task task_2 = new Task("Task_2", 4, 2500, 200, DeviceType.OUTPUT);
		Task task_3 = new Task("Task_3", 6, 1500, 50, DeviceType.INPUT);

		kernel.addTask(task_1);
		kernel.addTask(task_2);
		kernel.addTask(task_3);

		kernel.executeOneTask(task_1);
		creepingSystem.stop();

		System.out.println("Task " + task_1.getName() + " Status " + task_1.getStatus());
		System.out.println("Task " + task_2.getName() + " Status " + task_2.getStatus());
		System.out.println("Task " + task_3.getName() + " Status " + task_3.getStatus());
	}


}
