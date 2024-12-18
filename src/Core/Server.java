package Core;

import lib.HasTables.HashMap;

public class Server implements Runnable {

    private final HashMap<String, Service> services; // Mapa de serviços gerenciados pelo servidor
    private Thread serverThread;                     // Thread principal do servidor
    private volatile boolean isRunning;              // Flag para controlar a execução do servidor

    /**
     * Construtor do servidor.
     * Inicializa os serviços.
     */
    public Server() {
        this.isRunning = false;
        this.services = new HashMap<>();
        initServices();
    }

    /**
     * Inicializa os serviços do servidor.
     */
    private void initServices() {
        this.services.put("HARDWARE", new Service("HARDWARE"));
        this.services.put("MANAGER", new Service("MANAGER"));
        System.out.println("SERVICES INITIALIZED.");
    }

    /**
     * Inicia o servidor.
     */
    public synchronized void start() {
        if (isRunning) {
            System.out.println("SERVER ALREADY STARTED.");
            return;
        }

        System.out.println("SERVER STARTING...");
        isRunning = true;

        this.serverThread = new Thread(this, "Server-Thread");
        this.serverThread.start();

        System.out.println("SERVER STARTED.");
    }

    /**
     * Para o servidor.
     */
    public synchronized void stop() {
        if (!isRunning) {
            System.out.println("SERVER ALREADY STOPPED.");
            return;
        }

        System.out.println("SERVER STOPPING...");
        isRunning = false;

        try {
            if (serverThread != null) {
                serverThread.join(); // Aguarda o término da thread principal
            }
        } catch (InterruptedException e) {
            System.err.println("SERVER STOP INTERRUPTED: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        System.out.println("SERVER STOPPED.");
    }

    /**
     * Método principal da thread do servidor.
     */
    @Override
    public void run() {
        startAllServices();

        while (isRunning) {
            try {
                for (Service service : this.services.getValues()) {
                    if (service.asksProcess()) {
                        service.process();
                    }
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.err.println("SERVER INTERRUPTED: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("UNEXPECTED ERROR IN SERVER: " + e.getMessage());
            }
        }

        stopAllServices();
    }

    /**
     * Retorna o estado atual do servidor.
     *
     * @return true se o servidor estiver em execução.
     */
    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    /**
     * Inicia todos os serviços.
     */
    private void startAllServices() {
        System.out.println("STARTING ALL SERVICES...");
        for (Service service : this.services.getValues()) {
            service.start();
        }
    }

    /**
     * Para todos os serviços.
     */
    private void stopAllServices() {
        System.out.println("STOPPING ALL SERVICES...");
        for (Service service : this.services.getValues()) {
            service.stop();
        }
    }
}
