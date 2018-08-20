package com.cjljohnson.PetriNetter.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mxgraph.model.mxCell;

public class PetriGraphTest {
	PetriGraph graph;
	
	@Before
	public void setUp() throws Exception {
		graph = new PetriGraph();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddPlace() {
		graph.addPlace(5, 20, 10, 10);
		Object[] places = graph.getPlaces();
		assertEquals(places.length,  1);
		Place place = (Place)((mxCell)places[0]).getValue();
		assertEquals(place.getTokens(), 5);
		assertEquals(place.getCapacity(), 20);
	}
	
}
