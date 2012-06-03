package simpledb.tx.recovery;

import simpledb.file.Block;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class TestLogWriter {

    public static void main( String args[] )
    {
        SimpleDB.init( "student" );

        Block blk1 = new Block( "logtest", 1 );
        Transaction t1 = new Transaction();
        t1.pin( blk1 );
        t1.setInt( blk1, 10, 100 );
        t1.setInt( blk1, 20, 101 );
        t1.commit();

        Block blk2 = new Block( "logtest", 2 );
        Transaction t2 = new Transaction();
        t2.pin( blk2 );
        t2.setInt( blk2, 10, 200 );
        t2.setInt( blk2, 20, 201 );
        SimpleDB.logMgr().flush( 100 );
    }

}
