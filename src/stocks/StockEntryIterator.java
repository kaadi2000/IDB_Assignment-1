package stocks;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class StockEntryIterator implements Iterator<StockEntry> {

    private long pos;
    private final RandomAccessFile file;

    public StockEntryIterator(RandomAccessFile file) {
        // TODO
        this.file = file;
        this.pos = 0;
    }

    public boolean hasNext() {
        // TODO
        try {
            return this.pos < file.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StockEntry next() {
        // TODO
        if(hasNext()) {
            try {
                file.seek(pos);
                long id= file.readLong();

                short nameLen = file.readShort();
                byte[] byteName = new byte[nameLen];
                file.readFully(byteName);
                String name = new String(byteName, StandardCharsets.UTF_8);

                long timeStamp = file.readLong();
                double marketValue = file.readDouble();

                pos = file.getFilePointer();

                return new StockEntry(id, name, timeStamp, marketValue);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
