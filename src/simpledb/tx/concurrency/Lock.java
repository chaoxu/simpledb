package simpledb.tx.concurrency;

import java.util.TreeSet;

class Lock {
	
	private TreeSet<Integer> txnums;

	private int value;

	public final static int XLOCK_VALUE = -1;

	public Lock() {
		txnums = new TreeSet<Integer>();
		value = 0;
	}

	public void addSLock(int txnum) {
		txnums.add(txnum);
		value++;
	}

	public boolean removeSLock(int txnum) {
		if (txnums.remove(txnum)) {
			value--;
			return true;
		} else {
			return false;
		}
	}

	public void setXLock(int txnum) {
		txnums.clear();
		txnums.add(txnum);
		value = XLOCK_VALUE;
	}

	public boolean removeXLock(int txnum) {
		if (txnums.remove(txnum)) {
			value = 0;
			return true;
		} else {
			return false;
		}
	}

	public int getValue() {
		return value;
	}

	public boolean olderThanAllOtherTx(int txnum) {
		return txnums.first().compareTo(txnum) < 0;
	}

}