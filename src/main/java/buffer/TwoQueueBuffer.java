package buffer;

import java.util.ArrayDeque;

public class TwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1in = new ArrayDeque<>();
    private final ArrayDeque<Character> a1out = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private final int kin;
    private final int kout;

    public TwoQueueBuffer(int capacity) {
        super(capacity);
        // TODO
        kin = kout = 0;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        // TODO
        return null;
    }

    protected Slot victim() {
        // TODO
        return null;
    }
}
