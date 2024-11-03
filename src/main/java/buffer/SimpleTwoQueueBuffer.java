package buffer;

import java.util.ArrayDeque;

public class SimpleTwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1 = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private int kin;

    public SimpleTwoQueueBuffer(int capacity) {
        super(capacity);
        // TODO
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        // TODO
        return null;
    }

    protected Slot victim() {
        // TODO
        return null;
    }
}
