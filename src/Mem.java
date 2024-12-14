import lib.HasTables.HashMap;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;

import java.util.logging.Logger;

public class Mem {

    private static final int MAX_MEMORY_SIZE = 1024;
    private static final int MIN_FREE_MEMORY = 64;

    private final HashMap<String, MemoryBlock> memoryBlocks;
    private int totalMemoryUsed;
    private boolean isRunning;

    private int peakMemoryUsage;
    private int allocationCount;
    private int failedAllocations;

    private static final Logger logger = Logger.getLogger(Mem.class.getName());

    public Mem() {
        this.memoryBlocks = new HashMap<String, MemoryBlock>();
        this.totalMemoryUsed = 0;
        this.isRunning = false;
        this.peakMemoryUsage = 0;
        this.allocationCount = 0;
        this.failedAllocations = 0;
    }

    public synchronized void start() {
        if (!isRunning) {
            logger.info("Starting memory...");
            this.isRunning = true;
            printMemoryStatus();
        }
    }

    public synchronized void stop() {
        if (isRunning) {
            logger.info("Stopping memory...");
            memoryBlocks.clear();
            this.isRunning = false;
            printMemoryStatus();
        }
    }

    public synchronized boolean allocateMemory(String id, int requestedSize) {
        if (!isRunning) {
            logger.warning("Memory is not running...");
            return false;
        }

        if (requestedSize <= 0) {
            logger.warning("Invalid memory size...");
            failedAllocations++;
            return false;
        }

        if (this.totalMemoryUsed + requestedSize > MAX_MEMORY_SIZE) {
            logger.warning("Not enough memory...");
            failedAllocations++;
            return false;
        }

        MemoryBlock block = new MemoryBlock(id, requestedSize);
        memoryBlocks.put(id, block);
        totalMemoryUsed += requestedSize;
        allocationCount++;

        peakMemoryUsage = Math.max(peakMemoryUsage, totalMemoryUsed);

        logger.info("Memory allocated for " + id + " with size " + requestedSize);
        printMemoryStatus();
        return true;
    }

    public synchronized void freeMemory(String id) throws EmptyCollectionException, ElementNotFoundException {
        if (!isRunning) {
            logger.warning("Memory is not running...");
            return;
        }

        MemoryBlock block = memoryBlocks.get(id);

        if (block == null) {
            logger.warning("Memory block not found...");
            return;
        }

        memoryBlocks.remove(id);
        totalMemoryUsed -= block.getSize();

        logger.info("Memory freed " + block.getSize() + " for " + id);
        printMemoryStatus();
    }

    public synchronized boolean hasAvailableMemory(int requiredSize) {
        return isRunning && (totalMemoryUsed + requiredSize <= MAX_MEMORY_SIZE);
    }

    public synchronized boolean hasEnoughFreeMemory() {
        return (MAX_MEMORY_SIZE - totalMemoryUsed) >= MIN_FREE_MEMORY;
    }

    private void printMemoryStatus() {
        logger.info("\n==================== MEMORY STATUS ====================");
        logger.info("Total Memory: " + MAX_MEMORY_SIZE + "MB");
        logger.info("Total memory used: " + totalMemoryUsed + "MB");
        logger.info("Free memory: " + (MAX_MEMORY_SIZE - totalMemoryUsed) + "MB");
        logger.info("Allocated Blocks: " + memoryBlocks.size());
        logger.info("======================================================\n");

    }

    private void printFinalStatus() {
        logger.info("\n==================== FINAL MEMORY STATISTICS =====================");
        logger.info("Peak Memory Usage: " + peakMemoryUsage + "MB");
        logger.info("Total Allocations: " + allocationCount);
        logger.info("Failed Allocations: " + failedAllocations);
        logger.info("=================================================================\n");
    }

    public synchronized int getTotalMemoryUsed() {
        return this.totalMemoryUsed;
    }

    public synchronized int getFreeMemory() {
        return MAX_MEMORY_SIZE - this.totalMemoryUsed;
    }

    public synchronized int getAllocationCount() {
        return this.allocationCount;
    }

    public synchronized int getFailedAllocations() {
        return this.failedAllocations;
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

}
