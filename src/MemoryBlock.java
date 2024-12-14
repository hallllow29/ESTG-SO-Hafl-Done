public class MemoryBlock {
    
    private String id;
    private int size;
    private long allocationTime;
    
    public MemoryBlock(String id, int size) {
        this.id = id;
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
}
