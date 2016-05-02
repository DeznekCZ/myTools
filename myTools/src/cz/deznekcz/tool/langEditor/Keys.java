package cz.deznekcz.tool.langEditor;

import cz.deznekcz.tool.IContextedLangKey;

public class Keys {
	private static interface IKey extends IContextedLangKey {
		@Override
		default String contextName() {
			return "Lang";
		}
	}
	
	public static final String EXTENSION = "*.xlng";
	public static final String NO_EXTENSION = "*"; 
	
	public static enum IO implements IKey {
		FILTER, NO_FILTER, NOT_A_PROPERTY_FILE
	}
}
