import Core.Device;

import java.util.HashMap;
import java.util.logging.Logger;

public class Server {

    private boolean isRunning;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final HashMap<String, Service> services;
    private final HashMap<String, Device> devices;
    private Thread serverThread;

    public Server() {
        this.isRunning = false;
        this.services = new HashMap<String, Service>();
        this.devices = new HashMap<String, Device>();
    }

    public synchronized void start() {
        if (isRunning) {
            logger.warning("Server is already running...");
            return;
        }

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                logger.info("Starting server...");
                isRunning = true;
                startHardwareService();
                startServiceManager();
                logger.info("Server started");
            }
        };

        this.serverThread = new Thread(serverTask);
        this.serverThread.start();

    }

    public synchronized void stop() {
        if (!isRunning) {
            logger.warning("Server is already stopped...");
            return;
        }

        logger.info("Stopping server...");
        stopHardwareService();
        stopServiceManager();
        this.isRunning = false;

        try {
            serverThread.join();

        } catch (InterruptedException e) {
            logger.severe("Error stopping server" + e.getMessage());
        }

        logger.info("Server stopped");
    }

    private void startHardwareService() {
        logger.info("Starting hardware service...");
    }

    private void stopHardwareService() {
        logger.info("Stopping hardware service...");
    }

    private void startServiceManager() {
        logger.info("Starting service manager...");
    }

    private void stopServiceManager() {
        logger.info("Stopping service manager...");
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void registerService(String name, Service service) {
        if (this.services.putIfAbsent(name, service) != null) {
            logger.warning("Service " + name + " already exists");
        } else {
            logger.info("Service " + name + " registered");
        }
    }

    public synchronized void startService(String name) {
        Service service = this.services.get(name);
        if (service == null) {
            logger.warning("Service " + name + " does not exist");
            return;
        }
        service.start();
        logger.info("Service " + name + " started");
    }

    public synchronized void stopService(String name) {
        Service service = this.services.get(name);
        if (service == null) {
            logger.warning("Service " + name + " does not exist");
            return;
        }
        service.stop();
        logger.info("Service " + name + " stopped");
    }

    private void startAllServices() {
        for (Service service : this.services.values()) {
            service.start();
        }
    }

    private void stopAllServices() {
        for (Service service : this.services.values()) {
            service.stop();
        }
    }

    public synchronized void addDevice(String name, Device device) {
        if (this.devices.putIfAbsent(name, device) != null) {
            logger.warning("Device " + name + " already exists");
        } else {
            logger.info("Device " + name + " added");
        }
    }

    public synchronized void startDevice(String name) {
        Device device = this.devices.get(name);
        if (device == null) {
            logger.warning("Device " + name + " does not exist");
            return;
        }
        device.connect();
        logger.info("Device " + name + " connected");
    }

    public synchronized void stopDevice(String name) {
        Device device = this.devices.get(name);
        if (device == null) {
            logger.warning("Device " + name + " does not exist");
            return;
        }
        device.disconnect();
        logger.info("Device " + name + " disconnected");
    }
}