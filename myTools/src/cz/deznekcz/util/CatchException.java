package cz.deznekcz.util;

@FunctionalInterface
public interface CatchException {

	/**
	 * Method returns 
	 * @return instance of {@link Exception}
	 * @see #tryDo(CheckAction)
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
	 * <code>CatchException.tryDo(()-&gt;(List&lt;T&gt;) object)
	 * <br>&nbsp; != null
	 * <br>&nbsp;&nbsp;? false
	 * <br>&nbsp;&nbsp;: (List&lt;T&gt;) object</code>
	 * @param castAction intance of {@link CheckAction} or lambda "()->(Integer) 5"
	 * @return instance of {@link Exception}
	 * @see #doAction()
	 */
	static Exception tryDo(CheckAction castAction) {
		return new CatchException() {
			@Override
			public void defineAction() throws Exception {
				@SuppressWarnings("unused")
				Object noValue = castAction.get();
			}
		}.doAction();
	}
}
