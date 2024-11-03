package buffer;

public class PageFaultRateLRUBuffer extends LRUBuffer {
    private int fsCount = 0;
    private int sCount = 0;

    public PageFaultRateLRUBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        // TODO
        return 0.0;
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        // TODO
        return null;
    }
}
