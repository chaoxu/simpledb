package simpledb.file;

import java.util.Date;

import simpledb.server.SimpleDB;

public class PageTest {

    public static void main( String args[] )
    {
        SimpleDB.init( "studentdb" );

        String filename = "page.test";
        Page page = new Page();
        Block block;

        // create a file and write some data to block 2.
        block = new Block( filename, 2 );
        page.read( block );
        page.setInt( 0, 100 );
        page.setDouble( 16, 100 / 3.0 );
        page.setBoolean( 32, true );
        page.setBytes( 48, "hello world".getBytes() );
        page.setDate( 100, new Date() );
        page.write( block );

        // clear the page by reading from block 1, which should contain all 0's.
        block = new Block( filename, 1 );
        page.read( block );
        System.out.println( "Clear page: " + page.getInt( 0 ) );

        // re-read block 2 and see if we can get all the data back.
        block = new Block( filename, 2 );
        page.read( block );
        System.out.println( "Int: " + page.getInt( 0 ) );
        System.out.println( "Double: " + page.getDouble( 16 ) );
        System.out.println( "Boolean: " + page.getBoolean( 32 ) );
        System.out.println( "Bytes: " + new String( page.getBytes( 48 ) ) );
        System.out.println( "Date: " + page.getDate( 100 ) );
    }

}
