package buffer;

import java.util.ArrayDeque;

public class SimpleTwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1 = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private final int kin;

    public SimpleTwoQueueBuffer(int capacity) {
        super(capacity);
        this.kin = capacity > 3 ? capacity / 4 : 1;
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
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

//            if (a1.size() > kin) {
//                Slot swappedOutSlot = a1.poll();
//                swappedOutSlot.remove();
//            }

            a1.add(slot);
        } else {
            if (a1.contains(slot)) {
                a1.remove(slot);
                am.add(slot);
            } else {
                am.remove(slot);
                am.add(slot);
            }

        }

        slot.fix();

        return slot;
    }

    protected Slot victim() {
        Slot victimSlot;

        if (a1.size() > kin) {
            victimSlot = a1.poll();
        } else {
            victimSlot = am.poll();
        }

        return victimSlot;
    }
}
