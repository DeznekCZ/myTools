package cz.deznekcz.tool;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.util.ObservableQueue;
import cz.deznekcz.util.ObservableQueue.QueueChangeListener.Change;

public class QueuedExecutor implements Executor {

	public class ThreadSequence extends Thread {

		private Queue<Runnable> queue;
		private boolean running;
		private boolean halt;

		/**
		 * 
		 * @param queue concurrent modifiable queue of tasks
		 * @param name thread name
		 */
		public ThreadSequence(Queue<Runnable> queue, String name) {
			this.queue = queue;
			this.running = true;
			this.halt = false;
		}

		@Override
		public void run() {
			boolean notEnd = false;
			Runnable task;
			do {
				synchronized (queue) {
					task = queue.poll();
					Logger.getGlobal().log(Level.INFO, "Active task = \"" + task + "\" queue is " + (queue.isEmpty() ? "" : "not ") + "empty");
				}
				if (task == null) continue;
				task.run();
				if (halt) break;
				synchronized (queue) {
					notEnd = !queue.isEmpty();
					running = false;
				}
				
			} while (notEnd);
			
			if (!halt) {
				try {
					Thread.sleep(200);
					startExecute();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public boolean isRunning() {
			return running;
		}

		public void halt() {
			halt = true;
			System.out.println(getClass().getSimpleName().concat(": - Wait for complete 1 of sequence."));
		}
	}

	ObservableQueue<Runnable> commands = new ObservableQueue<>();
	ThreadSequence running = null;
	private String name;
	private boolean ignoring = false;

	public QueuedExecutor(String executorName) {
		this.name = executorName;
		commands.addListener((Change<? extends Runnable> change) -> {
			if (change.wasOffer() && (running == null || !running.isRunning())) {
				startExecute();
			}
			});
	}
	
	private void startExecute() {
		synchronized (commands) {
			if (!isRunning()) {
				running = new ThreadSequence(commands, name);
				running.start();
			}
		}
	}

	private static final ILangKey IGNORE_EXECUTION = ILangKey.simple("QueuedExecutor.Action.IGNORE_EXECUTION", "Execution of \"%s\" is ignored");
	
	@Override
	public void execute(Runnable command) {
		if ( isIgnoring() ) {
			Logger.getGlobal().log(Level.CONFIG, IGNORE_EXECUTION.value(command.toString()));
		}
		synchronized (commands) {
			commands.offer(command);
		}
	}

	public String getName() {
		return name;
	}
	
	public synchronized boolean isRunning() {
		return running != null && running.isRunning();
	}

	public void stop() {
		if (running.isRunning()) 
			running.halt();
	}

	public synchronized boolean isIgnoring() {
		return ignoring;
	}

	public synchronized void setIgnoring(boolean ignoring) {
		this.ignoring = ignoring;
	}
}
