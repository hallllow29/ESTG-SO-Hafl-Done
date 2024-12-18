package Core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private static final String LOG_FILE = "system_logs.txt";

    public static synchronized void log(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            String logMessage = "[" + System.currentTimeMillis() + "] " + message;
            printWriter.println(logMessage);
            System.out.println(logMessage);
        } catch (IOException e) {
            System.err.println("ERROR WRITING TO LOG FILE: " + e.getMessage());
        }
    }
}