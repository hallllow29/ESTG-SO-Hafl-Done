package Core;

public class MemoryBlock implements Comparable<MemoryBlock> {

    private final String id;       // Identificador único do bloco
    private int startAddress;      // Endereço inicial do bloco
    private int size;              // Tamanho do bloco

    /**
     * Construtor do bloco de memória.
     *
     * @param id            Identificador único do bloco
     * @param startAddress  Endereço inicial do bloco
     * @param size          Tamanho do bloco
     */
    public MemoryBlock(String id, int startAddress, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero.");
        }
        this.id = id;
        this.startAddress = startAddress;
        this.size = size;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getStartAddress() {
        return this.startAddress;
    }

    public int getSize() {
        return this.size;
    }

    // Setter para o tamanho
    public void setSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        this.size = size;
    }

    // Setter para o endereço (caso precise atualizar durante merge)
    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    /**
     * Compara dois blocos de memória com base no endereço inicial.
     *
     * @param other Outro bloco de memória
     * @return Valor negativo, zero ou positivo, dependendo da ordem
     */
    @Override
    public int compareTo(MemoryBlock other) {
        return Integer.compare(this.startAddress, other.startAddress);
    }

    /**
     * Representação em String do bloco de memória.
     *
     * @return Informações do bloco de memória
     */
    @Override
    public String toString() {
        return String.format("MemoryBlock{id='%s', startAddress=%d, size=%d}", id, startAddress, size);
    }

    /**
     * Verifica a igualdade entre dois blocos de memória.
     *
     * @param obj Objeto a ser comparado
     * @return true se os blocos forem iguais
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MemoryBlock that = (MemoryBlock) obj;
        return startAddress == that.startAddress && size == that.size && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + startAddress;
        result = 31 * result + size;
        return result;
    }
}
