package stocks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class Stocks implements Iterable<StockEntry> {
    private List<StockEntry> stockEntries;
    private final RandomAccessFile file;

    Stocks(String path) throws FileNotFoundException {
        // TODO
        stockEntries = new ArrayList<>();

        try {
            file = new RandomAccessFile(path, "r");
            FileChannel channel = file.getChannel();
            final int channelSize = (int) channel.size();
            ByteBuffer buffer = ByteBuffer.allocate(channelSize);
            channel.read(buffer);
            buffer.flip();

            while(buffer.remaining() > 0) {
                stockEntries.add(new StockEntry(buffer));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public StockEntry get(int i) {
        // TODO
        if(i > -1 && i < stockEntries.size()) {
            return stockEntries.get(i);
        }
        throw new IndexOutOfBoundsException("Invalid index");
    }

    @Override
    public Iterator<StockEntry> iterator() {
        return new StockEntryIterator(file);
    }

    public class stockEntryIterator implements Iterator<StockEntry> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public StockEntry next() {
            if(currentIndex < stockEntries.size()) {
                return stockEntries.get(currentIndex++);
            }
            throw new NoSuchElementException();
        }
    }

}