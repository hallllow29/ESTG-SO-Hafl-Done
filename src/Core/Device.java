package Core;
import Enums.DeviceType;

import java.util.concurrent.Semaphore;

public class Device {

    private final String name;
    private final DeviceType type;
    private boolean connected;
    private boolean busy;
    private final Semaphore semaphore;


    public Device(String name, DeviceType type) {
        this.name = name;
        this.type = type;
        this.connected = false;
        this.busy = false;
        this.semaphore = new Semaphore(1);
    }

    public synchronized void connect() {
        this.connected = true;
        System.out.println("Core.Device connected -> " + this.name);
    }

    public synchronized void disconnect() {
        this.connected = false;
        this.busy = false;
        System.out.println("Core.Device disconnected -> " + this.name);
    }

    public synchronized boolean requestUse() {
        try {
            semaphore.acquire();
            if (!this.connected || this.busy) {
                semaphore.release();
                return false;
            }
            this.busy = true;
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public synchronized void releaseUse() {
        this.busy = false;
        semaphore.release();
    }

    public String getName() {
        return this.name;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isBusy() {
        return this.busy;
    }

    public DeviceType getType() {
        return this.type;
    }
}
