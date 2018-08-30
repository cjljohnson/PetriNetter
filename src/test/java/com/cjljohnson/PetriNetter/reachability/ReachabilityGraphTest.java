package com.cjljohnson.PetriNetter.reachability;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Place;
import com.mxgraph.model.mxCell;

public class ReachabilityGraphTest {
	PetriGraph graph;
	ReachabilityGraph reach;
	
	@Before
	public void setUp() throws Exception {
		graph = new PetriGraph();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddPlace() {
		Object cell = graph.addPlace(5, 20, 10, 10);
		Object[] places = graph.getPlaces();
		assertEquals(places.length,  1);
		Place place = (Place)((mxCell)places[0]).getValue();
		assertEquals(5, place.getTokens());
		assertEquals(20, place.getCapacity());
		
		
	}

}
