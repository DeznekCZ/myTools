package cz.deznekcz.tool.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Information for programmer about arguments added in {@link ILangKey#value(Object[])}.
 * If is not used, automatically says NO ARGUMENTS.
 * <br>This annotation is visible in documentation with all variables.
 * 
 * <br>
 * <b>Use:</b>
 * <br>
 * <pre>
 * &nbsp;@Arguments(hints="size of array", types=Integer.class) SIZE 
 * &nbsp;&nbsp;&nbsp;&nbsp;Sample result="Array length is %d."
 * 
 * &nbsp;@Arguments(hints={"emailFrom","emailTo"}, types={String.class,String.class}) SENDING 
 * &nbsp;&nbsp;&nbsp;&nbsp;Sample result="Email is sending from %s to %s."
 * 
 * or without hints
 * 
 * &nbsp;@Arguments(types=Integer.class) KEY_WITH_INTEGER  
 * &nbsp;&nbsp;&nbsp;&nbsp;Sample result="%d of train(s) is(are) used to travel."
 * 
 * &nbsp;@Arguments(types={String.class,String.class}) KEY_WITH_TWO_STRINGS 
 * &nbsp;&nbsp;&nbsp;&nbsp;Sample result="Some %s of a %s"
 * </pre>
 * 
 * @author Zdenek Novotny (DeznekCZ)
 * @see ILangKey
 * @see ArgumentsTest
 */
@Target({ElementType.LOCAL_VARIABLE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Arguments {
	
	/**
	 * 
	 * @return array of applied arguments
	 */
	Class<?>[] types();
	/**
	 * 
	 * @return array of  arguments
	 */
	String[] hints() default {} ;
}
