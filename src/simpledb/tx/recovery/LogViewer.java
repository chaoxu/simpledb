package simpledb.tx.recovery;

import java.util.Iterator;

import simpledb.server.SimpleDB;

public class LogViewer {

    public static void main( String args[] )
    {
        SimpleDB.initFileLogAndBufferMgr( "student" );

        Iterator<LogRecord> it = new LogRecordIterator();
        while( it.hasNext() )
        {
            LogRecord logRecord = it.next();
            System.out.println( logRecord );
            if( logRecord.op() == LogRecord.CHECKPOINT ) break;
        }
    }

}
