package cz.deznekcz.util;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import cz.deznekcz.util.ObservableQueue.QueueChangeListener.Change;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import sun.reflect.CallerSensitive;

/**
 * Simple observable queue. Only methods from {@link Queue} or {@link Observable} allowed.
 * @author Zdenek Novotny (DeznekCZ)
 *
 * @param <E>
 */
public class ObservableQueue<E> extends AbstractQueue<E> implements Observable, Queue<E> {
	
	public static interface QueueChangeListener<E> {
		void onChange(Change<? extends E> change);
		
		public abstract class Change<E> {
			private final ObservableQueue<E> queue;
			protected Change(ObservableQueue<E> observableQueue) {
				this.queue = observableQueue;
			}
			public ObservableQueue<E> getQueue() {
				return queue;
			}
			public abstract boolean wasOffer();
			public abstract boolean wasPoll();
			public abstract E value();
			
			public static <C> Change<C> offer(final ObservableQueue<C> observableQueue, final C e) {
				return new Change<C>(observableQueue) {
					public boolean wasOffer() {return true;}
					public boolean wasPoll() {return false;}
					public C value() {return e;}
				};
			}
			
			public static <C> Change<C> poll(final ObservableQueue<C> observableQueue, final C e) {
				return new Change<C>(observableQueue) {
					public boolean wasOffer() {return false;}
					public boolean wasPoll() {return true;}
					public C value() {return e;}
				};
			}
		}
	}

	private List<InvalidationListener> invalidationlisteners;
	private List<QueueChangeListener<? super E>> queueChangeListeners;

	public ObservableQueue() {
		super();
		invalidationlisteners = new ArrayList<>();
		queueChangeListeners = new ArrayList<>();
	}
	
	@Override
	public synchronized boolean offer(E e) {
		boolean offer = link(e);
		if (offer) {
			invalidationlisteners.forEach((listener) -> listener.invalidated(ObservableQueue.this));
			queueChangeListeners.forEach((listener) -> listener.onChange(Change.offer(ObservableQueue.this,e)));
		}
		return offer;
	}

	@Override
	public synchronized E poll() {
		E e = unlink();
		if (e != null) {
			invalidationlisteners.forEach((listener) -> listener.invalidated(ObservableQueue.this));
			queueChangeListeners.forEach((listener) -> listener.onChange(Change.poll(ObservableQueue.this,e)));
		}
		return e;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		invalidationlisteners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		invalidationlisteners.remove(listener);
	}
	
	public void addListener(QueueChangeListener<? super E> listener) {
		queueChangeListeners.add(listener);
	}

	public void removeListener(QueueChangeListener<? super E> listener) {
		queueChangeListeners.remove(listener);
	}

	private static class Item<E> {
		public E value;
		public Item<E> next;
	}

	private Item<E> head;
	private Item<E> tail;
	
	@Override
	public E peek() {
		return head.value;
	}
	
	private boolean link(E e) {
		if (e == null)
			return false;
		if (head == null) {
			head = new Item<>();
			head.value = e;
			tail = head;
		} else {
			tail.next = new Item<>();
			tail.next.value = e;
			tail = tail.next;
		}
		return true;
	}
	
	private E unlink() {
		if (head == null)
			return null;
		Item<E> tmp = head;
		head = head.next;
		return tmp.value;
	}

	@CallerSensitive
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Item<E> head = ObservableQueue.this.head;
			Item<E> tail = ObservableQueue.this.tail;
			
			@Override
			public boolean hasNext() {
				synchronized (ObservableQueue.this) {
					return head != tail /* Queue may be changed */
							&& head != null;
				}
			}
			@Override
			public E next() {
				synchronized (ObservableQueue.this) {
					Item<E> tmp = head;
					if (head == null)
						throw new NullPointerException("No more elements");
					head = head.next;
					return tmp.value;
				}
			}
		};
	}

	@Override
	public synchronized int size() {
		if (head == null) return 0;
		
		int count = 1;
		Item<E> cur = head;
		while ( (cur = cur.next) != null ) count ++;
		
		return count;
	}
	
}
