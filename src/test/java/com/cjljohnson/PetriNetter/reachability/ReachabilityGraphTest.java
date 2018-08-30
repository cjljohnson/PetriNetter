package com.cjljohnson.PetriNetter.reachability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
	public void testBasicReach() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object place2 = graph.addPlace(1, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place2);

		reach = new ReachabilityGraph(graph, 200);
		assertEquals(6, reach.getChildVertices(reach.getDefaultParent()).length);
		Object[] nodes = reach.getChildVertices(reach.getDefaultParent());
		mxCell m0 = (mxCell)nodes[0];
		assertEquals("M0", m0.getValue());
		Object[] edges = reach.getEdges(m0);
		assertEquals(1, edges.length);
		mxCell edge = (mxCell)edges[0];
		assertEquals("t1", edge.getValue());
		mxCell m1 = (mxCell)edge.getTarget();
		assertEquals("M1", m1.getValue());
	}
	
	@Test
	public void testSharedEdgeReach() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object place2 = graph.addPlace(1, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object transition2 = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place2);
		Object edge3 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition2);
		Object edge4 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition2, place2);

		reach = new ReachabilityGraph(graph, 200);
		assertEquals(6, reach.getChildVertices(reach.getDefaultParent()).length);
		Object[] nodes = reach.getChildVertices(reach.getDefaultParent());
		mxCell m0 = (mxCell)nodes[0];
		assertEquals("M0", m0.getValue());
		Object[] edges = reach.getEdges(m0);
		assertEquals(1, edges.length);
		mxCell edge = (mxCell)edges[0];
		assertEquals("t1, t2", edge.getValue());
		mxCell m1 = (mxCell)edge.getTarget();
		assertEquals("M1", m1.getValue());
	}
	
	@Test
	public void testParallelEdgesReach() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object place2 = graph.addPlace(0, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object transition2 = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place2);
		Object edge3 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place2, transition2);
		Object edge4 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition2, place);

		reach = new ReachabilityGraph(graph, 200);
		assertEquals(6, reach.getChildVertices(reach.getDefaultParent()).length);
		Object[] nodes = reach.getChildVertices(reach.getDefaultParent());
		mxCell m0 = (mxCell)nodes[0];
		assertEquals("M0", m0.getValue());
		Object[] edges = reach.getEdges(m0);
		assertEquals(2, edges.length);
		mxCell edgeA = (mxCell)edges[0];
		assertEquals("t1", edgeA.getValue());
		mxCell m1 = (mxCell)edgeA.getTarget();
		assertEquals("M1", m1.getValue());
		mxCell edgeB = (mxCell)edges[1];
		assertEquals(m0, edgeB.getTarget());
		assertEquals(m1, edgeB.getSource());
		assertEquals("t2", edgeB.getValue());
	}
	
	@Test
	public void testSelfLoopsReach() {
		Object place = graph.addPlace(5, 20, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", place, transition);
		Object edge2 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place);

		reach = new ReachabilityGraph(graph, 200);
		assertEquals(1, reach.getChildVertices(reach.getDefaultParent()).length);
		Object[] nodes = reach.getChildVertices(reach.getDefaultParent());
		mxCell m0 = (mxCell)nodes[0];
		assertEquals("M0", m0.getValue());
		Object[] edges = reach.getEdges(m0);
		assertEquals(1, edges.length);
		mxCell edge = (mxCell)edges[0];
		assertEquals("t1", edge.getValue());
		assertEquals(m0, edge.getTarget());
		assertEquals(m0, edge.getSource());
	}
	
	@Test
	public void testInfiniteMarkingSetTerminates() {
		Object place = graph.addPlace(0, -1, 10, 10);
		Object transition = graph.addTransition(20, 30);
		Object edge1 = graph.insertEdge(graph.getDefaultParent(), null, "ARC", transition, place);

		reach = new ReachabilityGraph(graph, 200);
		assertEquals(201, reach.getChildVertices(reach.getDefaultParent()).length);
	}

}
