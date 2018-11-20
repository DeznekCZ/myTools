package cz.deznekcz.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Once {

	public static abstract class OnceEntry {
		private String type;

		private OnceEntry(String type) {
			this.type = type;
		}

		public abstract boolean canRun();

		@Override
		public String toString() {
			return "Once:"+type;
		}

		public boolean doSkip() {
			return !canRun();
		}
	}

	public static class OnceIF {

		private OnceIF() {

		}

		private OnceEntry entry;

		public OnceEntry per(long milis) {
			String typeString = "PER("+milis+"ms)";
			if (entry != null && !entry.type.equals(typeString))
				return entry;
			else {
				entry = new OnceEntry(typeString) {
					private long time = 0L;

					@Override
					public boolean canRun() {
						long now = System.currentTimeMillis();
						if (now > time) {
							time = now + milis;
							return true;
						} else {
							return false;
						}
					}
				};
				return entry;
			}
		}

		public OnceEntry perIf(long milis, Supplier<Boolean> predicate) {
			String typeString = "PER("+milis+"ms)IF";
			if (entry != null && entry.type.equals(typeString))
				return entry;
			else {
				entry = new OnceEntry(typeString) {
					private long time = 0L;

					@Override
					public boolean canRun() {
						long now = System.currentTimeMillis();
						if (now > time && predicate.get()) {
							time = now + milis;
							return true;
						} else {
							return false;
						}
					}
				};
				return entry;
			}
		}
	}

	private static Map<String, OnceIF> registered = new HashMap<>();

	private Once() {

	}

	public static OnceIF id(String keyInstance) {
		if (registered.containsKey(keyInstance)) {
			return registered.get(keyInstance);
		} else {
			OnceIF once = new OnceIF();
			registered.put(keyInstance, once);
			return once;
		}
	}
}
