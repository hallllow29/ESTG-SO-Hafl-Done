package Core;
import lib.HasTables.HashMap;

public class Server implements Runnable {

    private final HashMap<String, Service> services;
    private Thread serverThread;
    private boolean isRunning;

    public Server() {
        this.isRunning = false;
        this.services = new HashMap<String, Service>();
        initServices();
    }

     private synchronized void initServices() {
        this.services.put("HARDWARE", new Service( "HARDWAR"));
        this.services.put("MANAGER", new Service( "MANAGER"));
    }



    public synchronized void start() {
        if (isRunning) {
            System.out.println("SERVER ALREADY STARTED");
            return;
        }

        System.out.println("SERVER STARTING...");

        this.isRunning = true;

        this.serverThread = new Thread(this);
        this.serverThread.start();

        System.out.println("SERVER STARTED");


    }

    public synchronized void stop() {
        if (!isRunning) {
            System.out.println("SERVER ALREADY STOPPED");
            return;
        }

        System.out.println("SERVER STOPPING...");

        this.isRunning = false;

        try {
            serverThread.join();

        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("SERVER STOPPED");

    }

    public void run() {
        startAllServices();

        while (this.isRunning) {
            for (Service service : this.services.getValues()) {
                if (service.asksProcess()) {
                    service.process();
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            }
        }

        stopAllServices();
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    private void startAllServices() {
        for (Service service : this.services.getValues()) {
            service.start();
        }
    }

    private void stopAllServices() {
        for (Service service : this.services.getValues()) {
            service.stop();
        }
    }
}
