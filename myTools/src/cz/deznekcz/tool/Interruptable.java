package cz.deznekcz.tool;

/**
 * @author Zdenek Novotny (DeznekCZ)
 * Instances of class {@link Interruptable} represents
 * an interrupt-able {@link Runnable} instances.
 * </br>
 * <b>Example:</b><br>
 * Simple code with LAMBDA functions:
 * <pre>new Thread(
    Interruptable.run(
      () -> {
        while(true) {
          System.out.println("Something");
          Thread.sleep(100);
        }
      },
      (exception) -> {
        exception.printStackTrace();
      }
    )
  );</pre>
 * Simple code with explicit declaration:
 * <pre>new Thread(
    Interruptable.run(
      new RunCode() {
        &#64;Override void todo() throws InterruptedException {
          while(true) {
            System.out.println("Something");
            Thread.sleep(100);
          }
        }
      },
      new InterruptCode() {
        &#64;Override void todo(InterruptedException exception) {
          exception.printStackTrace();
        }
      }
    )
  );</pre>
  @version 2 (Handling all exceptions natively)
 */
public final class Interruptable implements Runnable {
	/** Represents code of run */
	private RunCode runCode;
	/** Represents code of exception */
	private ExceptionCode exceptionCode;

	/**
	 * Run code function
	 * @author Zdenek Novotny (DeznekCZ)
	 */
	@FunctionalInterface
	public static interface RunCode {
		/**
		 * Represent a code of thread
		 * @throws Exception throws while thread interrupting
		 */
		void todo() throws Exception ;
	}
	/**
	 * Catch code function
	 * @author Zdenek Novotny (DeznekCZ)
	 */
	@FunctionalInterface
	public static interface ExceptionCode {
		/**
		 * Handles an {@link Exception} of thread
		 * @param e instance of handled {@link Exception}
		 */
		void todo(Exception e);
	}
	
	/**
	 * Constructor of {@link Interruptable} class implements an interface {@link Runnable}
	 * @param runCode instance of {@link RunCode} or lambda <code>() -> { CODE }</code> }
	 * @param exceptionCode instance of {@link ExceptionCode} or lambda <code>(exceptionVar) -> { CODE }</code> }
	 */
	public Interruptable(RunCode runCode, ExceptionCode exceptionCode) {
		this.runCode = runCode;
		this.exceptionCode = exceptionCode;
	}

	/**
	 * Factory method for instances of {@link Interruptable}
	 * @param runCode instance of {@link RunCode} or lambda <code>() -> { CODE }</code> }
	 * @param exceptionCode instance of {@link ExceptionCode} or lambda <code>(exceptionVar) -> { CODE }</code> }
	 * @return instance of {@link Runnable}
	 */
	public static final Interruptable run(RunCode runnable, ExceptionCode exceptionCode) {
		return new Interruptable(runnable, exceptionCode);
	}

	/**
	 * @see #Interruptable(RunCode, ExceptionCode)
	 * @see #run(RunCode, ExceptionCode)
	 */
	@Override
	public final void run() {
		try {
			runCode.todo();
		} catch (Exception e) {
			exceptionCode.todo(e);
		}
	}
	
	public Thread start() {
		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}
}
