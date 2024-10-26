package stocks;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class StockEntry {
    private final long id;
    private final String name;
    private final long ts;
    private final double value;

    public StockEntry(long id, String name, long timestamp, double market_value) {
        this.id = id;
        this.name = name;
        this.ts = timestamp;
        this.value = market_value;
    }

    public StockEntry(ByteBuffer bb) {
        // TODO
        this.id= bb.getLong();

        int nameLen = bb.getShort();
        byte[] byteName = new byte[nameLen];
        bb.get(byteName);
        this.name = new String(byteName, StandardCharsets.UTF_8);

        this.ts = bb.getLong();
        this.value = bb.getDouble();
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
        // TODO
        ByteBuffer buffer = ByteBuffer.allocate(8 + 2 + name.length() + 8 + 8);

        buffer.putLong(id);

        byte[] byteName = name.getBytes(StandardCharsets.UTF_8);
        buffer.putShort((short) byteName.length);
        buffer.put(byteName);

        buffer.putLong(ts);
        buffer.putDouble(value);
        buffer.flip();
        return buffer;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StockEntry) {
            StockEntry entry = (StockEntry) obj;
            return id == entry.id && name.equals(entry.name) && ts == entry.ts && value == entry.value;
        }
        return false;
    }
}
