import lib.HasTables.HashMap;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedList;

import java.util.logging.Logger;

public class Mem {

    private static final int MAX_MEMORY_SIZE = 1024;
    private static final int MIN_FREE_MEMORY = 64;

    private final LinkedList<MemoryBlock> memoryFree;
    private final HashMap<String, MemoryBlock> memoryBlocks;
    private int totalMemoryUsed;
    private boolean isRunning;

    private int peakMemoryUsage;
    private int allocationCount;
    private int failedAllocations;

    private static final Logger logger = Logger.getLogger(Mem.class.getName());

    public Mem() {

		this.memoryFree = new LinkedList<>();
        try {
        	this.memoryFree.add(new MemoryBlock("0", MAX_MEMORY_SIZE));
        } catch (NotElementComparableException e) {
			System.out.println(e.getMessage());
		}

        this.memoryBlocks = new HashMap<String, MemoryBlock>();
        this.totalMemoryUsed = 0;
        this.isRunning = false;
        this.peakMemoryUsage = 0;
        this.allocationCount = 0;
        this.failedAllocations = 0;
    }

    public synchronized void start() {
        if (isRunning) {
            logger.warning("MEMORY ALREADY STARTED");
            return;
        }

        if (!isRunning) {
            logger.info("Starting memory...");
            this.isRunning = true;
            printMemoryStatus();
        }
    }

    public synchronized void stop() {
       if (!isRunning) {
           logger.info("MEMORY ALREADY STOPPED");
           return;
       }

	   System.out.println("MEMORY STOPPING");

	   if (isRunning) {
		   memoryBlocks.clear();
		   this.isRunning = false;
		   logger.info("MEMORY STOPPED");
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

    public synchronized boolean allocateFF(String id, int requestedSize) {
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

        // Traversing the the free memory blocks
        for (MemoryBlock block : this.memoryFree) {

            // As soon as the requested size is smaller or equal than
            // a block size
            if (block.getSize() >= requestedSize) {

                // Get the start address from that memeory block
                String startID = block.getId();

                // Allocate a new Block
                MemoryBlock newBlock = new MemoryBlock(startID, requestedSize);

                this.memoryBlocks.put(id, newBlock);
                this.totalMemoryUsed += requestedSize;
                peakMemoryUsage = Math.max(peakMemoryUsage, totalMemoryUsed);

				// Set a new size
				block.setSize(block.getSize() - requestedSize);

				if (block.getSize() == 0) {
					try {
						this.memoryFree.remove(block);
					} catch (EmptyCollectionException | ElementNotFoundException e) {
						System.err.println(e.getMessage());
					}
				}

				this.allocationCount++;
				logger.info("Memory allocated for " + id + " with size " + requestedSize);
                printMemoryStatus();
                return true;
            }
        }

		this.failedAllocations++;
		logger.warning("Failed allocation for MemoryBlock " + id + " with size " + requestedSize);
		return false;
    }


    public synchronized void freeMemory(String id) throws EmptyCollectionException, ElementNotFoundException {
        if (!isRunning) {
            logger.warning("MEMORY IS NOT RUNNING");
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

// TODO: MEMORY allocating algorithm BestFit BF
