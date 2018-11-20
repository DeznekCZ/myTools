package cz.deznekcz.javafx.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;

public interface OnSetAction extends InvalidationListener {

    public static <A> void invalidated(final Observable observable, final Consumer<A> setter, final A value) {
        observable.addListener(new OnSetAction() {
            @Override
            public void invalidated(Observable observable) {
                setter.accept(value);
                observable.removeListener(this);
            }
        });
    }

    public static <A> void invalidated(final Observable observable, final Consumer<A> setter, final Supplier<A> generator) {
        observable.addListener(new OnSetAction() {
            @Override
            public void invalidated(Observable observable) {
                setter.accept(generator.get());
                observable.removeListener(this);
            }
        });
    }

    public static void invalidated(final Observable observable, final Runnable method) {
        observable.addListener(new OnSetAction() {
            @Override
            public void invalidated(Observable observable) {
                method.run();
                observable.removeListener(this);
            }
        });
    }

    public static <A> void invalidatedRepeatelly(Observable observable, final Consumer<A> setter, final A value) {
        observable.addListener(o -> setter.accept(value));
    }

    public static <A> void invalidatedRepeatelly(Observable observable, final Consumer<A> setter, final Supplier<A> generator) {
        observable.addListener(o -> setter.accept(generator.get()));
    }

    public static void invalidatedRepeatelly(Observable observable, final Runnable method) {
        observable.addListener(o -> method.run());
    }
}
