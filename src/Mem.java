import lib.HasTables.HashMap;
import lib.LinearNode;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedOrderedList;

import java.util.logging.Logger;

public class Mem {

    private static final int MAX_MEMORY_SIZE = 1024;
    private static final int MIN_FREE_MEMORY = 64;

    private final LinkedOrderedList<MemoryBlock> memoryFree;
    private final HashMap<String, MemoryBlock> memoryBlocks;
    private int totalMemoryUsed;
    private boolean isRunning;

    private int peakMemoryUsage;
    private int allocationCount;
    private int failedAllocations;

    private static final Logger logger = Logger.getLogger(Mem.class.getName());

    public Mem() {

		this.memoryFree = new LinkedOrderedList<MemoryBlock>();
        try {
        	this.memoryFree.add(new MemoryBlock("0000", 0, MAX_MEMORY_SIZE));
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

    public synchronized boolean allocateMemory(String id, int startAddress, int requestedSize) {
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

        MemoryBlock block = new MemoryBlock(id, startAddress, requestedSize);
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

            // looking for the first free space that fits that requested size
            if (block.getSize() >= requestedSize) {

                // Get the start address from that memeory block
                int startAddress = block.getStartAddress();

                // Allocate a new Block
                MemoryBlock newBlock = new MemoryBlock(id, startAddress, requestedSize);

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

    public synchronized boolean allocateBF(String id, int requestedSize) {
        if (!isRunning) {
            logger.warning("Memory is not running...");
        }

        if (requestedSize <= 0) {
            logger.warning("Invalid memory size...");
            this.failedAllocations++;
            return false;
        }

        if (this.totalMemoryUsed + requestedSize > MAX_MEMORY_SIZE) {
            logger.warning("Not enough memory...");
            this.failedAllocations++;
            return false;
        }

        MemoryBlock bestBlock = null;

        for (MemoryBlock block : this.memoryFree) {
            if (block.getSize() >= requestedSize) {

                if (bestBlock == null || block.getSize() < bestBlock.getSize()) {
                    bestBlock = block;
                }
            }
        }

        if (bestBlock != null) {
            int startAddress = bestBlock.getStartAddress();

            MemoryBlock newBlock = new MemoryBlock(id, startAddress, requestedSize);
            this.memoryBlocks.put(id, newBlock);
            this.totalMemoryUsed += requestedSize;
            this.peakMemoryUsage = Math.max(peakMemoryUsage, totalMemoryUsed);

            bestBlock.setSize(bestBlock.getSize() - requestedSize);

            if (bestBlock.getSize() == 0) {
                try {
                    this.memoryFree.remove(bestBlock);
                } catch (EmptyCollectionException | ElementNotFoundException e) {
                    System.err.println(e.getMessage());
                }
            }

            logger.info("Memory allocated for " + id + " with size " + requestedSize);
            this.allocationCount++;
            return true;
        }

        this.failedAllocations++;
		logger.warning("Failed allocation for MemoryBlock " + id + " with size " + requestedSize);
		return false;
    }

    public synchronized boolean allocateWF(String id, int requestedSize) {

        if (!isRunning) {
            logger.warning("Memory is not running...");
        }

        if (requestedSize <= 0) {
            logger.warning("Invalid memory size...");
            this.failedAllocations++;
            return false;
        }

        if (this.totalMemoryUsed + requestedSize > MAX_MEMORY_SIZE) {
            logger.warning("Not enough memory...");
            this.failedAllocations++;
            return false;
        }

        MemoryBlock worstBlock = null;

        for (MemoryBlock block : this.memoryFree) {
            if (block.getSize() >= requestedSize) {

                if (worstBlock == null || block.getSize() < worstBlock.getSize()) {
                    worstBlock = block;
                }
            }
        }

        if (worstBlock != null) {
            int startAddress = worstBlock.getStartAddress();

            MemoryBlock newBlock = new MemoryBlock(id, startAddress, requestedSize);
            this.memoryBlocks.put(id, newBlock);
            this.totalMemoryUsed += requestedSize;
            this.peakMemoryUsage = Math.max(peakMemoryUsage, totalMemoryUsed);

            worstBlock.setSize(worstBlock.getSize() - requestedSize);

            if (worstBlock.getSize() == 0) {
                try {
                    this.memoryFree.remove(worstBlock);
                } catch (EmptyCollectionException | ElementNotFoundException e) {
                    System.err.println(e.getMessage());
                }
            }

            logger.info("Memory allocated for " + id + " with size " + requestedSize);
            this.allocationCount++;
            return true;
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

        MemoryBlock blockToFree = memoryBlocks.get(id);

        if (blockToFree == null) {
            logger.warning("Memory block not found...");
            return;
        }

        memoryBlocks.remove(id);
        totalMemoryUsed -= blockToFree.getSize();

        // Updates here the memoryFree LinkedList
        try {
            this.memoryFree.add(new MemoryBlock(blockToFree.getId(), blockToFree.getStartAddress(), blockToFree.getSize()));
        } catch (NotElementComparableException e) {
            System.err.println(e.getMessage());
        }

        logger.info("Memory freed " + blockToFree.getSize() + " for " + id);
        printMemoryStatus();
    }

    private void mergeMemoryFreeBlocks() throws EmptyCollectionException, ElementNotFoundException {

        // There is no need to merge one block with nothing.
        if (this.memoryFree.isEmpty() || this.memoryFree.size() == 1) {
            return;
        }

        // Get the first block of the memoryFree List
        LinearNode<MemoryBlock> currentNode = this.memoryFree.getFront();

        while (currentNode != null && currentNode.getNext() != null) {
            MemoryBlock currentBlock = currentNode.getElement();
            MemoryBlock nextBlock = currentNode.getNext().getElement();

            if (currentBlock.getStartAddress() + currentBlock.getSize() == nextBlock.getStartAddress()) {

                // Merging...
                currentBlock.setSize(currentBlock.getSize() + nextBlock.getSize());

                // Remove the next block, since next block
                // was merged by current.
                this.memoryFree.remove(nextBlock);
            } else {
                currentNode = currentNode.getNext();
            }
        }
    }

    public synchronized boolean hasAvailableMemory(int requiredSize) {
        return isRunning && (totalMemoryUsed + requiredSize <= MAX_MEMORY_SIZE);
    }

    public synchronized boolean hasEnoughFreeMemory() {
        return (MAX_MEMORY_SIZE - totalMemoryUsed) >= MIN_FREE_MEMORY;
    }

    private synchronized void printMemoryStatus() {
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
