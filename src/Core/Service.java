package Core;

public class Service {

    private final String name; // Nome do serviço
    private boolean isRunning; // Estado do serviço (ativo ou inativo)

    /**
     * Construtor do Serviço.
     *
     * @param name Nome do serviço.
     */
    public Service(String name) {
        this.name = name;
        this.isRunning = false;
    }

    /**
     * Inicia o serviço.
     */
    public synchronized void start() {
        if (this.isRunning) {
            System.out.println("SERVICE '" + name + "' IS ALREADY RUNNING");
            return;
        }

        System.out.println("SERVICE '" + name + "' STARTING...");
        this.isRunning = true;
        System.out.println("SERVICE '" + name + "' STARTED");
    }

    /**
     * Para o serviço.
     */
    public synchronized void stop() {
        if (!this.isRunning) {
            System.out.println("SERVICE '" + name + "' IS ALREADY STOPPED");
            return;
        }

        System.out.println("SERVICE '" + name + "' STOPPING...");
        this.isRunning = false;
        System.out.println("SERVICE '" + name + "' STOPPED");
    }

    /**
     * Verifica se o serviço está pedindo para processar.
     *
     * @return true se o serviço estiver ativo, caso contrário false.
     */
    public synchronized boolean asksProcess() {
        return this.isRunning;
    }

    /**
     * Processa o serviço.
     */
    public synchronized void process() {
        if (this.isRunning) {
            System.out.println("SERVICE '" + name + "' IS PROCESSING...");
        } else {
            System.out.println("SERVICE '" + name + "' IS NOT RUNNING. PROCESS CANNOT BE EXECUTED.");
        }
    }

    /**
     * Retorna o nome do serviço.
     *
     * @return Nome do serviço.
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna se o serviço está ativo.
     *
     * @return true se o serviço estiver em execução.
     */
    public synchronized boolean isRunning() {
        return isRunning;
    }
}
