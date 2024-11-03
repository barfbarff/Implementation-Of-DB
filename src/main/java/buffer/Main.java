package buffer;

public class Main {

    public static void main(String[] args) {

        SimpleTwoQueueBuffer buf1 = new SimpleTwoQueueBuffer(3);
        TwoQueueBuffer buf2 = new TwoQueueBuffer(3);
        PageFaultRateLRUBuffer buf3 = new PageFaultRateLRUBuffer(3);

        String pages = "ABAABAACDBACED";

        for (int i = 0; i < pages.length(); i++) {
            buf1.get(pages.charAt(i), true);
            buf2.get(pages.charAt(i), true);
            buf3.get(pages.charAt(i), true);
        }

        System.out.println("buffers.SimpleTwoQueueBuffer: " + buf1.getFSR());
        System.out.println("buffers.TwoQueueBuffer: " + buf2.getFSR());
        System.out.println("buffers.LRUBuffer: " + buf3.getFSR());
    }
}
