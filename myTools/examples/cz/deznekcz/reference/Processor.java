package cz.deznekcz.reference;

import java.util.HashMap;
import java.util.Scanner;

import cz.deznekcz.reference.Out;

public class Processor {
	private static final int STORE_INT = 1;
	private static final int LOAD_INT = 2;
	private static final int PRINT_INT = 5;
	private static final int READ_INT = 6;
	/* Registers */
	public final Out<Integer> NULL_INT = Out.init(0);
	public final Out<Long> NULL_LONG = Out.init(0L);
	
	public final Out<Integer> X1 = Out.init();
	public final Out<Integer> X2 = Out.init();
	public final Out<Long>    L1 = Out.init(); 
	public final Out<Long>    L2 = Out.init();
	public final Out<Integer> T1 = Out.init();
	public final Out<Integer> T2 = Out.init();
	public final Out<Integer> T3 = Out.init();
	
	/* Is not a known type of memory. */
	/* <ADDRESS64,WORD64>             */
	public HashMap<Long, Long> data = new HashMap<>();
	
	/** systemInterrupt for X1:
	 * 1 - store integer value from X2 to address L1
	 * 2 - load integer value from address L1 to X1
	 * 5 - print int from register X2
	 * 6 - read int to register X1
	 */
	public void systemInterrupt() {
		switch ((int) X1.get()) {
		case STORE_INT: data.replace(L1.get(), Integer.toUnsignedLong(X2.get())); break;
		case LOAD_INT: X1.set((int) (0xFFFFFFFF & data.get(L1.get()))); break;
		case PRINT_INT: System.out.println(X2.get());  break;
		case READ_INT: X1.set(new Scanner(System.in).nextInt()); break;
		default:
			break;
		}
	}

	public <T> void set(Out<T> register, T value) {
		register.set(value);
	}
	
	public void add(Out<Integer> result, Out<Integer> operand1, Out<Integer> operand2) {
		result.set((operand1.get() + operand2.get()));
	}
	
	public void addL(Out<Long> result, Out<Long> operand1, Out<Long> operand2) {
		result.set((operand1.get() + operand2.get()));
	}
	
	public static void main(String[] args) {
		Processor p = new Processor();
		
		/* Initialize memory */
		p.data.put(0x264L, 2800L);
		p.data.put(0x272L, Integer.toUnsignedLong(-50));
		p.data.put(0x280L, Integer.toUnsignedLong(25));
		
		/* Actions */
		p.set(p.X1, LOAD_INT); // set value of register
		p.set(p.L1, 0x272L);
		p.systemInterrupt(); // load integer
		p.add(p.T1, p.X1, p.NULL_INT); // move from X1 to T4

		p.set(p.X1, LOAD_INT);
		p.set(p.L1, 0x280L);
		p.systemInterrupt(); // load integer
		p.add(p.X2, p.T1, p.X1); // sum of T4 and X1 to X2
		
		p.set(p.X1, PRINT_INT);
		// X2 have desired number to print
		p.systemInterrupt(); // print number
		
		p.set(p.X1, READ_INT);
		p.systemInterrupt(); // reads integer
		
		p.add(p.X2, p.X1, p.X2);
		p.set(p.X1, PRINT_INT);
		p.systemInterrupt();
		
		/* SEE REGISTERS */
//		System.out.println(p.X1);
	}

}
