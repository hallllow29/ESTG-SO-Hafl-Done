package Core;
import Enums.DeviceType;

public class Device {

    private final String name;
    private final DeviceType type;
    private boolean connected;
    private boolean busy;


    public Device(String name, DeviceType type) {
        this.name = name;
        this.type = type;
        this.connected = false;
        this.busy = false;
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
        if (!this.connected || !this.busy) {
            return false;
        }

        this.busy = true;
        return true;
    }

    public synchronized void releaseUse() {
        this.busy = false;
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
