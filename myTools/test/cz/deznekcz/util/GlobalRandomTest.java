package cz.deznekcz.util;

import java.util.ArrayList;
import java.util.List;

import sun.applet.Main;

public class GlobalRandomTest {
	public static void main(String[] args) {
long lastRestore = 0L;
		
		ShareableRandom gr = ShareableRandom.from("initRandomSeed");
		lastRestore = gr.getRestore();
		
		List<Integer> getted = new ArrayList<>(7);
		
		Runnable[][] checks = new Runnable[][] {
			{
				() -> gr.setSeed("intRandomSeed"),
				Link.makeSupply(gr::nextInt),
				Link.makeSupply(gr::nextInt),
				Link.makeSupply(gr::nextInt),
				Link.makeSupply(gr::nextInt),
				Link.makeSupply(gr::nextInt),
				Link.makeSupply(gr::nextInt),
				Link.makeSupply(gr::nextInt)
			},
			{
				() -> gr.setSeed("longRandomSeed"),
				Link.makeSupply(gr::nextLong),
				Link.makeSupply(gr::nextLong),
				Link.makeSupply(gr::nextLong),
				Link.makeSupply(gr::nextLong),
				Link.makeSupply(gr::nextLong),
				Link.makeSupply(gr::nextLong),
				Link.makeSupply(gr::nextLong)
			},
			{
				() -> gr.setSeed("longRandomSeed"),
				Link.makeSupply(gr::nextDouble),
				Link.makeSupply(gr::nextDouble),
				Link.makeSupply(gr::nextDouble),
				Link.makeSupply(gr::nextDouble),
				Link.makeSupply(gr::nextDouble),
				Link.makeSupply(gr::nextDouble),
				Link.makeSupply(gr::nextDouble)
			},
			{
				() -> gr.setSeed("intRandomSeed"),
				Link.makeSupply(gr::nextInt).andThen(getted::add),
				Link.makeSupply(gr::nextInt).andThen(getted::add),
				Link.makeSupply(gr::nextInt).andThen(getted::add),
				Link.makeSupply(gr::nextInt).andThen(getted::add),
				Link.makeSupply(gr::nextInt).andThen(getted::add),
				Link.makeSupply(gr::nextInt).andThen(getted::add),
				Link.makeSupply(gr::nextInt).andThen(getted::add)
			},
			{
				() -> gr.setSeed("intRandomSeed"),
				//            checking function, value1 getter
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 0)),
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 1)),
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 2)),
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 3)),
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 4)),
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 5)),
				Link.predicted(Link::EQUALS, Link.makeSupply(gr::nextInt), Link.makeSupply(getted::get, () -> 6))
			}
		};
		
		for (int i = 0; i < checks.length; i++) {
			Runnable[] checksI = checks[i];
			for (int j = 0; j < checksI.length; j++) {
				checksI[j].run();
				if (lastRestore == gr.getRestore())
				{
					System.err.println("Test "+i+", "+j+" no change get");
				}
				else
				{
					System.out.println("Test "+i+", "+j+" complete");
				}
				lastRestore = gr.getRestore();
			}
		}
	}
}
