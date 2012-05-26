package simpledb.buffer;

import simpledb.file.*;
import java.util.ArrayList;
import java.lang.Integer;

public class LRUBufferMgr {

   private Buffer[] bufferpool;
   private int numAvailable;
   private ArrayList<Integer> unpinned;
   
   public LRUBufferMgr(int numbuffs) {
      bufferpool = new Buffer[numbuffs];
      numAvailable = numbuffs;
      for (int i=0; i<numbuffs; i++)
         bufferpool[i] = new Buffer();
      unpinned = new ArrayList<Integer>(numbuffs);
      for (int i = 0; i < numbuffs; i++)
         unpinned.add(new Integer(i));
   }
   
   public synchronized void flushAll(int txnum) {
      for (Buffer buff : bufferpool)
         if (buff.isModifiedBy(txnum))
         buff.flush();
   }
   
   public synchronized Buffer pin(Block blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   public synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
      Buffer buff = chooseUnpinnedBuffer();
      if (buff == null)
         return null;
      buff.assignToNew(filename, fmtr);
      numAvailable--;
      buff.pin();
      return buff;
   }
   
   public synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned()) {
         numAvailable++;
         for (int i = 0; i < bufferpool.length; i++) {
            if (bufferpool[i].equals(buff)) {
               unpinned.add(i);
               break;
            }
         }
      }
   }

   public synchronized void printBufferPool() {
      for (int i = 0; i < bufferpool.length; i++) {
         System.out.println("Buffer " + i + "-> Pinned: " + bufferpool[i].isPinned());
      }
   }
 
   int available() {
      return numAvailable;
   }
   
   private Buffer findExistingBuffer(Block blk) {
      for (Buffer buff : bufferpool) {
         Block b = buff.block();
         if (b != null && b.equals(blk))
            return buff;
      }
      return null;
   }
   
   private Buffer chooseUnpinnedBuffer() {
      if (unpinned.size() > 0) {
         int bufferpoolIndex = unpinned.remove(0);
         return bufferpool[bufferpoolIndex];
      }

      return null;
   }
}
