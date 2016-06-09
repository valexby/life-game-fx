package life.util;

public class SaveCarrier implements Comparable<SaveCarrier> {
    public int coefficient;
    public String name;

    public int compareTo(SaveCarrier saveCarrier) {
        if (this.coefficient == saveCarrier.coefficient) return 0;
        if (this.coefficient > saveCarrier.coefficient) return 1;
        return -1;
    }
}
