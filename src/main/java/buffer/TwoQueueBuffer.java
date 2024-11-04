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
        kin = capacity > 3 ? capacity / 4 : 1;
        kout = capacity > 2 ? capacity / 2 : 1;
    }

    @Override
    protected Slot fix(char c) throws IllegalStateException {
        sCount++;

        Slot slot = lookUp(c);

        if (slot == null) {
            fsCount++;

            if (fixedSlots == slots.size()) {
                throw new IllegalStateException("Buffer overflow. Too many slots fixed.");
            }

            if (size() == slots.size()) {
                Slot victimSlot = victim();
                victimSlot.remove();
            }

            (slot = slots.get(size())).insert(c);
            a1in.add(slot);
        } else {
            if (am.contains(slot)) {
                am.remove(slot);
                am.add(slot);
            }
        }

        slot.fix();

        return slot;
    }

    protected Slot victim() {
        Slot victimSlot;

        if (a1in.size() > kin) {
            victimSlot = a1in.poll();
            a1out.add(victimSlot.c);

            if (a1out.size() > kout) {
                a1out.poll();
            }
        } else {
            victimSlot = am.poll();
        }

        return victimSlot;
    }
}
