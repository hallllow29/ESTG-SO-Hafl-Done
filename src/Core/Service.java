package Core;
public class Service {

    private String name;
    private boolean isRunning;

    public Service(String name) {
        this.name = name;
        this.isRunning = false;
    }

    public void start() {
        if (this.isRunning) {
            System.out.println("SERVICE ALREADY RUNNING");
            return;
        }

        System.out.println("SERVICE STARTING...");

        this.isRunning = true;

        System.out.println("SERVICE STARTED");

    }
    public void stop() {
        if (!this.isRunning) {
            System.out.println("SERVICE ALREADY STOPPED");
            return;
        }

        System.out.println("SERVICE STOPPING...");

        this.isRunning = false;

        System.out.println("SERVICE STOPPED");
    }

    public boolean asksProcess() {
       if (this.isRunning) {
           System.out.println("SERVICE ASKS PROCESS");
           return true;
       }

        System.out.println("SERVICE DOES NOT ASKS PROCESS");
       return false;

    }

    public void process() {
        System.out.println("PROCESSING SERVICE");
    }

}
