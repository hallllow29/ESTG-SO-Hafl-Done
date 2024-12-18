package Core;

import Enums.Priority;
import lib.lists.ArrayUnorderedList;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Scanner;

public class MainMenu {

    private static LinkedList<Integer> memoryUsageData = new LinkedList<>();
    private static ArrayUnorderedList<Task> tasks = new ArrayUnorderedList<>();

    public static void mainMenu() {
        boolean running = true;
        CreepingSystem creepingSystem = new CreepingSystem(new Kernel());
        Scanner scanner = new Scanner(System.in);

        System.out.println("==== WELCOME TO CREEPING SYSTEM ====");
        System.out.println("==== MAIN MENU ====");

        while (running) {
            System.out.println("1 - Start Creeping System");
            System.out.println("2 - Stop Creeping System");
            System.out.println("3 - Add Task");
            System.out.println("4 - Show Memory Usage Chart");
            System.out.println("5 - Save System to JSON");
            System.out.println("6 - Exit");

            if (scanner.hasNextInt()) {
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        creepingSystem.start();
                        break;
                    case 2:
                        creepingSystem.stop();
                        break;
                    case 3:
                        addTask(creepingSystem, scanner);
                        break;
                    case 4:
                        showMemoryUsageChart();
                        break;
                    case 5:
                        SaveToJson.saveSystem(tasks);
                        System.out.println("System saved to JSON.");
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                        break;
                }
            }
        }
    }

    private static void addTask(CreepingSystem creepingSystem, Scanner scanner) {
        System.out.println("Enter the task description:");
        String name = scanner.next();
        int priorityOption = -1;

        while (priorityOption < 0 || priorityOption > 2) {
            System.out.println("Enter Task Priority (0-HIGH, 1-MEDIUM, 2-LOW):");
            priorityOption = scanner.nextInt();
        }

        Priority priority = Priority.values()[priorityOption];

        System.out.println("Enter the task duration (ms):");
        long duration;

        try {
            duration = scanner.nextLong();
        } catch (NumberFormatException e) {
            System.out.println("Invalid duration. Task not added.");
            return;
        }

        System.out.println("Enter task memory size (MB): ");
        int memorySize;
        try {
            memorySize = scanner.nextInt();
        } catch (NumberFormatException e) {
            System.out.println("Invalid memory size. Task not added.");
            return;
        }

        Task task = new Task(name, priority);
        task.setDuration(duration);
        task.setMemorySize(memorySize);

        creepingSystem.addTask(task);
        tasks.addToRear(task);
        System.out.println("Task added successfully.");

        memoryUsageData.add(memorySize);

    }

    private static void showMemoryUsageChart() {
        SwingUtilities.invokeLater(() -> {
            MemoryUsageChart example = new MemoryUsageChart("Memory Usage Chart", memoryUsageData);
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
