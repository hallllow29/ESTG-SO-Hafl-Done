import Core.CreepingSystem;
import Core.Kernel;
import Core.Task;
import Enums.Priority;

public class Main {

	public static void main(String[] args) {

		Kernel kernel = new Kernel();
		CreepingSystem creepingSystem = new CreepingSystem(kernel);

		creepingSystem.start();
 		Task memoryHog1 = new Task("MemoryHog1", Priority.HIGH);
        Task memoryHog2 = new Task("MemoryHog2", Priority.HIGH);

        memoryHog1.setMemorySize(512);
        memoryHog2.setMemorySize(512);

        creepingSystem.addTask(memoryHog1);
        creepingSystem.addTask(memoryHog2);

        Task longTask1 = new Task("LongTask1", Priority.MEDIUM);
        Task longTask2 = new Task("LongTask2", Priority.MEDIUM);

        longTask1.setDuration(3000);
        longTask2.setDuration(3000);

        creepingSystem.addTask(longTask1);
        creepingSystem.addTask(longTask2);

        Task displayTask1 = new Task("DisplayTask1", Priority.LOW);
        Task displayTask2 = new Task("DisplayTask2", Priority.LOW);

        // Aqui elas precisam de o mesmo dispostivo temos que analisar isto...
        displayTask1.addDevice("DISPLAY");
        displayTask2.addDevice("DISPLAY");

        creepingSystem.addTask(displayTask1);
        creepingSystem.addTask(displayTask2);

        // Opah se calhar mais de 10 de segundos faz a diferenca.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        creepingSystem.stop();

        System.out.println("\nFINAL RESULTS");
        // TODO: Através do kernel vai se buscar os trackings da memory
        // System.out.println("USED MEMEORY: " + memory.getTotalMemoryUsed());
        // System.out.println("TASK COMPLETED: " + kernel.getCompletedTaskCount());
	}

}
