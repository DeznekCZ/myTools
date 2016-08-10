package cz.deznekcz.tool;

/**
 *     New type of searching key in lang file.
 *     Is a better form for Object oriented programing.
 * <br>
 * <br>Default context is got from second declaring class.
 * <br>
 * <br><b>Example:</b> 
 * <br><code>public ContextClass {
 * <br>&nbsp;&nbsp;public enum group1 implements IContextedLangKey
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;{ KEY_1, KEY_2; }
 * <br>&nbsp;&nbsp;public enum group2 implements IContextedLangKey
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;{ KEY_3, KEY_2; } // KEY_2 is different
 * <br>}</code>
 * <br><b>Output language keys:</b>
 * <br>&nbsp;&nbsp;ContextClass.values1.KEY_1
 * <br>&nbsp;&nbsp;ContextClass.values1.KEY_2
 * <br>&nbsp;&nbsp;ContextClass.values2.KEY_2
 * <br>&nbsp;&nbsp;ContextClass.values2.KEY_3  
 * 
 * @author Zdenek Novotny (DeznekCZ)
 * @see ILangKey
 * @version Needs {@link Lang} version 4.0
 */
public interface IGroupLangKey extends IContextedLangKey {
	@Override
	default String contextName() {
		return getClass().getDeclaringClass().getSimpleName();
	}
}
