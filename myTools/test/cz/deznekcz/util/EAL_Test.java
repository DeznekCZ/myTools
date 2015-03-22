package cz.deznekcz.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;

/**
 * {@link EqualArrayList} test case for {@link JUnit4}
 * @author Zdeněk Novotný (DeznekCZ)
 *
 */
public class EAL_Test {

	private EqualArrayList<EAL_Integer> list;

	@Before
	public void setUp() throws Exception {
		list = new EqualArrayList<EAL_Integer>();
		list.add(new EAL_Integer());
		list.add(new EAL_Integer(1));
		list.add(new EAL_Integer("8"));
		list.add(new EAL_Integer(3));
	}

	@Test
	public void testInteger() {
		assertTrue(list.contains(8));
	}

	@Test
	public void testSameClass() {
		assertTrue(list.contains(new EAL_Integer(0)));
	}

	@Test
	public void testString() {
		assertTrue(list.contains("1"));
	}

}
