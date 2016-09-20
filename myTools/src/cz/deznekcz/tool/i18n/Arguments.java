package cz.deznekcz.tool.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Information for programmer about arguments added in {@link ILangKey#value(Object[])}.
 * If is not used, automatically says NO ARGUMENTS.
 * @author Zdenek Novotny (DeznekCZ)
 * @see ILangKey
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Arguments {

	/**
	 * 
	 * @return array of applied arguments
	 */
	Class<?>[] value();
}
