package cz.deznekcz.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 
 * @author Zdenek Novotny (DeznekCZ)
 * @see ILangKey
 */
public @interface Arguments {

	/**
	 * 
	 * @return array of applied arguments
	 */
	Class<?>[] value();
}
