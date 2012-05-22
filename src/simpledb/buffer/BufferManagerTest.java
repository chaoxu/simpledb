package simpledb.buffer;

import simpledb.file.Block;
import simpledb.file.Page;
import simpledb.server.SimpleDB;

public class BufferManagerTest {

    public static void main( String args[] ) throws Exception
    {
        SimpleDB.init( "studentdb" );

        // create a file with 10 blocks
        Block block = new Block( "buffer.test", 10 );
        Page page = new Page();
        page.read( block );

        // create a buffer pool
        LRUBufferMgr bufferManager = new LRUBufferMgr( 4 );

        // 10 blocks and 4 buffers
        Block blocks[] = new Block[10];
        for( int i = 0; i < blocks.length; ++i )
            blocks[i] = new Block( "buffer.test", i );
        Buffer buffers[] = new Buffer[4];

        // pin the first 4 block of the file
        buffers[0] = bufferManager.pin( blocks[0] );
        Thread.sleep(100);
        buffers[1] = bufferManager.pin( blocks[1] );
        Thread.sleep(100);
        buffers[2] = bufferManager.pin( blocks[2] );
        Thread.sleep(100);
        buffers[3] = bufferManager.pin( blocks[3] );
        Thread.sleep(100);
        bufferManager.printBufferPool();

        // some testing
        bufferManager.unpin( buffers[3] );
        Thread.sleep(100);
        bufferManager.unpin( buffers[1] );
        Thread.sleep(100);
        bufferManager.unpin( buffers[2] );
        Thread.sleep(100);
        bufferManager.pin( blocks[5] );
        Thread.sleep(100);
        bufferManager.pin( blocks[6] );
        Thread.sleep(100);
        bufferManager.printBufferPool();
    }

}
