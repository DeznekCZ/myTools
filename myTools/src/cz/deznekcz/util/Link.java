package cz.deznekcz.util;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.util.Pair;

public class Link<A,T> implements Runnable, Supplier<T>, Consumer<A>, Predicate<A>, Function<A, T> {


	public static <A,T> Link<A,T> make(Function<A,T> function) {
		return new Link<>(function);
	}

	public static <A,T> Link<A,T> makeSupply(Function<A,T> function, Supplier<A> argumentSupplier) {
		return make((ingnoredV) -> function.apply(argumentSupplier.get()));
	}
	
	public static Link<Void,Void> makeRun(Runnable runnable) {
		return new Link<>((voidV) -> {runnable.run(); return null;});
	}

	public static <A> Link<A,Void> makeConsume(Consumer<A> consumer) {
		return new Link<>((consumed) -> {consumer.accept(consumed); return null;});
	}

	public static <A> Link<A,Boolean> makePredict(Predicate<A> predicate) {
		return new Link<>((predicated) -> predicate.test(predicated));
	}

	public static <A,T> Link<Void,T> makeSupply(Supplier<T> supplier) {
		return new Link<>((voidV) -> supplier.get());
	}

	private Function<A,T> function;
	
	public Link(Function<A, T> function) {
		this.function = function;
	}

	@Override
	public T apply(A a) {
		return function != null ? function.apply(a) : null;
	}

	@Override
	public void accept(A a) {
		if (function != null) function.apply(a);
	}

	@Override
	public T get() {
		return function != null ? function.apply(null) : null;
	}

	@Override
	public void run() {
		if (function != null) function.apply(null);
	}

	@Override
	public boolean test(A a) {
		return function != null && (boolean) function.apply(a);
	}

	@Override
	public Link<A,Boolean> and(Predicate<? super A> other) {
		return makePredict(((Predicate<A>) this::test).and(other));
	}
	
	@Override
	public Link<A,Boolean> or(Predicate<? super A> other) {
		return makePredict(((Predicate<A>) this::test).or(other));
	}
	
	@Override
	public Link<A,Void> andThen(Consumer<? super A> after) {
		return makeConsume(((Consumer<A>) this::accept).andThen(after));
	}
	
	@Override
	public <V> Link<A,V> andThen(Function<? super T, ? extends V> after) {
		return make(((Function<A,T>) this::apply).andThen(after));
	}

	public static <A> Link<A,Boolean> predicted(BiPredicate<A, A> predicator, Link<?, A> value1, Link<?, A> value2) {
		return make((ignoredV) -> predicator.test(value1.get(), value2.get()));
	}
	
	public static <A> boolean EQUALS(A v1, A v2) {
		return v1 != null && v1.equals(v2);
	}
}
