package cz.deznekcz.reference;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * {@link PredictionAble} methods is only allowed in another case is needed to create own {@link Predicate} instance or lambda.
 * @author Zdenek Novotny (DeznekCZ)
 * @see Out#bindChecked(Predicate, Object)
 */
@Retention(SOURCE)
@Target(METHOD)
public @interface PredictionAble {

}
