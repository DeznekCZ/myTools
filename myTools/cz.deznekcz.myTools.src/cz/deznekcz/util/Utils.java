package cz.deznekcz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.reference.OutBoolean;
import cz.deznekcz.reference.OutInteger;
import cz.deznekcz.reference.OutString;
import cz.deznekcz.tool.QueuedExecutor;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.util.Utils.TreeMapHandler.TreeMapPath;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Pair;
import sun.reflect.CallerSensitive;

public class Utils {

	private static final String NULL = "null";
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
	@SafeVarargs
	@CallerSensitive
	public static <T> void initAfterNotValue(ObservableValue<T> observable, Runnable action, T...values) {
		Objects.nonNull(observable);
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
				java.util.List<String> keyList = new ArrayList<>();
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



	public static <E> java.util.List<E> iterableToList(Iterable<E> iterable) {
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
							fields.put(field.toString(), Utils.normalizedString((String) obj));
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
			};
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String normalizedString(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
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
		try {
			externalExecution(execution, anyError, errorValue, null, commands);
		} catch (TimeoutException e) {
			errorValue.set(e.getLocalizedMessage());
			anyError.set(true);
		}
	}

	public static void externalExecution(String execution, OutBoolean anyError, OutString errorValue,
			Pair<TimeUnit, Long> timeout, String... commands) throws IOException, InterruptedException, TimeoutException {
		String s;
		System.out.println(execution);
		Matcher m = Pattern.compile("\\.exe").matcher(execution);
		m.find();
		String appname = execution.substring(0, m.end());

    	System.out.println("External execution "+appname+"\n"+execution);
    	Process p = Runtime.getRuntime().exec(execution); //"cmd.exe")

    	if (timeout != null) {
    		Worker worker = new Worker(p);
        	worker.start();
        	try {
        		worker.join(timeout.getKey().toMillis(timeout.getValue()));
        		if (worker.exit == null)
        			throw new TimeoutException();
        	} catch(InterruptedException ex) {
        		worker.interrupt();
        		Thread.currentThread().interrupt();
        		throw ex;
        	} finally {
        		p.destroy();
        	}
    	}


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

	private static class Worker extends Thread {
		  private final Process process;
		  private Integer exit;
		  private Worker(Process process) {
		    this.process = process;
		  }
		  public void run() {
		    try {
		      exit = process.waitFor();
		    } catch (InterruptedException ignore) {
		      return;
		    }
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

	public static class FX {

		private FX() {
		}

		public static class VoidBinding implements InvalidationListener {

			private Consumer<Observable> onChange;
			private Observable[] observables;

			public VoidBinding(Consumer<Observable> onChange, Observable...observables) {
				this.onChange = onChange;
				this.observables = observables;
				for (Observable observable : this.observables) {
					observable.addListener(this);
				}
			}

			@Override
			public void invalidated(Observable observable) {
				onChange.accept(observable);
			}

		}

		public static class Callbacks {


			public static <T,V> Callback<TableColumn<T, V>, TableCell<T, V>> tableDefaultCell(boolean wraptext,
					Pos alignment, Insets padding, Function<V,String[]> converter, String styleSheet, String[]...styles) {
				return tbl -> new TableCell<T, V>() {
					protected void updateItem(V item, boolean empty) {
						if (empty) {
							setGraphic(null);
						} else {
							VBox box = new VBox();
							box.getStylesheets().add(styleSheet);
							box.setPadding(padding);
							box.setSpacing(padding.getBottom());
							box.setAlignment(alignment);
							String[] texts = converter.apply(item);
							if (texts == null) return;
							for (int i = 0 ; i < texts.length ; i++) {
								Text text = new Text(texts[i]);
								text.getStyleClass().addAll(styles[i]);
								text.wrappingWidthProperty().bind(widthProperty().subtract(padding.getLeft() + padding.getRight()));
								box.getChildren().add(text);
							}
							setGraphic(box);
							setAlignment(alignment);
//							getTableRow().setMinHeight(text.getLayoutBounds().getHeight());
						}

						super.updateItem(item, empty);
					};
				};
			}

		}

		public static <T> ChangeListener<T> onValue(final Predicate<T> test, final Runnable runLater) {
			return (observable, oldValue, newValue) -> {
					if (test.test(oldValue)) Platform.runLater(runLater);
				};
		}

		public static VoidBinding voidBinding(Consumer<Observable> onChange, Observable...observables) {
			return new VoidBinding(onChange, observables);
		}
	}

	public static class Functions {
		public static <T> Function<T,T> same() {
			return (a) -> a;
		}

		public static <T> Function<T,T[]> toArray(IntFunction<T[]> toArray) {
			return (a) -> {
				T[] arr = toArray.apply(1);
				arr[0] = a;
				return arr;
			};
		}

		public static <T> Function<T,String[]> toStringArray() {
			return (a) -> {
				if (a instanceof String) {
					return new String[] {(String) a};
				} else if (a != null) {
					return new String[] {a.toString()};
				} else {
					return new String[] {null};
				}
			};
		}
	}

	public static class List {

		public static <P,O extends P,R> Consumer<P> applyIfNot(Predicate<O> predicate, Function<O,R> action) {
			return new Consumer<P>() {
				@SuppressWarnings("unchecked")
				@Override
				public void accept(P t) {
					if (!predicate.test((O) t)) action.apply((O) t);
				}
			};
		}

		public static <P,O extends P,R> Consumer<P> applyIf(Predicate<O> predicate, Function<O,R> action) {
			return new Consumer<P>() {
				@SuppressWarnings("unchecked")
				@Override
				public void accept(P t) {
					if (predicate.test((O) t)) action.apply((O) t);
				}
			};
		}
	}

	public static Pair<TimeUnit,Long> timeout(TimeUnit unit, long value) {
		return new Pair<>(unit,value);
	}

	@SafeVarargs
	public static <T> String concat(T...array) {
		return concat("", array);
	}

	public static <T> String concat(Iterable<String> iterable) {
		return concat("", iterable);
	}

	@SafeVarargs
	public static <T> String concat(Function<T, String> toStringMethod, T...array) {
		return concat("", toStringMethod, array);
	}

	public static <T> String concat(Function<T, String> toStringMethod, Iterable<String> iterable) {
		return concat("", toStringMethod, iterable);
	}

	/**
	 * Concatenating static method
	 * @param <T> Type of instances inside {@link Iterable}
	 * @param binder string value between each element
	 * @param toStringMethod method delegate (DeclaredClass::toString or another)
	 * <br>or functional interface matching {@link Function}&lt;{@link T},{@link String}&gt;
	 * @param iterable instance which contains values of type {@link T}
	 * @return Returns concatenated string of {@link Iterable}
	 *
	 * @see #concat(Iterable)
	 * @see #concat(Function, Iterable)
	 * @see #concat(String, Iterable)
	 * @see #concat(String, Function, Object...)
	 */
	public static <T> String concat(String binder, Function<T, String> toStringMethod, Iterable<T> iterable) {
		Objects.requireNonNull(binder);
		Objects.requireNonNull(iterable);
		Iterator<T> iterator = iterable.iterator();

		if (iterator.hasNext()) {
			T next = iterator.next();
			OutString string = OutString.init(next != null ? toStringMethod.apply(next) : NULL);
			while (iterator.hasNext()) {
				next = iterator.next();
				string.append(binder);
				string.append(next != null ? toStringMethod.apply(next) : NULL);
			}
			return string.get();
		} else {
			return "";
		}
	}

	/**
	 * Concatenating static method
	 * @param <T> Type of instances inside array
	 * @param binder string value between each element
	 * @param toStringMethod method delegate (DeclaredClass::toString or another)
	 * <br>or functional interface matching {@link Function}&lt;{@link T},{@link String}&gt;
	 * @param array of values of type {@link T}
	 * @return Returns concatenated string of array
	 *
	 * @see #concat(Object...)
	 * @see #concat(String, Object...)
	 * @see #concat(Function, Object...)
	 * @see #concat(String, Function, Iterable)
	 */
	@SafeVarargs
	public static <T> String concat(String binder, Function<T, String> toStringMethod, T...array) {
		Objects.requireNonNull(binder);
		Objects.requireNonNull(array);

		if (array.length > 0) {
			OutString string = OutString.init(array[0] != null ? toStringMethod.apply(array[0]) : NULL);
			for (int i = 1; i < array.length; i++) {
				string.append(binder);
				string.append(array[i] != null ? toStringMethod.apply(array[i]) : NULL);
			}
			return string.get();
		} else {
			return "";
		}
	}

	public static <T> String concat(String binder, Iterable<T> iterable) {
		Objects.requireNonNull(binder);
		Objects.requireNonNull(iterable);
		Iterator<T> iterator = iterable.iterator();

		if (iterator.hasNext()) {
			T next = iterator.next();
			OutString string = OutString.init(next != null ? next.toString() : NULL);
			while (iterator.hasNext()) {
				next = iterator.next();
				string.append(binder);
				string.append(next != null ? next.toString() : NULL);
			}
			return string.get();
		} else {
			return "";
		}
	}

	/**
	 * Concatenating static method
	 * @param <T> Type of instances inside array
	 * @param binder string value between each element
	 * @param toStringMethod method delegate (DeclaredClass::toString or another)
	 * <br>or functional interface matching {@link Function}&lt;{@link T},{@link String}&gt;
	 * @param array of values of type {@link T}
	 * @return Returns concatenated string of array
	 *
	 * @see #concat(Object...)
	 * @see #concat(Function, Object...)
	 * @see #concat(Function, Iterable)
	 * @see #concat(String, Function, Object...)
	 */
	@SafeVarargs
	public static <T> String concat(String binder, T...array) {
		Objects.requireNonNull(binder);
		Objects.requireNonNull(array);

		if (array.length > 0) {
			OutString string = OutString.init(array[0] != null ? array[0].toString() : NULL);
			for (int i = 1; i < array.length; i++) {
				string.append(binder);
				string.append(array[i] != null ? array[i].toString() : NULL);
			}
			return string.get();
		} else {
			return "";
		}
	}

	public static class Array {
		public static <T> T next(Class<T> returned, Object[] array, OutInteger startIndex, T defaultValue) {
			for (; startIndex.isLess(array.length); startIndex.increment()) {
				int index = startIndex.get();
				Object output = array[index];
				if (output != null && returned.isInstance(output)) {
					return returned.cast(output);
				}
			}
			return defaultValue;
		}
	}

	public static class TreeMapHandler<T> {

		public class TreeMapPath<S> {

			private String[] keys;
			private Object root;
			private ObservableMap<String, Object> parent;

			@SuppressWarnings("unchecked")
			public TreeMapPath(String[] keys) {
				this.keys = keys;
				this.parent = (ObservableMap<String, Object>) tree;
				this.root = tree;
			}

			private String lastKey() {
				return this.keys[(this.keys.length - 1)];
			}

			private ObservableMap<String, Object> getParent() {
				return this.parent;
			}

			@SuppressWarnings("unchecked")
			private Object getRoot() {
				for (int i = 0; i < keys.length; i++) {
					parent = (ObservableMap<String, Object>) root;
					root = parent.get(keys[i]);
					if (i >= 0 && i < (keys.length - 1) && root == null) {
						root = FXCollections.<String,Object>observableHashMap();
						parent.put(keys[i], root);
					}
				}
				return root;
			}

			/**
			 * Return last value stored in leaf
			 * @param value new value
			 * @return last stored value
			 */
			@SuppressWarnings("unchecked")
			public S value(S value) {
				Object finalRoot = getRoot();
				((ObservableMap<String, Object>) getParent()).put(lastKey(), value);
				return finalRoot != null ? (S) finalRoot : null;
			}

			@SuppressWarnings("unchecked")
			public void valueList(S...value) {
				Object finalRoot = getRoot();
				if (finalRoot == null) {
					ObservableList<S> list = FXCollections.observableArrayList();
					list.addAll(value);
					getParent().put(lastKey(), list);
				}
			}

			public void valueList(java.util.Collection<S> value) {
				Object finalRoot = getRoot();
				if (finalRoot == null) {
					ObservableList<S> list = FXCollections.observableArrayList();
					list.addAll(value);
					getParent().put(lastKey(), list);
				}
			}

		}

		private Class<T> leaves;
		private ObservableMap<String, ?> tree;

		public TreeMapHandler(Class<T> leaves, ObservableMap<String, ?> tree) {
			this.leaves = leaves;
			this.tree = tree;
		}

		public static <T> TreeMapHandler<T> handle(Class<T> leaves, ObservableMap<String, ?> tree) {
			return new TreeMapHandler<>(leaves, tree);
		}

		public TreeMapPath<T> put(String...keys) {
			return new TreeMapPath<T>(keys);
		}

		public T get(String...keys) {
			Object root = new TreeMapPath<T>(keys).getRoot();

			if (leaves.isInstance(root)) {
				return leaves.cast(root);
			} else if (root == null) {
				return null;
			} else {
				throw new InconsistentTreeException();
			}
		}

		@SuppressWarnings("unchecked")
		public ObservableList<T> getList(String...keys) {
			TreeMapPath<T> treePath = new TreeMapPath<>(keys);
			Object root = treePath.getRoot();

			if (root != null) {
				return (ObservableList<T>) root;
			} else {
				ObservableList<T> list = FXCollections.observableArrayList();
				treePath.getParent().put(treePath.lastKey(), list);
				return list;
			}
		}

		@SuppressWarnings("rawtypes")
		public boolean hasLeaf(String...keys) {
			Object leaf = new TreeMapPath<T>(keys).getRoot();
			return ( leaves.isInstance(leaf) )
			||	(  java.util.List.class.isInstance(leaf) && !((java.util.List) leaf).isEmpty()	);
		}

		@SuppressWarnings("unchecked")
		public T getOrDefault(Supplier<T> constructor, String...keys) {
			TreeMapPath<T> treePath = new TreeMapPath<>(keys);
			Object root = treePath.getRoot();

			if (leaves.isInstance(root)) {
				return leaves.cast(root);
			} else if (root == null) {
				root = constructor.get();
				treePath.getParent().put(treePath.lastKey(), root);
				return (T) root;
			} else {
				throw new InconsistentTreeException(leaves, root.getClass());
			}
		}

	}

	public static <T> T[] subArray(int start, T[] array, IntFunction<T[]> arrayConstructor) {
		return subArray(start, array.length - 1, array, arrayConstructor);
	}

	public static <T> T[] subArray(int start, int end, T[] array, IntFunction<T[]> arrayConstructor) {
		T[] newArray = arrayConstructor.apply(end - start + 1);
		System.arraycopy(array, start, newArray, 0, end - start + 1);
		return newArray;
	}
}
