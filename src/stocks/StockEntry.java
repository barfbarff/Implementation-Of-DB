package stocks;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StockEntry {
    private final long id;
    private final String name;
    private final long ts;
    private final double value;

    public StockEntry(int id, String name, int timestamp, double market_value) {
        this.id = id;
        this.name = name;
        this.ts = timestamp;
        this.value = market_value;
    }

    public StockEntry(ByteBuffer bb) {
        int id = (int) bb.getLong();

        byte[] nameByteArray = new byte[bb.getShort()];
        bb.get(nameByteArray);
        String name = new String(nameByteArray, StandardCharsets.UTF_8);

        int timestamp = (int) bb.getLong();

        double marketValue = bb.getDouble();

        this(id, name, timestamp, marketValue);

    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public long getTimeStamp() {
        return this.ts;
    }

    public double getMarketValue() {
        return this.value;
    }

    public int getSerializedLength() {
        return 3 * 8 + 2 + name.getBytes().length;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + ts + " " + value;
    }

    public ByteBuffer getBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(26 + name.length());

        byteBuffer.putLong(id);

        byteBuffer.putShort((short) name.length());

        byte[] nameByteArray = name.getBytes(StandardCharsets.UTF_8);
        byteBuffer.put(nameByteArray);

        byteBuffer.putLong(ts);

        byteBuffer.putDouble(value);

        byteBuffer.flip();

        return byteBuffer;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StockEntry) {
            StockEntry entry = (StockEntry) obj;
            return id == entry.id && name.equals(entry.name) && ts == entry.ts && value == entry.value;
        }
        return false;
    }
}
