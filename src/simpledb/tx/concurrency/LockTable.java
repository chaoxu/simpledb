package simpledb.tx.concurrency;

import simpledb.file.Block;
import java.util.*;

/**
 * The lock table, which provides methods to lock and unlock blocks.
 * If a transaction requests a lock that causes a conflict with an
 * existing lock, then that transaction is placed on a wait list.
 * There is only one wait list for all blocks.
 * When the last lock on a block is unlocked, then all transactions
 * are removed from the wait list and rescheduled.
 * If one of those transactions discovers that the lock it is waiting for
 * is still locked, it will place itself back on the wait list.
 * @author Edward Sciore
 */
class LockTable {
   private static final long MAX_TIME = 10000; // 10 seconds
   
   private Map<Block,Lock> locks = new HashMap<Block,Lock>();
   
   /**
    * Grants an SLock on the specified block.
    * If an XLock exists when the method is called,
    * then the calling thread will be placed on a wait list
    * until the lock is released.
    * If the thread remains on the wait list for a certain 
    * amount of time (currently 10 seconds),
    * then an exception is thrown.
    * @param blk a reference to the disk block
    */
   public synchronized void sLock(Block blk, int txnum) {

      if (!locks.containsKey(blk))
        locks.put(blk, new Lock());

      try {
         if (hasXlock(blk) && !olderThan(blk, txnum)) {
            System.out.println("T" + txnum + ": SL(" + blk.number() + ") ABORTED");
            throw new LockAbortException();
         }
         while (hasXlock(blk))
            wait(MAX_TIME);
         int val = getLockVal(blk);  // will not be negative
         locks.get(blk).addSLock(txnum);
         System.out.println("T" + txnum + ": SL(" + blk.number() + ") -> " + blk.toString());
      }
      catch(InterruptedException e) {
         System.out.println("T" + txnum + ": SL(" + blk.number() + ") ABORTED");
         throw new LockAbortException();
      }
   }
   
   /**
    * Grants an XLock on the specified block.
    * If a lock of any type exists when the method is called,
    * then the calling thread will be placed on a wait list
    * until the locks are released.
    * If the thread remains on the wait list for a certain 
    * amount of time (currently 10 seconds),
    * then an exception is thrown.
    * @param blk a reference to the disk block
    */
   synchronized void xLock(Block blk, int txnum) {

      if (!locks.containsKey(blk))
        locks.put(blk, new Lock());

      try {
         if (hasOtherSLocks(blk) && !olderThan(blk, txnum)) {
            System.out.println("T" + txnum + ": XL(" + blk.number() + ") ABORTED");
            throw new LockAbortException();
         }
         while (hasOtherSLocks(blk))
            wait(MAX_TIME);
         locks.get(blk).setXLock(txnum);
         System.out.println("T" + txnum + ": XL(" + blk.number() + ") -> " + blk.toString());
      }
      catch(InterruptedException e) {
         System.out.println("T" + txnum + ": XL(" + blk.number() + ") ABORTED");
         throw new LockAbortException();
      }
   }
   
   /**
    * Releases a lock on the specified block.
    * If this lock is the last lock on that block,
    * then the waiting transactions are notified.
    * @param blk a reference to the disk block
    */
   synchronized void unlock(Block blk, int txnum) {

      if (!locks.containsKey(blk))
        locks.put(blk, new Lock());

      int val = getLockVal(blk);
      if (val > 1)
         locks.get(blk).removeSLock(txnum);
      else {
         locks.remove(blk);
         notifyAll();
      }
      System.out.println("T" + txnum + ": UL(" + blk.number() + ") -> " + blk.toString());

   }

   private boolean olderThan(Block blk, int txnum) {

      if (!locks.containsKey(blk))
        locks.put(blk, new Lock());

      return locks.get(blk).olderThanAllOtherTx(txnum); 
   }
   
   private boolean hasXlock(Block blk) {
      return getLockVal(blk) == Lock.XLOCK_VALUE;
   }
   
   private boolean hasOtherSLocks(Block blk) {
      return getLockVal(blk) > 1;
   }
   
   private int getLockVal(Block blk) {

      if (!locks.containsKey(blk))
        locks.put(blk, new Lock());

      Integer ival = locks.get(blk).getValue();
      return ival.intValue();
   }
}
