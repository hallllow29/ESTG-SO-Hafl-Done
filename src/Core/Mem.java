package Core;

import lib.HasTables.HashMap;
import lib.LinearNode;
import lib.exceptions.ElementNotFoundException;
import lib.exceptions.EmptyCollectionException;
import lib.exceptions.NotElementComparableException;
import lib.lists.LinkedOrderedList;

import java.util.logging.Logger;

public class Mem {

    private static final int MAX_MEMORY_SIZE = 1024; // Tamanho máximo da memória
    private final LinkedOrderedList<MemoryBlock> memoryFree; // Blocos livres
    private final HashMap<String, MemoryBlock> memoryBlocks; // Blocos alocados
    private int totalMemoryUsed; // Total de memória utilizada
    private static final Logger logger = Logger.getLogger(Mem.class.getName());
    private boolean isRunning; // Estado da memória (ativa/inativa)

    public Mem() {
        this.memoryFree = new LinkedOrderedList<>();
        this.memoryBlocks = new HashMap<>();
        this.totalMemoryUsed = 0;
        this.isRunning = false;

        try {
            this.memoryFree.add(new MemoryBlock("0000", 0, MAX_MEMORY_SIZE));
        } catch (NotElementComparableException e) {
            System.err.println("ERROR INITIALIZING MEMORY: " + e.getMessage());
        }
    }

    /**
     * Inicia a memória.
     */
    public synchronized void start() {
        if (isRunning) {
            logger.warning("MEMORY ALREADY STARTED");
            return;
        }
        logger.info("MEMORY STARTING...");
        isRunning = true;
        printMemoryStatus();
    }

    /**
     * Para a memória e limpa os blocos alocados.
     */
    public synchronized void stop() {
        if (!isRunning) {
            logger.warning("MEMORY ALREADY STOPPED");
            return;
        }
        logger.info("MEMORY STOPPING...");
        memoryBlocks.clear();
        totalMemoryUsed = 0;
        isRunning = false;
        printMemoryStatus();
    }

    /**
     * Aloca memória usando o algoritmo First-Fit.
     *
     * @param task Tarefa que precisa de memória.
     * @return true se a memória for alocada com sucesso.
     */
    public boolean allocateFF(Task task) {
        if (task.getMemorySize() <= 0) {
            System.out.println("INVALID MEMORY SIZE FOR TASK: " + task.getName());
            return false;
        }

        long startTime = System.currentTimeMillis();
        long timeout = 2000;

        synchronized (memoryFree) {
            while (true) {
                for (MemoryBlock block : memoryFree) {
                    if (block.getSize() >= task.getMemorySize()) {
                        int startAddress = block.getStartAddress();
                        block.setStartAddress(startAddress + task.getMemorySize());
                        block.setSize(block.getSize() - task.getMemorySize());

                        MemoryBlock newBlock = new MemoryBlock(task.getName(), startAddress, task.getMemorySize());
                        memoryBlocks.put(newBlock.getId(), newBlock);
                        totalMemoryUsed += task.getMemorySize();
                        System.out.println("MEMORY ALLOCATED FOR TASK: " + task.getName() + " AT ADDRESS: " + startAddress);
                        printMemoryStatus();
                        return true;
                    }
                }

                if (System.currentTimeMillis() - startTime >= timeout) {
                    System.out.println("TIMEOUT: MEMORY NOT AVAILABLE FOR TASK: " + task.getName());
                    return false;
                }

                try {
                    memoryFree.wait(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
    }

    /**
     * Libera memória associada a um bloco.
     *
     * @param id ID do bloco de memória a ser liberado.
     */
    public void freeMemory(String id) throws EmptyCollectionException, ElementNotFoundException, NotElementComparableException {
       MemoryBlock blockToFree;

       synchronized (memoryBlocks) {
              blockToFree = memoryBlocks.get(id);
              if (blockToFree == null) {
                System.out.println("MEMORY BLOCK NOT FOUND...");
                return;
              }
              memoryBlocks.remove(id);
       }

       synchronized (memoryFree) {
              memoryFree.add(blockToFree);
              totalMemoryUsed -= blockToFree.getSize();
              System.out.println("MEMORY BLOCK " + id + " FREED");

              mergeMemoryFreeBlocks();
       }

    }

    /**
     * Verifica se há memória suficiente disponível.
     *
     * @param requiredSize Tamanho requerido.
     * @return true se houver memória suficiente.
     */
    public synchronized boolean hasAvailableMemory(int requiredSize) {
        return isRunning && (totalMemoryUsed + requiredSize <= MAX_MEMORY_SIZE);
    }

    /**
     * Imprime o status atual da memória.
     */
    public synchronized void printMemoryStatus() {
        logger.info("\n==================== MEMORY STATUS ====================");
        logger.info("Total Memory: " + MAX_MEMORY_SIZE + "MB");
        logger.info("Used Memory: " + totalMemoryUsed + "MB");
        logger.info("Free Memory: " + (MAX_MEMORY_SIZE - totalMemoryUsed) + "MB");
        logger.info("Allocated Blocks: " + memoryBlocks.size());
        logger.info("======================================================\n");
    }

    /**
     * Valida se o sistema pode alocar memória.
     */
    private boolean validateMemoryState(Task task) {
        if (!isRunning) {
            logger.warning("MEMORY IS NOT RUNNING");
            return false;
        }
        if (task.getMemorySize() <= 0) {
            logger.warning("INVALID MEMORY SIZE FOR TASK");
            return false;
        }
        if (!hasAvailableMemory(task.getMemorySize())) {
            logger.warning("INSUFFICIENT MEMORY FOR TASK " + task.getName());
            return false;
        }
        return true;
    }

    /**
     * Aloca efetivamente o bloco de memória.
     */
    private void allocateMemoryBlock(Task task, MemoryBlock block) {
        try {
            int startAddress = block.getStartAddress();
            MemoryBlock newBlock = new MemoryBlock(task.getName(), startAddress, task.getMemorySize());
            memoryBlocks.put(newBlock.getId(), newBlock);
            totalMemoryUsed += newBlock.getSize();

            block.setSize(block.getSize() - task.getMemorySize());
            if (block.getSize() == 0) memoryFree.remove(block);

            logger.info("Memory allocated for task " + task.getName() + " at address " + startAddress);
            printMemoryStatus();
        } catch (Exception e) {
            logger.severe("Error allocating memory block: " + e.getMessage());
        }
    }

    /**
     * Mescla blocos livres adjacentes para otimizar o uso da memória.
     */
    private void mergeMemoryFreeBlocks() throws NotElementComparableException {
        synchronized (memoryFree) {
            if (memoryFree.isEmpty() || memoryFree.size() == 1) {
                return;

            }
        }

        LinearNode<MemoryBlock> current = memoryFree.getFront();
        while (current != null && current.getNext() != null) {
            MemoryBlock currentBlock = current.getElement();
            MemoryBlock nextBlock = current.getNext().getElement();

            if (currentBlock.getStartAddress() + currentBlock.getSize() == nextBlock.getStartAddress()) {
                currentBlock.setSize(currentBlock.getSize() + nextBlock.getSize());
                try {
                    memoryFree.remove(nextBlock);
                } catch (EmptyCollectionException | ElementNotFoundException e) {
                    System.err.println("ERROR MERGING BLOCKS: " + e.getMessage());
                }
            } else {
                current = current.getNext();
            }
        }
    }

    public int getTotalMemory() {
        return MAX_MEMORY_SIZE;
    }
}
