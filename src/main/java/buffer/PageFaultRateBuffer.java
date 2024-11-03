package buffer;

public abstract class PageFaultRateBuffer extends Buffer {
    private int fsCount = 0;
    private int sCount = 0;

    public PageFaultRateBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        // TODO
        return 0.0;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        // TODO
        return null;
    }
}
