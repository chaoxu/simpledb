package simpledb.tx.recovery;

import simpledb.server.SimpleDB;
import simpledb.buffer.*;
import simpledb.file.Block;
import simpledb.log.BasicLogRecord;

class SetIntRecord implements LogRecord {
   private int txnum, offset, oldVal, newVal;
   private Block blk;

   /**
    * Creates a new setint log record.
    * @param txnum the ID of the specified transaction
    * @param blk the block containing the value
    * @param offset the offset of the value in the block
    * @param oldVal the old value
    * @param newVal the new value
    */
   public SetIntRecord(int txnum, Block blk, int offset, int oldVal, int newVal) {
      this.txnum = txnum;
      this.blk = blk;
      this.offset = offset;
      this.oldVal = oldVal;
      this.newVal = newVal;
   }

   /**
    * Creates a log record by reading five other values from the log.
    * @param rec the basic log record
    */
   public SetIntRecord(BasicLogRecord rec) {
      txnum = rec.nextInt();
      String filename = rec.nextString();
      int blknum = rec.nextInt();
      blk = new Block(filename, blknum);
      offset = rec.nextInt();
      oldVal = rec.nextInt();
      newVal = rec.nextInt();
   }

   /**
    * Writes a setInt record to the log.
    * This log record contains the SETINT operator,
    * followed by the transaction id, the filename, number,
    * and offset of the modified block, and the previous
    * integer value at that offset.
    * @return the LSN of the last log value
    */
   public int writeToLog() {
      Object[] rec = new Object[] {SETINT, txnum, blk.fileName(),
         blk.number(), offset, oldVal, newVal};
      return logMgr.append(rec);
   }

   public int op() {
      return SETINT;
   }

   public int txNumber() {
      return txnum;
   }

   public String toString() {
      return "<SETINT " + txnum + " " + blk + " " + offset + " " + oldVal + " " + newVal + ">";
   }

   /**
    * Replaces the specified data value with the old value saved in the log record.
    * The method pins a buffer to the specified block,
    * calls setInt to restore the saved value
    * (using a dummy LSN), and unpins the buffer.
    * @see simpledb.tx.recovery.LogRecord#undo(int)
    */
   public void undo(int txnum) {
      BufferMgr buffMgr = SimpleDB.bufferMgr();
      Buffer buff = buffMgr.pin(blk);
      buff.setInt(offset, oldVal, txnum, -1);
      buffMgr.unpin(buff);
   }

   /**
    * Replaces the specified data value with the new value saved in the log record.
    * The method pins a buffer to the specified block,
    * calls setInt to restore the saved value
    * (using a dummy LSN), and unpins the buffer. Implemented for HW#5.
    * @see simpledb.tx.recovery.LogRecord#undo(int)
    */
   public void redo(int txnum) {
      BufferMgr buffMgr = SimpleDB.bufferMgr();
      Buffer buff = buffMgr.pin(blk);
      buff.setInt(offset, newVal, txnum, -1);
      buffMgr.unpin(buff);
   }
}
