package simpledb.tx.concurrency;

import simpledb.file.Block;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class ConcurrencyTest {

	public static void main(String[] args) {

        SimpleDB.init( "student" );

        Block blk1 = new Block( "ConcurrencyTest", 1 );
        Block blk2 = new Block( "ConcurrencyTest", 2 );

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();

        t1.pin( blk1 );
        t2.pin( blk2 );
        t1.setInt( blk1, 10, 100 );
        t2.setInt( blk2, 10, 200 );
        t1.setInt( blk2, 20, 101 );
        t2.setInt( blk2, 20, 201 );
        t1.commit();
        t2.commit();

        SimpleDB.logMgr().flush( 100 );
	}
}