public class MemoryBlock implements Comparable<MemoryBlock> {
    
    private String id;
    private int startAddress;
    private int size;
    private long allocationTime;
    
    public MemoryBlock(String id, int startAddress, int size) {
        this.id = id;
        this.startAddress = startAddress;
        this.size = size;
        this.allocationTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getAllocationTime() {
        return allocationTime;
    }

    public void setAllocationTime(long allocationTime) {
        this.allocationTime = allocationTime;
    }

    public int getStartAddress() {
        return this.startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int compareTo(MemoryBlock other) {
        return Integer.compare(this.startAddress, other.startAddress);
    }


}
