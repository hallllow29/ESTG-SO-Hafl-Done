import Core.CreepingSystem;
import Core.Task;
import Enums.Priority;

public class Main {
    public static void main(String[] args) {
        // Inicializa o sistema
        CreepingSystem creepingSystem = new CreepingSystem(new Core.Kernel());

        System.out.println("STARTING CREEPING SYSTEM...");
        creepingSystem.start();

        System.out.println("\n=== TEST 1: MEMORY ALLOCATION ===");
        // Adiciona tarefas que ocupam memória
        Task task1 = new Task("Task1", Priority.HIGH);
        task1.setMemorySize(256);

        Task task2 = new Task("Task2", Priority.HIGH);
        task2.setMemorySize(256);

        Task task3 = new Task("Task3", Priority.HIGH);
        task3.setMemorySize(256);

        Task task4 = new Task("Task4", Priority.HIGH);
        task4.setMemorySize(256);

        Task task5 = new Task("Task5", Priority.HIGH);
        task5.setMemorySize(256);

        Task task6 = new Task("Task6", Priority.HIGH);
        task6.setMemorySize(256);

        creepingSystem.addTask(task1);
        creepingSystem.addTask(task2);
        creepingSystem.addTask(task3);
        creepingSystem.addTask(task4);
        creepingSystem.addTask(task5);
        creepingSystem.addTask(task6);

        // Aguarda a execução das tarefas
        try {
            Thread.sleep(10000); // Espera 10 segundos para todas as tarefas serem processadas
        } catch (InterruptedException e) {
            System.err.println("MAIN THREAD INTERRUPTED: " + e.getMessage());
        }

        System.out.println("\n=== TEST 2: MEMORY REUSE AFTER TASK COMPLETION ===");
        // Adiciona mais tarefas para verificar reutilização da memória liberada
        Task task7 = new Task("Task7", Priority.HIGH);
        task7.setMemorySize(512);

        Task task8 = new Task("Task8", Priority.HIGH);
        task8.setMemorySize(256);

        creepingSystem.addTask(task7);
        creepingSystem.addTask(task8);

        try {
            Thread.sleep(5000); // Aguarda execução das novas tarefas
        } catch (InterruptedException e) {
            System.err.println("MAIN THREAD INTERRUPTED: " + e.getMessage());
        }

        System.out.println("STOPPING CREEPING SYSTEM...");
        creepingSystem.stop();

        System.out.println("\nFINAL MEMORY STATUS:");
        creepingSystem.getKernel().getMemory().printMemoryStatus();
    }
}
