import java.util.HashMap;
import java.util.logging.Logger;

public class Server {

    private boolean isRunning;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final HashMap<String, Service> services;

    public Server() {
        this.isRunning = false;
        this.services = new HashMap<String, Service>();
    }

    public synchronized void start() {
        if (isRunning) {
            logger.warning("Server is already running...");
            return;
        }

        logger.info("Starting server...");
        this.isRunning = true;
        startHardwareService();
        startServiceManager();
        logger.info("Server started");
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
}
