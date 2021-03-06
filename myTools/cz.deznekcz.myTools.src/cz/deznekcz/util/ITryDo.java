package cz.deznekcz.util;

import java.util.Random;

import javax.xml.xpath.XPathExpression;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.reference.Out;

/**
 *     Instances or lambda functions of {@link ITryDo} is usable to remove a
 * <br>&nbsp;<code>try {var = sometingWhatCauseException();} catch({@link Exception} e) {e.printStackTrace();}</code>
 * <br>and replace with
 * <br>&nbsp;<code>{@link Exception} e = (({@link ITryDo}) ()->outVar.set(sometingWhatCauseException())).{@link #doAction()};</code>
 * <br>can be use in condition clause with <code>tryDoinstance.doAction() != null</code> or more complex.
 * @author Zdenek Novotny (DeznekCZ)
 * @see #checkValue(CheckAction)
 */
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
		void get() throws Exception;
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
				castAction.get();
			}
		}.doAction();
	}

	@FunctionalInterface
	public interface CheckActionReturnable<A> {
		A get() throws Exception;
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
	static <A> Exception checkValue(CheckActionReturnable<A> castAction) {
		return new ITryDo() {
			@Override
			public void defineAction() throws Exception {
				castAction.get();
			}
		}.doAction();
	}
	/**
	 * Method returns null if any exception was excepted
	 * @param setAction supplier of new value
	 * @return new value
	 */
	static <A> A setValue(CheckActionReturnable<A> setAction) {
		Out<A> value = Out.init();
		Exception e = new ITryDo() {
			@Override
			public void defineAction() throws Exception {
				value.set(setAction.get());
			}
		}.doAction();
		if (e != null) e.printStackTrace();
		return value.get();
	}

	static void fxTask(boolean showError, CheckAction action) {
		Thread t = new Thread(() -> {
					try {
						action.get();
					} catch (Exception e) {
						if (showError) {
							Dialogs.EXCEPTION.show(e);
						} else {
							e.printStackTrace(System.err);
						}
					}
				}, String.format("ITryDo#%x", new Random().nextInt()));
		t.setDaemon(true);
		t.start();
	}
}
