package Core;

import Enums.DeviceType;
import lib.HasTables.HashMap;
import lib.exceptions.EmptyCollectionException;

public class Devices {

    private final HashMap<String, Device> devices; // Mapa de dispositivos
    private boolean isRunning; // Indica se os dispositivos estão ativos

    public Devices() {
        this.devices = new HashMap<>();
        this.isRunning = false;
        try {
            initDevices();
        } catch (EmptyCollectionException e) {
            System.err.println("INIT DEVICES FAILED: " + e.getMessage());
        }
    }

    /**
     * Inicializa os dispositivos com tipos e nomes padrão.
     */
    private synchronized void initDevices() throws EmptyCollectionException {
        devices.put("DISPLAY", new Device("DISPLAY", DeviceType.OUTPUT));
        devices.put("KEYBOARD", new Device("KEYBOARD", DeviceType.INPUT));
        devices.put("MOUSE", new Device("MOUSE", DeviceType.INPUT));
        devices.put("SPEAKER", new Device("SPEAKER", DeviceType.OUTPUT));
        devices.put("MICROPHONE", new Device("MICROPHONE", DeviceType.INPUT));
        System.out.println("DEVICES INITIALIZED SUCCESSFULLY");
    }

    /**
     * Inicia todos os dispositivos conectando-os.
     */
    public synchronized void start() {
        if (this.isRunning) {
            System.out.println("DEVICES ALREADY STARTED");
            return;
        }

        System.out.println("DEVICES STARTING...");
        for (Device device : devices.getValues()) {
            device.connect();
        }
        this.isRunning = true;
        System.out.println("DEVICES STARTED");
    }

    /**
     * Desliga todos os dispositivos, desconectando-os.
     */
    public synchronized void stop() {
        if (!this.isRunning) {
            System.out.println("DEVICES ALREADY STOPPED");
            return;
        }

        System.out.println("DEVICES STOPPING...");
        for (Device device : devices.getValues()) {
            device.disconnect();
        }
        this.isRunning = false;
        System.out.println("DEVICES STOPPED");
    }

    /**
     * Verifica se um dispositivo específico está disponível para uso.
     *
     * @param name Nome do dispositivo.
     * @return true se o dispositivo estiver conectado e não estiver em uso.
     */
    public synchronized boolean isDeviceAvailable(String name) {
        try {
            Device device = devices.get(name);
            return device != null && device.isConnected() && !device.isBusy();
        } catch (EmptyCollectionException e) {
            System.err.println("DEVICE NOT FOUND: " + e.getMessage());
            return false;
        }
    }

    /**
     * Solicita o uso de um dispositivo específico com timeout.
     *
     * @param name Nome do dispositivo.
     * @param timeout Tempo máximo de espera (em milissegundos).
     * @return true se o uso do dispositivo for concedido dentro do timeout.
     */
    public synchronized boolean requestDevice(String name, long timeout) {
        if (!this.isRunning) {
            System.out.println("DEVICES ARE NOT RUNNING");
            return false;
        }

        try {
            Device device = devices.get(name);
            if (device == null) {
                System.err.println("DEVICE DOES NOT EXIST: " + name);
                return false;
            }
            // Solicita o uso do dispositivo com timeout
            if (device.requestUse(timeout)) {
                System.out.println("[" + System.currentTimeMillis() + "] DEVICE ACQUIRED -> " + name);
                return true;
            } else {
                System.out.println("[" + System.currentTimeMillis() + "] TIMEOUT: DEVICE '" + name + "' IS BUSY OR NOT AVAILABLE");
                return false;
            }
        } catch (EmptyCollectionException e) {
            System.err.println("REQUEST DEVICE FAILED: " + e.getMessage());
            return false;
        }
    }

    /**
     * Libera o uso de um dispositivo específico.
     *
     * @param name Nome do dispositivo.
     */
    public synchronized void releaseDevice(String name) {
        try {
            Device device = devices.get(name);
            if (device != null) {
                device.releaseUse();
                System.out.println("[" + System.currentTimeMillis() + "] DEVICE RELEASED -> " + name);
            } else {
                System.err.println("DEVICE '" + name + "' NOT FOUND");
            }
        } catch (EmptyCollectionException e) {
            System.err.println("RELEASE DEVICE FAILED: " + e.getMessage());
        }
    }

    /**
     * Lista o status de todos os dispositivos.
     */
    public synchronized void listDevices() {
        System.out.println("\n=== DEVICES STATUS ===");
        for (Device device : devices.getValues()) {
            System.out.println(device.getName() + ": " +
                    (device.isConnected() ? "Connected" : "Disconnected") +
                    (device.isBusy() ? " (In Use)" : " (Available)"));
        }
        System.out.println("========================\n");
    }
}
