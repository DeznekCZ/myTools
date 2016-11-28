package cz.deznekcz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.istack.internal.NotNull;

import cz.deznekcz.reference.OutBoolean;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.tool.QueuedExecutor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import sun.reflect.CallerSensitive;

public class Utils {
	
	private static QueuedExecutor executor;
	static {
		executor = new QueuedExecutor(Utils.class.toString().concat("$").concat(QueuedExecutor.class.toString()));
	}

	/**
	 * 
	 * @param observable value which will be checked as not null
	 * @param action instance of lambda of action apply-able to not null value
	 * @param values rejected values for this action (array, none (meaning: not null) or more values)
	 */
	@CallerSensitive
	public static <T> void initAfterNotValue(@NotNull ObservableValue<T> observable, Runnable action, T...values) {
		ChangeListener<T> change = null;
		if (values == null || (values.getClass().isArray() && values.length == 0)) {
			change = new ChangeListener<T>() {
				public void changed(ObservableValue<? extends T> o, T l, T n) {
					if (n == null) {
						executor.execute(action);
						observable.removeListener(this);
					}
				}
			};
		} else if (values.getClass().isArray()) {
			change = new ChangeListener<T>() {
				public void changed(ObservableValue<? extends T> o, T l, T n) {
					boolean found = false;
					T stored = observable.getValue();
					if (stored == null) {
						for(T value : values) {
							if (value == null) {
								found = true;
								break;
							}
						}
					} else {
						for(T value : values) {
							if (stored.equals(value)) {
								found = true;
								break;
							}
						}
					}
					
					if (found) {
						executor.execute(action);
						observable.removeListener(this);
					}
				}
			};
		} else {
			change = new ChangeListener<T>() {
				public void changed(ObservableValue<? extends T> o, T l, T n) {
					if (observable.getValue() != null && observable.getValue().equals(values)) {
						observable.removeListener(this);
					}
				}
			};
		}
		if (change != null) 
			observable.addListener(change);
	}

	public static ResourceBundle bundlePack(ResourceBundle... bundles) {
		return new ResourceBundle() {

			@Override
			protected Object handleGetObject(String key) {
				for (ResourceBundle resourceBundle : bundles) {
					if (resourceBundle.containsKey(key))
						return resourceBundle.getObject(key);
				}
				return null;
			}
			
			@Override
			public boolean containsKey(String key) {
				for (ResourceBundle resourceBundle : bundles) {
					if (resourceBundle.containsKey(key))
						return true;
				}
				return false;
			}

			@Override
			public Enumeration<String> getKeys() {
				List<String> keyList = new ArrayList<>();
				for (ResourceBundle resourceBundle : bundles) {
					keyList.addAll(
							Utils.iterableToList(
									ForEach.enumeration(
											resourceBundle.getKeys()
											)
									)
							);
				}
				return Utils.iterableToEnumeration(keyList);
			}
			
		};
	}

	public static <E> Enumeration<E> iterableToEnumeration(Iterable<E> iterable) {
		return new Enumeration<E>() {
			Iterator<E> it = iterable.iterator();
			
			@Override
			public E nextElement() {
				return it.next();
			}
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}
		};
	}
	
	

	public static <E> List<E> iterableToList(Iterable<E> iterable) {
		ArrayList<E> list = new ArrayList<>();
		for (E e : iterable) {
			list.add(e);
		}
		return list;
	}

	public static <C> ResourceBundle classFieldValues(C instance, Class<C> clazz, boolean normalizedString) {
		try {
			return new ResourceBundle() {
				private HashMap<String, Object> fields = new HashMap<>();
				private Enumeration<String> keys;
				{
					// TODO
					for (Field field : clazz.getDeclaredFields()) {
						boolean accesible = field.isAccessible();
						field.setAccessible(true);
						Object obj = field.get(instance);
						if (obj instanceof String)
							fields.put(field.toString(), normalized((String) obj));
						else 
							fields.put(field.toString(), field.get(instance));
						field.setAccessible(accesible);
					}
					
					keys = Utils.iterableToEnumeration(fields.keySet());
				}
				
				@Override
				public Enumeration<String> getKeys() {
					return keys;
				}

				@Override
				protected Object handleGetObject(String key) {
					return fields.get(key);
				}
				
				private String normalized(String str) {
				    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
				    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
				    return pattern.matcher(nfdNormalizedString).replaceAll("");
				}
			};
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static <C> ResourceBundle classFieldValues(C instance, Class<C> clazz) {
		return classFieldValues(instance, clazz, false);
	}

	public static <C> String classField(Class<C> clazz, String fieldName) {
		Field field;
		try {
			field = clazz.getDeclaredField(fieldName);
			return field.toString();
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return fieldName;
		}
	}

	@SuppressWarnings("unchecked")
	public static <C,V> V classFieldValue(Class<C> clazz, String fieldName, ResourceBundle resources) {
		String classField = classField(clazz, fieldName);
		if (resources.containsKey(classField))
			return (V) resources.getObject(classField);
		else
			throw new NullPointerException();
	}

	public static void stream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
	}

	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static <T> T[] bindArrays(IntFunction<T[]> generator, T[] original, @SuppressWarnings("unchecked") T... added) {
		if (added == null) return original;
		if (added.length < 1) return original;
		if (original == null) return added;
		T[] array = generator.apply(original.length + added.length);
		for (int i = 0; i < array.length; ) {
			for (int j = 0; j < original.length; j++, i++) {
				array[i] = original[j];
			}
			for (int j = 0; j < added.length; j++, i++) {
				array[i] = added[j];
			}
		}
		return array;
	}

	public static void copyProperties(ResourceBundle asBundle, ResourceBundle classFieldValues) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Combine comparators by delegated functions that is represented by {@link BiFunction} instances.
	 * Comparing is solving like OR conditions for boolean. If is taken first non zero result returns that result. 
	 * <br><b>Using:</b>
	 * <br>Utils.priorityComparator(String::compare, Collator.getInstance()::compare, AnotherClass::methodWithSameNotation)
	 * @param comparators
	 * @return
	 */
	@SafeVarargs
	public static <C> Comparator<C> priorityComparator(BiFunction<C,C,Integer>...comparators) {
		return new Comparator<C>() {
			@Override
			public int compare(C o1, C o2) {
				int result = 0;
				for (int i = 0; i < comparators.length; i++) {
					result = comparators[i].apply(o1, o2);
					if (result != 0)
						break;
				}
				return result;
			}
		};
	}

	public static String textFromFile(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void externalExecution(String execution, OutBoolean anyError, OutString errorValue) throws IOException, InterruptedException {
		externalExecution(execution, anyError, errorValue, (String[]) null);
	}
	
	public static void externalExecution(String execution, OutBoolean anyError, OutString errorValue, String... commands) throws IOException, InterruptedException {
		String s;
		System.out.println(execution);
		Matcher m = Pattern.compile("\\.exe").matcher(execution);
		m.find();
		String appname = execution.substring(0, m.end());

    	System.out.println("External execution "+appname+"\n"+execution);
    	Process p = Runtime.getRuntime().exec(execution); //"cmd.exe"
    	
    	OutputStream ps = p.getOutputStream();
//    	ps.println(execution);
    	
    	Thread.sleep(300L);
    	
    	if (commands != null && commands.length > 0) {
    		for (String string : commands) {
				ps.write((string + " /r/n").getBytes());
				ps.flush();
			}
    	}
    	
//    	ps.println("exit");

    	BufferedReader stdInput = new BufferedReader(new 
    			InputStreamReader(p.getInputStream()));

    	BufferedReader stdError = new BufferedReader(new 
    			InputStreamReader(p.getErrorStream()));

    	// read the output from the command
    	System.out.println("["+appname+"] Here is the standard output of the command:\n");
    	while ((s = stdInput.readLine()) != null) {
    		System.out.println("["+appname+"]".concat(s));
    	}

    	errorValue.set();
    	// read any errors from the attempted command
    	System.out.println("["+appname+"] Here is the standard error of the command (if any):\n");
    	while ((s = stdError.readLine()) != null) {
    		System.err.println("["+appname+"]".concat(s));
    		anyError.setTrue();
    		errorValue.append(s).append("\n");
    	}
	}

	public static String[] readLines(URI uri, Predicate<String> filter, String encoding) throws MalformedURLException, IOException {
		Scanner sc = new Scanner(uri.toURL().openStream(), encoding);
		ArrayList<String> list = new ArrayList<>();
		String line;
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			if (filter.test(line))
				continue;
			list.add(line);
		}
		sc.close();
		return list.toArray(new String[list.size()]);
	}
	
	public static enum LineFilter implements Predicate<String> {
		NONE((s) -> false),
		FREE((s) -> s == null || s.trim().length() == 0);
		
		public static class Match implements Predicate<String> {
			private final String ANY = "\\.*";
			
			public Match contains(String substring) {
				return pattern(ANY+substring+ANY);
			}
			
			public Match startsWith(String substring) {
				return pattern(substring+ANY);
			}
			
			public Match endsWith(String substring) {
				return pattern(ANY+substring);
			}
			
			public Match pattern(String patern) {
				return new Match((line) -> line.matches(patern));
			}

			private Match(Predicate<String> predicate) {
				this.predicate = predicate;
			}

			private Predicate<String> predicate;
			
			@Override
			public boolean test(String t) {
				return predicate.test(t);
			}
		}

		private LineFilter(Predicate<String> predicate) {
			this.predicate = predicate;
		}

		private Predicate<String> predicate;
		
		@Override
		public boolean test(String t) {
			return predicate.test(t);
		}
	}

	@SafeVarargs
	public static <A> A[] array(A... array) {
		return array;
	}

	public static <T> ChangeListener<T> streamChange(Class<T> classOfChangeAble, Consumer<T> stream) {
		return new ChangeListener<T>() {
			@Override
			public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
				stream.accept(newValue);
			}
		};
	}
}
