import Core.Device;
import Enums.DeviceType;
import lib.HasTables.HashMap;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;

import java.util.logging.Logger;

public class Devices {

    private final HashMap<String, Device> devices;
    private boolean isRunning;
    private static final Logger logger = Logger.getLogger(Devices.class.getName());

    public Devices() throws EmptyCollectionException {
        this.devices = new HashMap<String, Device>();
        this.isRunning = false;
        this.initDevices();
    }

    private synchronized void initDevices() throws EmptyCollectionException {
        this.addDevice("DISPLAY", DeviceType.INPUT);
        this.addDevice("KEYBOARD", DeviceType.INPUT);
        this.addDevice("MOUSE", DeviceType.INPUT);
        this.addDevice("SPEAKER", DeviceType.OUTPUT);
        this.addDevice("MICROPHONE", DeviceType.INPUT);

    }

    public synchronized void addDevice(String name, DeviceType type) throws EmptyCollectionException {
        if (this.devices.putIfAbsent(name, new Device(name, type)) != null) {
            logger.warning("Device " + name + " already exists");
        } else {
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info("Device " + name + " added");
            }
        }

    }

    public synchronized void removeDevice(String name) throws EmptyCollectionException, ElementNotFoundException {
        if (this.devices.remove(name) == null) {
            logger.warning("Device " + name + " does not exist");
        } else {
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info("Device " + name + " removed");
            }
        }
    }

    public synchronized void start() {
        if (!isRunning) {
            logger.info("Starting devices...");
            this.isRunning = true;
            for (Device device : this.devices.getValues()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        device.connect();
                    }
                }).start();
            }
        }
    }

    public synchronized void stop() {
        if (isRunning) {
            logger.info("Stopping device management...");
            this.isRunning = false;
            for (Device device : this.devices.getValues()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        device.disconnect();
                    }
                }).start();
            }
        }
    }

    public synchronized boolean isDeviceAvailable(String name) {
        Device device = null;
        try {
            device = this.devices.get(name);
        } catch (EmptyCollectionException e) {
            System.err.println(e.getMessage());
        }
        return device != null && device.isConnected() && !device.isBusy();
    }

   /* public synchronized boolean requestDevice(String name) throws EmptyCollectionException {
        if (!this.isRunning) {
            throw new IllegalStateException("Device management is not running");
        }

        Device device = this.devices.get(name);

        if (device == null) {
            throw new IllegalArgumentException("Device not found: " + name);
        }

        return device.requestUse();
    } */

    public synchronized void releaseDevice(String name) throws EmptyCollectionException {
        Device device = devices.get(name);
        if (device != null) {
            device.releaseUse();
        }
    }

    public synchronized void listDevices() throws EmptyCollectionException {
        logger.info("Current device states:");

        for (Device device : this.devices.getValues()) {
            logger.info(device.getName() + ": " + (device.isConnected() ? "connected" : "disconnected") + ", " + (device.isBusy() ? " (In use) " : " (Available)"));
        }
    }
}