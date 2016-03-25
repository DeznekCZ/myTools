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
 */
public final class Interruptable implements Runnable {
	/** Represents code of run */
	private RunCode runCode;
	/** Represents code of exception */
	private InterruptCode interruptCode;

	/**
	 * Run code function
	 * @author Zdenek Novotny (DeznekCZ)
	 */
	@FunctionalInterface
	public static interface RunCode {
		/**
		 * Represent a code of thread
		 * @throws InterruptedException throws while thread interrupting
		 */
		void todo() throws InterruptedException ;
	}
	/**
	 * Catch code function
	 * @author Zdenek Novotny (DeznekCZ)
	 */
	@FunctionalInterface
	public static interface InterruptCode {
		/**
		 * Handles an {@link InterruptedException} of thread
		 * @param e instance of handled {@link InterruptedException}
		 */
		void todo(InterruptedException e);
	}
	
	/**
	 * Constructor of {@link Interruptable} class implements an interface {@link Runnable}
	 * @param runCode instance of {@link RunCode} or lambda <code>() -> { CODE }</code> }
	 * @param interruptCode instance of {@link InterruptCode} or lambda <code>(exceptionVar) -> { CODE }</code> }
	 */
	public Interruptable(RunCode runCode, InterruptCode interruptCode) {
		this.runCode = runCode;
		this.interruptCode = interruptCode;
	}

	/**
	 * Factory method for instances of {@link Interruptable}
	 * @param runCode instance of {@link RunCode} or lambda <code>() -> { CODE }</code> }
	 * @param interruptAction instance of {@link InterruptCode} or lambda <code>(exceptionVar) -> { CODE }</code> }
	 * @return instance of {@link Runnable}
	 */
	public static final Runnable run(RunCode runnable, InterruptCode interruptAction) {
		return new Interruptable(runnable, interruptAction);
	}

	/**
	 * @see #Interruptable(RunCode, InterruptCode)
	 * @see #run(RunCode, InterruptCode)
	 */
	@Override
	public final void run() {
		try {
			runCode.todo();
		} catch (InterruptedException e) {
			interruptCode.todo(e);
		}
	}
}
