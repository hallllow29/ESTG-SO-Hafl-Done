package Core;
import Enums.DeviceType;
import lib.HasTables.HashMap;
import lib.exceptions.EmptyCollectionException;


public class Devices {

    private final HashMap<String, Device> devices;
    private boolean isRunning;

    public Devices()  {
        this.devices = new HashMap<String, Device>();
        this.isRunning = false;
        try {
            initDevices();
        }catch (EmptyCollectionException e) {
            System.err.println("INIT DEVICES FAILED!");
        }

    }

    private synchronized void initDevices() throws EmptyCollectionException {
        this.devices.put("DISPLAY", new Device( "DISPLAY", DeviceType.INPUT));
        this.devices.put("KEYBOARD", new Device( "KEYBOARD", DeviceType.INPUT));
        this.devices.put("MOUSE", new Device( "MOUSE", DeviceType.INPUT));
        this.devices.put("SPEAKER", new Device( "SPEAKER", DeviceType.OUTPUT));
        this.devices.put("MICROPHONE", new Device( "MICROPHONE", DeviceType.INPUT));
    }


    public synchronized void start() {
        if (this.isRunning) {
            System.out.println("DEVICES ALREADY STARTED");
            return;
        }

        System.out.println("DEVICES STARTING...");

        // Se iniciarmos aqui umas Threads vai haver uma racing condition
        // Mas o nosso objectivo é uma Thread para cada Processo/Core.Task
        // Tal como no Core.Task Manager da Windows.
        // Nos vamos adicionar logger, mas por agora vamos
        // deixar na Memory o qual indica se foi usado
        // FF para o Core.Task ser alocado e assim o Core.CPU o executar.
        for (Device device : this.devices.getValues()) {
            device.connect();
        }

        this.isRunning = true;
        System.out.println("DEVICES STARTED");

    }

    public synchronized void stop() {
        if (!this.isRunning) {
            System.out.println("DEVICES ALREADY STOPPED");
            return;
        }

        System.out.println("DEVICES STOPPING...");

        for (Device device : this.devices.getValues()) {
            device.disconnect();
        }

        this.isRunning = false;
        System.out.println("DEVICES STOPPED");

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

    public synchronized boolean requestDevice(String name) throws EmptyCollectionException {
        if (!this.isRunning) {
            System.out.println("DEVICES ARE NOT RUNNING");
            return false;
        }

        Device device = this.devices.get(name);

        if (device == null) {
            System.out.println("DEVICE CANNOT BE NULL");
            return false;
        }

        return device.requestUse();
    }

    public synchronized void listDevices() {
        System.out.println("\n=== DEVICES STATUS ===");
        for (Device device : devices.getValues()) {
            System.out.println(device.getName() + ": " +
                (device.isConnected() ? "Connected" : "Disconnected") +
                (device.isBusy() ? " (In Use)" : " (Available)"));
        }
        System.out.println("====================\n");
    }

    public synchronized void releaseDevice(String name) throws EmptyCollectionException {
        Device device = devices.get(name);
        if (device != null) {
            device.releaseUse();
        }
    }

}
