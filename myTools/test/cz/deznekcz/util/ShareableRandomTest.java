package cz.deznekcz.util;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;

import cz.deznekcz.reference.OutArrays;

public class ShareableRandomTest {
	
	@Test
	public void testSeed() {
		long lastRestore = 0L;
		
		ShareableRandom gr = ShareableRandom.from("initRandomSeed");
		lastRestore = gr.getRestore();
		
		ArrayList<Integer> getted = new ArrayList<>(7);
		
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
				assertNotEquals("Retore number was same", lastRestore, gr.getRestore());
				lastRestore = gr.getRestore();
			}
		}
	}
	
	final static int LIMIT = 100000;
	
	@Test
	public void testRestoreSeedVariablility() throws Exception {
		
		ShareableRandom gr = ShareableRandom.from("initRandomSeed");
		long loopLast = gr.getRestore();
		boolean breaked = false;
		
		ArrayList<Long> list = new ArrayList<>(20);
		HashMap<Integer, Integer> randomCounter1 = new HashMap<>();
		
	    do {
	    	list.add(loopLast);
	    	
	    	if (list.size() == LIMIT)
	    	{
	    		breaked = true;
	    		break;
	    	}
	    	
			int cur = gr.nextInt();
			
			for(int i = 0; i < Long.numberOfLeadingZeros((long)loopLast); i++) {
			      System.out.print('0');
			}
			System.out.println(Long.toBinaryString((long)loopLast) + " : " + list.size());
			
			randomCounter1.put(cur, 1 + randomCounter1.getOrDefault(cur, 0));
		} while(!list.contains((loopLast = gr.getRestore())));
	    
	    System.out.println("Variability (list 1) = \"" + list.size() + "\"" + (breaked ? " limit reached" : ""));
	    
	    gr = ShareableRandom.from("initRandomSeed");
		loopLast = gr.getRestore();
	    
	    ArrayList<Long> list2 = new ArrayList<>(20);
		HashMap<Integer, Integer> randomCounter2 = new HashMap<>();
		
	    do {
	    	list2.add(loopLast);
	    	
	    	if (breaked && list2.size() == LIMIT)
	    	{
	    		break;
	    	}
	    	
			int cur = gr.nextInt();
			randomCounter2.put(cur, 1 + randomCounter2.getOrDefault(cur, 0));
		} while(!list2.contains((loopLast = gr.getRestore())));

	    System.out.println("Variability (list 2) = \"" + list2.size() + "\"" + (breaked ? " limit reached" : ""));
	    
	    assertEquals("Generation was not same!",
	    	    randomCounter1.toString(),
	    	    randomCounter2.toString()
	    	    );
	}
	
	@Test
	public void testInitialSeeds() throws Exception {
		
		Random r = new Random();
		
		long[] seeds = r.longs(200L).toArray();
		
		for (int seed = 0; seed < seeds.length; seed++) {
			ShareableRandom gr = ShareableRandom.from(seeds[seed]);
			long loopLast = gr.getRestore();
			boolean breaked = false;
			
			ArrayList<Long> list = new ArrayList<>(20);
			HashMap<Integer, Integer> randomCounter1 = new HashMap<>();
			
			System.out.format("Seed id %3d = ", seed);
			for(int i = 0; i < Long.numberOfLeadingZeros((long)seeds[seed]); i++) {
			      System.out.print('0');
			}
			System.out.println(Long.toBinaryString((long)seeds[seed]));
			
			do {
		    	list.add(loopLast);
		    	
		    	if (list.size() == LIMIT)
		    	{
		    		breaked = true;
		    		break;
		    	}
		    	
				int cur = gr.nextInt();
				
				randomCounter1.put(cur, 1 + randomCounter1.getOrDefault(cur, 0));
			} while(!list.contains((loopLast = gr.getRestore())));
			
			System.out.println("Variability = \"" + list.size() + "\"" + (breaked ? " limit reached" : ""));
		}
	}
}
