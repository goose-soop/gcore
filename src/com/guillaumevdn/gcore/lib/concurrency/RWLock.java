package com.guillaumevdn.gcore.lib.concurrency;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public final class RWLock {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);  // fair lock

	// ----- lock manipulation

	public ReentrantReadWriteLock getBackingLock() {
		return lock;
	}

	public void lockRead() {
		lock.readLock().lock();
	}

	public void unlockRead() {
		lock.readLock().unlock();  // unlock only once ; there might be multiple operations during the same "parent read lock", and always unlocking all held in this method would make it so parent operations could end up being made while not locked at all
	}

	public void lockWrite() {
		lock.writeLock().lock();
	}

	public void unlockWrite() {
		lock.writeLock().unlock();  // unlock only once ; there might be multiple operations during the same "parent write lock", and always unlocking all held in this method would make it so parent operations could end up being made while not locked at all
	}

	// ----- shortcuts to read/write

	public void read(Runnable operation) {
		try {
			lockRead();
			operation.run();
			unlockRead();
		} catch (Throwable error) {
			unlockRead();
			throw new RuntimeException("ERROR WHILE LOCKED ON READ/no result, current read " + lock.getReadHoldCount() + " write " + lock.getWriteHoldCount() + ", thread " + Thread.currentThread().toString(), error);
		}
	}

	public void readThrowable(ThrowableRunnable operation) throws Throwable {
		try {
			lockRead();
			operation.run();
			unlockRead();
		} catch (Throwable error) {
			unlockRead();
			throw new RuntimeException("ERROR WHILE LOCKED ON READ/no result, current read " + lock.getReadHoldCount() + " write " + lock.getWriteHoldCount() + ", thread " + Thread.currentThread().toString(), error);
		}
	}

	public <R> R read(Supplier<R> operation) {
		try {
			lockRead();
			R r = operation.get();
			unlockRead();
			return r;
		} catch (Throwable error) {
			unlockRead();
			throw new RuntimeException("ERROR WHILE LOCKED ON READ/result, current read " + lock.getReadHoldCount() + " write " + lock.getWriteHoldCount() + ", thread " + Thread.currentThread().toString(), error);
		}
	}

	public void write(Runnable operation) {
		try {
			lockWrite();
			operation.run();
			unlockWrite();
		} catch (Throwable error) {
			unlockWrite();
			throw new RuntimeException("ERROR WHILE LOCKED ON WRITE/no result, current read " + lock.getReadHoldCount() + " write " + lock.getWriteHoldCount() + ", thread " + Thread.currentThread().toString(), error);
		}
	}

	public void writeThrowable(ThrowableRunnable operation) {
		try {
			lockWrite();
			operation.run();
			unlockWrite();
		} catch (Throwable error) {
			unlockWrite();
			throw new RuntimeException("ERROR WHILE LOCKED ON WRITE/no result, current read " + lock.getReadHoldCount() + " write " + lock.getWriteHoldCount() + ", thread " + Thread.currentThread().toString(), error);
		}
	}

	public <R> R write(Supplier<R> operation) {
		try {
			lockWrite();
			R r = operation.get();
			unlockWrite();
			return r;
		} catch (Throwable error) {
			unlockWrite();
			throw new RuntimeException("ERROR WHILE LOCKED ON WRITE/result, current read " + lock.getReadHoldCount() + " write " + lock.getWriteHoldCount() + ", thread " + Thread.currentThread().toString(), error);
		}
	}

}
