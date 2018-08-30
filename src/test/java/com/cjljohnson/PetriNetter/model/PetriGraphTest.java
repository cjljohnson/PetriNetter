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
		Object cell = graph.addPlace(5, 20, 10, 10);
		Object[] places = graph.getPlaces();
		assertEquals(places.length,  1);
		Place place = (Place)((mxCell)places[0]).getValue();
		assertEquals(5, place.getTokens());
		assertEquals(20, place.getCapacity());
	}
	
	
	@Test
	public void testAddTransitions() {
		graph.addTransition(20, 30);
		Object[] transitions = graph.getTransitions();
		assertEquals(1, transitions.length);
		Transition transition = (Transition)((mxCell)transitions[0]).getValue();
		assertNotNull(transition);
	}
	
	@Test
	public void testAddArc() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object newEdge = graph.createEdge(graph.getDefaultParent(), null, null, place, transition, null);
		graph.addEdge(newEdge, graph.getDefaultParent(), place, transition, null);
		Object[] edges = graph.getAllEdges(new Object[]{place});
		assertEquals(1, edges.length);
		mxCell edge = (mxCell)edges[0];
		assertEquals(edge.getSource(), place);
		assertEquals(edge.getTarget(), transition);		
	}
	
	@Test
	public void testAddArcNullEnd() {
		graph.setAllowDanglingEdges(false);
		Object place = graph.addPlace(5, 20, 10, 10);
		
		Object edge = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, null);
		assertEquals("", graph.getEdgeValidationError(edge, place, null));
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", null, place);
		assertEquals("", graph.getEdgeValidationError(edge2, null, place));
	}
	
	@Test
	public void testAddArcPlaceToPlace() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object place2 = graph.addPlace(5, 20, 10, 10);
		Object edge = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, place2);
		assertEquals("Places can only be connected to transitions.", graph.getEdgeValidationError(edge, place, place2));
	}
	
	@Test
	public void testAddArcTransitionToTransition() {
		Object transition = graph.addTransition(20, 30);
		Object transition2 = graph.addTransition(20, 30);
		Object edge = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, transition2);
		assertEquals("Transitions can only be connected to places.", graph.getEdgeValidationError(edge, transition, transition2));
	}
	
	@Test
	public void testPlaceConstraints() {
		mxCell cell = (mxCell)graph.addPlace(5, 20, 10, 10);
		Object[] places = graph.getPlaces();
		assertEquals(places.length,  1);
		Place place = (Place)((mxCell)places[0]).getValue();
		assertEquals(5, place.getTokens());
		assertEquals(20, place.getCapacity());
		
		
		graph.setTokens(cell, 0);
		assertEquals(0, ((Place)cell.getValue()).getTokens());
		graph.setTokens(cell, -1);
		assertNotEquals(-1, ((Place)cell.getValue()).getTokens());
		graph.setTokens(cell, 1);
		assertEquals(1, ((Place)cell.getValue()).getTokens());
		
		graph.setTokens(cell, 20);
		assertEquals(20, ((Place)cell.getValue()).getTokens());
		graph.setTokens(cell, 21);
		assertEquals(20, ((Place)cell.getValue()).getTokens());
		
		graph.setTokens(cell, 20);
		assertEquals(20, ((Place)cell.getValue()).getTokens());
		graph.setCapacity(cell, 21);
		assertEquals(21, ((Place)cell.getValue()).getCapacity());
		graph.setCapacity(cell, 20);
		assertEquals(20, ((Place)cell.getValue()).getCapacity());
		graph.setCapacity(cell, 19);
		assertEquals(20, ((Place)cell.getValue()).getCapacity());
		graph.setTokens(cell, 0);
		graph.setCapacity(cell, 0);
		assertEquals(20, ((Place)cell.getValue()).getCapacity());
		
		graph.setCapacity(cell, -1);
		assertEquals(-1, ((Place)cell.getValue()).getCapacity());
		graph.setTokens(cell, 30);
		assertEquals(30, ((Place)cell.getValue()).getTokens());
		
		graph.setCapacity(cell, -2);
		assertEquals(-1, ((Place)cell.getValue()).getCapacity());
	}
	
	@Test
	public void testArcConstraints() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object newEdge = graph.createEdge(graph.getDefaultParent(), null, null, place, transition, null);
		graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object[] edges = graph.getAllEdges(new Object[]{place});
		assertEquals(1, edges.length);
		mxCell edge = (mxCell)edges[0];
		Arc arc = (Arc)edge.getValue();
		assertEquals(1, arc.getWeight());
		graph.setArcWeight(edge, 2);
		assertEquals(2, ((Arc)edge.getValue()).getWeight());
		graph.setArcWeight(edge, 0);
		assertEquals(2, ((Arc)edge.getValue()).getWeight());
		graph.setArcWeight(edge, -1);
		assertEquals(2, ((Arc)edge.getValue()).getWeight());
	}
	
	@Test
	public void testStandardFiring() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object place2 = graph.addPlace(1, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place2);

		// Fire once
		graph.setArcWeight(edge1, 2);
		graph.setArcWeight(edge2, 3);
		assertTrue(graph.isFirable(transition));
		graph.fireTransition(transition);
		assertEquals(3, graph.getTokens(place));
		assertEquals(4, graph.getTokens(place2));
		
		// Can't fire when pre-condition not met
		assertTrue(graph.fireTransition(transition));
		assertFalse(graph.fireTransition(transition));
		assertEquals(1, graph.getTokens(place));
		assertEquals(7, graph.getTokens(place2));
		
		// Can't fire when post-condition not met
		graph.setTokens(place, 5);
		graph.setTokens(place2, 1);
		graph.setCapacity(place2, 1);
		assertFalse(graph.isFirable(transition));
		assertFalse(graph.fireTransition(transition));
		assertEquals(5, graph.getTokens(place));
		assertEquals(1, graph.getTokens(place2));
	}
	
	@Test
	public void testSelfLoopFiring() {
		Object place = graph.addPlace(1, 2, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place);

		// Can fire when there is tolerance
		assertTrue(graph.isFirable(transition));
		
		// Can't fire when no tolerance
		graph.setCapacity(place, 1);
		assertFalse(graph.isFirable(transition));
	}
	
	@Test
	public void testInfiniteCapacityFiring() {
		Object place = graph.addPlace(0, -1, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place);

		// Fire once
		assertTrue(graph.isFirable(transition));
		graph.fireTransition(transition);
		assertEquals(1, graph.getTokens(place));
	}
	
	@Test
	public void testNoPostConditionFiring() {
		Object place = graph.addPlace(1, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);

		assertTrue(graph.isFirable(transition));
		graph.fireTransition(transition);
		assertEquals(0, graph.getTokens(place));
		assertFalse(graph.isFirable(transition));
	}
	
	@Test
	public void testIsolatedTransitionFiring() {
		Object transition = graph.addTransition(20, 30);

		assertFalse(graph.isFirable(transition));
	}
	
}
