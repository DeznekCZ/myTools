package cz.deznekcz.util;

@FunctionalInterface
public interface ITryDo {

	/**
	 * Method returns 
	 * @return instance of {@link Exception}
	 * @see #checkValue(CheckAction)
	 */
	default Exception doAction(){
		try {
			defineAction();
			return null;
		} catch (Exception e) {
			return e;
		}
	}
	/**
	 * Method throws all cached exceptions
	 * @throws Exception instance of first cached exception
	 */
	void defineAction() throws Exception ;

	@FunctionalInterface
	public interface CheckAction {
		Object get();
	}
	
	/**
	 *     Method is usable to check casting classes.
	 * <br>
	 * <br><b>Using:</b>
	 * <br>
	 * <code>ITryDo.checkValue(()-&gt;(List&lt;T&gt;) object)
	 * <br>&nbsp; != null
	 * <br>&nbsp;&nbsp;? false
	 * <br>&nbsp;&nbsp;: (List&lt;T&gt;) object</code>
	 * @param castAction intance of {@link CheckAction} or lambda "()->(Integer) 5"
	 * @return instance of {@link Exception}
	 * @see #doAction()
	 */
	static Exception checkValue(CheckAction castAction) {
		return new ITryDo() {
			@Override
			public void defineAction() throws Exception {
				@SuppressWarnings("unused")
				Object noValue = castAction.get();
			}
		}.doAction();
	}
}
