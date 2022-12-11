package com.guillaumevdn.gcore.lib.concurrency;

/**
 * Inspired from https://stackoverflow.com/questions/39437411/java-priority-in-semaphore
 */
public final class MainThreadAwareLock {}/*extends RWLock {

	private boolean mainThreadWaitingOnSomething = false;
	private int failedMainThreadAttempts = 0;

	private boolean isLockedOnSomething() {
		return getBackingLock().getReadLockCount() != 0 || getBackingLock().isWriteLocked();
	}

	@Override
	public void lockRead() {
		if (Bukkit.isPrimaryThread()) {
			if (isLockedOnSomething()) {
				mainThreadWaitingOnSomething = true;
			}
		}
		while (mainThreadWaitingOnSomething) {

		}
	}

	@Override
	public void lockWrite() {

	}

	public synchronized void tryLock() throws InterruptedException, MainThreadAwareLockMainThreadError {
		// main thread : prioritize
		if (Bukkit.isPrimaryThread()) {
			mainThreadWaitingOnSomething = true;
			if (locked) {
				wait(5000L);  // extreme case
				if (locked) {
					// the lock is held, we'll wait for the holding thread to finish its work and try again on next timer call
					// here, mainThreadWaiting is still true, so that new async threads have to wait before processing their things
					// this hopefully will avoid the main thread trying and failing indefinitely
					throw new MainThreadAwareLockMainThreadError(currentHoldingThread, ++failedMainThreadAttempts);
				}
			}
			locked = true;
			mainThreadWaitingOnSomething = false;
			failedMainThreadAttempts = 0;
			currentHoldingThread = "MAIN (" + Thread.currentThread().getName() + ")";
			return;
		}
		// other thread
		else {
			while (locked || mainThreadWaitingOnSomething) {
				wait(ConfigQC.progressLockMaxWaitAsyncThreadMillis);
				if (!QuestCreator.inst().isEnabled()) {
					throw new InterruptedException("plugin is disabled");
				}
			}
			locked = true;
			currentHoldingThread = Thread.currentThread().getName();
		}
	}

	public synchronized void unlock() {
		locked = false;
		notifyAll();
	}

}*/
