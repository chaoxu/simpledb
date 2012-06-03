package simpledb.record;

import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class RecordFileTest {

    public static void main( String[] args )
    {
        SimpleDB.initFileLogAndBufferMgr( "student" );

        Schema schema = new Schema();
        schema.addIntField( "id" );
        schema.addStringField( "name", 10 );

        TableInfo tableInfo = new TableInfo( "students", schema );

        Transaction transaction = new Transaction();
        RecordFile recordFile = new RecordFile( tableInfo, transaction );
        while( recordFile.next() )
        {
            // print record
            System.out.print( "(" + recordFile.getInt( "id" ) + ", " );
            System.out.println( recordFile.getString( "name" ) + ") " );
        }

        recordFile.close();
        transaction.commit();
    }

}
