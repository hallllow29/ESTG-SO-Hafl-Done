package Core;

import Enums.DeviceType;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Device {

    private final String name; // Nome do dispositivo
    private final DeviceType type; // Tipo do dispositivo (ex.: INPUT, OUTPUT, NETWORK)
    private boolean connected; // Indica se o dispositivo está conectado
    private boolean busy; // Indica se o dispositivo está em uso
    private final Semaphore semaphore; // Semáforo para controle de acesso ao recurso

    public Device(String name, DeviceType type) {
        this.name = name;
        this.type = type;
        this.connected = false;
        this.busy = false;
        this.semaphore = new Semaphore(1); // Inicializa semáforo com 1 recurso (exclusão mútua)
    }

    /**
     * Conecta o dispositivo, se não estiver conectado.
     */
    public synchronized void connect() {
        if (this.connected) {
            System.out.println("[" + System.currentTimeMillis() + "] Device already connected -> " + this.name);
            return;
        }
        this.connected = true;
        System.out.println("[" + System.currentTimeMillis() + "] Device connected -> " + this.name);
    }

    /**
     * Desconecta o dispositivo, se estiver conectado.
     */
    public synchronized void disconnect() {
        if (!this.connected) {
            System.out.println("[" + System.currentTimeMillis() + "] Device already disconnected -> " + this.name);
            return;
        }
        this.connected = false;
        this.busy = false;
        System.out.println("[" + System.currentTimeMillis() + "] Device disconnected -> " + this.name);
    }

    /**
     * Solicita o uso do dispositivo. Retorna true se a solicitação for bem-sucedida.
     */
    public boolean requestUse(long timeout) {
        try {
            if (semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS)) {
                synchronized (this) {
                    if (!this.connected || this.busy) {
                        semaphore.release();
                        System.out.println("[" + System.currentTimeMillis() + "] Device not available -> " + this.name);
                        return false;
                    }
                    this.busy = true;
                    System.out.println("[" + System.currentTimeMillis() + "] Device acquired -> " + this.name);
                    return true;
                }
            } else {
                System.out.println("[" + System.currentTimeMillis() + "] TIMEOUT: DEVICE '" + this.name + "' IS NOT AVAILABLE.");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Libera o uso do dispositivo.
     */
    public synchronized void releaseUse() {
        if (!this.busy) {
            System.out.println("[" + System.currentTimeMillis() + "] Device not in use -> " + this.name);
            return;
        }
        this.busy = false;
        semaphore.release();
        System.out.println("[" + System.currentTimeMillis() + "] Device released -> " + this.name);
    }

    /**
     * Retorna o nome do dispositivo.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retorna se o dispositivo está conectado.
     */
    public synchronized boolean isConnected() {
        return this.connected;
    }

    /**
     * Retorna se o dispositivo está ocupado.
     */
    public synchronized boolean isBusy() {
        return this.busy;
    }

    /**
     * Retorna o tipo do dispositivo.
     */
    public DeviceType getType() {
        return this.type;
    }
}
