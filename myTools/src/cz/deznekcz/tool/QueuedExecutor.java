package cz.deznekcz.tool;

import java.util.Queue;
import java.util.concurrent.Executor;

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
				}
				task.run();
				if (halt) break;
				synchronized (queue) {
					notEnd = !queue.isEmpty();
					running = false;
				}
				
			} while (notEnd);
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

	public QueuedExecutor(String executorName) {
		this.name = executorName;
		commands.addListener((Change<? extends Runnable> change) -> {
			if (change.wasOffer() && (running == null || !running.isRunning())) {
				synchronized (commands) {
					running = new ThreadSequence(commands, executorName);
					running.start();
				}
			}
			});
	}
	
	@Override
	public void execute(Runnable command) {
		synchronized (commands) {
			commands.offer(command);
		}
	}

	public String getName() {
		return name;
	}
	
	public boolean isRunning() {
		return running != null && running.isRunning();
	}

	public void stop() {
		if (running != null) 
			running.halt();
	}
}
