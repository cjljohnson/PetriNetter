package com.cjljohnson.PetriNetter.reachability;

import java.awt.GridLayout;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.w3c.dom.Element;

import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Transition;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class ReachabilityGraph extends mxGraph{
	
	static {
		UIManager.put( "ToolTip.border", BorderFactory.createCompoundBorder( UIManager.getBorder( "ToolTip.border" ), BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) ) );
	}

	private Map<Map<String, Integer>, mxCell> nodeMap;
	private Map<String, Map<String, Integer>> markingMap;
	private int size;
	private PetriGraph graph;
	int i;
	private Set<Object> liveSet;
	private boolean isComplete;
	private Map<String, Integer> boundedness;
	
	public ReachabilityGraph(PetriGraph graph, int size) {
		markingMap = new HashMap<String, Map<String, Integer>>();
		nodeMap = new HashMap<Map<String, Integer>, mxCell>();
		this.size = size;
		this.graph = graph;
		liveSet = new HashSet<Object>();
		isComplete = false;
		boundedness = new TreeMap<String, Integer>();
		
		
		initStyles();
		
		setCellsEditable(false);
		setCellsResizable(false);
		setSplitEnabled(false);
		
		
		
		calcReachability();
		
		//showLive();
		//showBounded();
	}
	
	public void showLive() {
		String message = "The following transitions are live:\n";
		
		for (Object vertex : liveSet) {
			message += " t" + graph.getCellMarkingName(vertex);
			
		}
		message += "\nThe following transitions are dead:\n";
		
		for (Object vertex : graph.getChildVertices(graph.getDefaultParent())) {
			if (vertex instanceof mxCell) {
				Object value = ((mxCell) vertex).getValue();
				if (value instanceof Transition && !liveSet.contains(vertex)) {
					message += " t" + graph.getCellMarkingName(vertex);
				}
			}
		}
		
		if (!isComplete) {
			message += "\nNOTE: Reachability graph was terminated after " 
					+ i + " markings were assessed. Some dead markings may be live.";
		}
	                		
		
		JOptionPane.showMessageDialog(null, message);
	}
	
	public void showBounded() {
		String message = "Boundedness for all places:\n";
		for (Map<String, Integer> map : nodeMap.keySet()) {
			for (String id : map.keySet()) {
				int val = map.get(id);
				boundedness.put(id, Math.max(boundedness.getOrDefault(id, 0), val));
			}
		}
		
		for (String id : boundedness.keySet()) {
			message += " p" + 
					graph.getCellMarkingName(((mxGraphModel)graph.getModel()).getCell(id)) 
					+ ": " + boundedness.get(id) + "\n";
		}
		
		if (!isComplete) {
			message += "\nNOTE: Reachability graph was terminated after " 
					+ i + " markings were assessed. Values given are only a lower bound.";
		}
		JOptionPane.showMessageDialog(null, message);
	}
	
	public boolean isCellSelectable(Object cell)
	{
	    if (getModel().isEdge(cell))
	    {
	        return false;
	    }

	    return super.isCellSelectable(cell);
	}
	
	
	public void calcReachability() {
		Queue<Map<String, Integer>> queue = new ArrayDeque<Map<String, Integer>>();
		i = 0;
		
		Map<String, Integer> s1 = graph.getPlaceTokens();
		queue.add(s1);
		
		try {
			getModel().beginUpdate();
			
			mxCell node = (mxCell)insertVertex(getDefaultParent(), null, "M0", i * 50, i * 50,
					40, 40, "NODE");
			
			markingMap.put("M0", s1);
			nodeMap.put(s1, node);
		
		
			while (queue.size() > 0 && i < size) {
				calcNodeReachability(queue.poll(), queue);
				i++;
			}
			setCellStyle(node.getStyle() + ";INITIAL", new Object[] {node});
			graph.setPlaceTokens(s1);
			setCellStyle(node.getStyle() + ";CURRENT", new Object[] {node});
		} finally {
			getModel().endUpdate();
		}
		if (queue.isEmpty()) {
			isComplete = true;
		}
	}
	
	private void calcNodeReachability(Map<String, Integer> state, Queue<Map<String, Integer>> queue) {
		graph.setPlaceTokens(state);
		mxCell node1 = nodeMap.get(state);
		int j = 0;
		for (Object vertex : graph.getChildVertices(graph.getDefaultParent())) {
			if (vertex instanceof mxCell) {
				Object value = ((mxCell) vertex).getValue();
				if (value instanceof Transition) {
	                if (graph.isFirable(vertex)) {
	                	graph.fireTransition(vertex);
	                	Map<String, Integer> newState = graph.getPlaceTokens();
	                	mxCell node = nodeMap.get(newState);
	                	if (node == null) {
	                		String markingName = "M" + markingMap.size();
	                		node = (mxCell)insertVertex(getDefaultParent(), null, markingName, i * 50, j * 50,
	            					40, 40, "NODE");
	                		markingMap.put(markingName, newState);
	                		nodeMap.put(newState, node);
	                		queue.add(newState);
	                	}
	                	Object[] edges = graph.getEdgesBetween(node1, node, true);
	                	if (edges.length > 0) {
	                		mxCell edge = (mxCell) edges[0];
	                		String label = (String) edge.getValue();
	                		label += ", t" + graph.getCellMarkingName(vertex);
	                		edge.setValue(label);
	                	} else {
	                		insertEdge(getDefaultParent(), null, "t" + graph.getCellMarkingName(vertex), node1, node, "");
	                	}
	                	
	                	
	                	graph.setPlaceTokens(state);
	                	j++;
	                	
	                	// Add to liveSet
	                	liveSet.add(vertex);
	                }
				}
			}
		}
		setCellStyle(node1.getStyle() + ";COMPLETE", new Object[] {node1});
	}
	
	private void initStyles() {
		mxStylesheet stylesheet = getStylesheet();
//		Hashtable<String, Object> nodeStyle = new Hashtable<String, Object>();
//		nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
//		nodeStyle.put(mxConstants.STYLE_OPACITY, 100);
//		nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
//		nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
//		nodeStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
//		nodeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);
//		stylesheet.putCellStyle("NODE", nodeStyle);
		
		Hashtable<String, Object> nodeStyle = new Hashtable<String, Object>();
		nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		nodeStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		nodeStyle.put(mxConstants.STYLE_OPACITY, 100);
		nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		nodeStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
		nodeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);
		stylesheet.putCellStyle("NODE", nodeStyle);
		
		Hashtable<String, Object> completeStyle = new Hashtable<String, Object>();
		completeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheet.putCellStyle("COMPLETE", completeStyle);
		
		Hashtable<String, Object> currentStyle = new Hashtable<String, Object>();
        //currentStyle.put(mxConstants.STYLE_FILLCOLOR, "#97b9ef");
        currentStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
        stylesheet.putCellStyle("CURRENT", currentStyle);
        
        Hashtable<String, Object> initialStyle = new Hashtable<String, Object>();
        initialStyle.put(mxConstants.STYLE_FILLCOLOR, "#00FF00");
        //initialStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
        stylesheet.putCellStyle("INITIAL", initialStyle);
		
		Map<String, Object> edge = new HashMap<String, Object>();
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_STROKEWIDTH, 2);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");
	    edge.put(mxConstants.STYLE_EDGE, "PETRI_STYLE");
		getStylesheet().setDefaultEdgeStyle(edge);
	}
	
	public void setActiveState(Object obj) {
	    if (obj instanceof mxCell) {
	        mxCell vertex = (mxCell)obj;
	        Map<String, Integer> map = getMarkingMap(obj);
	        if (map != null) {
	            Map<String, Integer> currentState = graph.getPlaceTokens();
	            mxCell currentCell = nodeMap.get(currentState);
	            String style = currentCell.getStyle().replaceFirst(";CURRENT", "");
	            setCellStyle(style, new Object[] {currentCell});
	            setCellStyle(vertex.getStyle() + ";CURRENT", new Object[] {vertex});
	            Map<String, Integer> state = map;
	            graph.setPlaceTokens(state);
	            graph.checkEnabledTransitions();
	            graph.refresh();
	            
	        }
	    }
	    
	}
	
	@Override
	public String convertValueToString(Object cell)
	{
//		if (cell instanceof mxCell)
//		{
//			Object value = ((mxCell) cell).getValue();
//
//			if (value instanceof Map<?,?>)
//			{	
//				Map<String, Integer> map = (Map<String, Integer>)value;
//				StringBuilder sb = new StringBuilder();
//				for (String id : map.keySet()) {
//					Object vertex = ((mxGraphModel)graph.getModel()).getCell(id);
//					sb.append('p');
//					sb.append(graph.getCellMarkingName(vertex));
//					sb.append(": ");
//					sb.append(map.get(id));
//					sb.append('\n');
//				}
//				return sb.toString();
//			}
//		}

		return super.convertValueToString(cell);
	}
	
	@Override
	public String getToolTipForCell(Object cell) {
		if (cell instanceof mxCell)
		{
			Map<String, Integer> map = getMarkingMap(cell);

			if (map != null)
			{	
				String marking = getMarkingName(cell);
				StringBuilder sb = new StringBuilder();
				sb.append("<html>");
				sb.append("<p style=\"font-weight: bold; font-size: 12px\">" + marking + "</p>");
				//sb.append("<div style=\"background-color: red\">");
				for (String id : map.keySet()) {
					Object vertex = ((mxGraphModel)graph.getModel()).getCell(id);
					sb.append("<p style=\"color: \">");
					sb.append("<span style=\"font-weight: bold\">p");
					sb.append(graph.getCellMarkingName(vertex));
					sb.append(": </span>");
					sb.append(map.get(id));
					sb.append("</p>");
				}
				sb.append("</html>");
				return sb.toString();
			}
		}
		return "";
	}
	
	public void findNodes() {
	    
	    Map<String, Integer> key = (Map<String, Integer>) nodeMap.keySet().toArray()[0];
	    
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    List<JTextField> values = new ArrayList<JTextField>();
	    mxGraphModel model = (mxGraphModel) graph.getModel();
	    panel.add(new JLabel("Define marking:"));
	    for (String id : key.keySet()) {
	        System.out.println(id);
	        
	        JTextField valField = new JTextField(5);
	        JPanel panel2 = new JPanel();
	        panel2.add(new JLabel("p" + graph.getCellMarkingName(model.getCell(id)) + ": "));
	        panel2.add(valField);
	        panel.add(panel2);
	        values.add(valField);
	        
	    }
	    int result = JOptionPane.showConfirmDialog(null, panel, "Find Marking",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    if (result == JOptionPane.OK_OPTION) {
	        Map<String, Integer> goal = new TreeMap<String, Integer>();
	        Iterator<JTextField> iter = values.iterator();
	        for (String id : key.keySet()) {
	            try {
	                int n = Integer.parseInt(iter.next().getText());
	                goal.put(id, n);
	            } catch (Exception e) {
                }
	        }
	        if (nodeMap.containsKey(goal)) {
	            setSelectionCell(nodeMap.get(goal));
	        } else {
	            JOptionPane.showMessageDialog(null, "Marking not in reachability graph.");
	        }
	    }
	    
	}
	
	public Map<String, Integer> getMarkingMap(Object cell) {
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof String)
			{	
				return markingMap.get(value);
			}
		}
		return null;
	}
	
	public String getMarkingName(Object cell) {
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof String)
			{	
				return (String)value;
			}
		}
		return null;
	}
}
