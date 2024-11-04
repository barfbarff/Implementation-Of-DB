package buffer;

public abstract class PageFaultRateBuffer extends Buffer {
    protected int fsCount = 0;
    protected int sCount = 0;

    public PageFaultRateBuffer(int capacity) {
        super(capacity);
    }

    public double getFSR() {
        return (double) fsCount / sCount;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        sCount++;

        Slot slot = lookUp(c);

        if (slot == null) {
            fsCount++;

            if (fixedSlots == slots.size())
                throw new IllegalStateException("Buffer overflow. Too many slots fixed.");

            // Make space for one new object
            if (size() == slots.size()) {
                Slot vic = victim();
                vic.remove();
            }

            // insert the object
            (slot = slots.get(size())).insert(c);
            slot.fix();
        } else {
            slot.fix();
        }

        return slot;
    }
}
