package Core;
public class MemoryBlock implements Comparable<MemoryBlock> {
    
    private String id;
    private int startAddress;
    private int size;

    public MemoryBlock(String id, int startAddress, int size) {
        this.id = id;
        this.startAddress = startAddress;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public int getStartAddress() {
        return this.startAddress;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int compareTo(MemoryBlock other) {
        return Integer.compare(this.startAddress, other.startAddress);
    }


}
