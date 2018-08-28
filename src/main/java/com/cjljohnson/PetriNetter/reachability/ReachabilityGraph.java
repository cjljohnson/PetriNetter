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
	private PetriGraph graph2;
	int i;
	private Set<Object> liveSet;
	private boolean isComplete;
	private Map<String, Integer> boundedness;
	private mxCell currentCell;
	private List<String> deadList;
	
	public ReachabilityGraph(PetriGraph graph, int size) {
		markingMap = new TreeMap<String, Map<String, Integer>>();
		nodeMap = new HashMap<Map<String, Integer>, mxCell>();
		this.size = size;
		this.graph = graph;
		liveSet = new HashSet<Object>();
		deadList = new ArrayList<String>();
		isComplete = false;
		boundedness = new TreeMap<String, Integer>();
		
		
		this.graph2 = new PetriGraph();
		graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent())));

		
		
		initStyles();
		
		setCellsEditable(false);
		setCellsResizable(false);
		setSplitEnabled(false);
		setKeepEdgesInBackground(true);
		
		
		
		calcReachability();

	}
	
	public void showLive() {
		String message = "The following transitions are semi-live:\n";
		
		for (Object vertex : liveSet) {
			message += " t" + graph2.getCellMarkingName(vertex);
			
		}
		message += "\nThe following transitions are dead:\n";
		
		for (Object vertex : graph2.getChildVertices(graph2.getDefaultParent())) {
			if (vertex instanceof mxCell) {
				Object value = ((mxCell) vertex).getValue();
				if (value instanceof Transition && !liveSet.contains(vertex)) {
					message += " t" + graph2.getCellMarkingName(vertex);
				}
			}
		}
		
		if (!isComplete) {
			message += "\nNOTE: Reachability graph was terminated after " 
					+ i + " markings were assessed. Some dead markings may be live.";
		}
	                		
		
		JOptionPane.showMessageDialog(null, message);
	}

	public void showDeadlock() {
	    String message;

	    if (deadList.size() > 0) {

	        message = "The following transitions are in deadlock:\n";

	        for (String marking : deadList) {
	            message += " " + marking;

	        }
	    } else {
	        message = "No states of deadlock found in reachable markings.\n";
	    }

	    if (!isComplete) {
	        message += "\nNOTE: Reachability graph was terminated after " 
	                + i + " markings were assessed. There may be unexplored dead markings.";
	    }

        
        JOptionPane.showMessageDialog(null, message);
    }
	
	public void showBounded() {
		String message = "Place boundedness for all places:\n";
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
		
		Map<String, Integer> s1 = graph2.getPlaceTokens();
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
			System.out.println("YEE");
			graph2.setPlaceTokens(s1);
			graph2.refresh();
			setCurrentCell(node);
		} finally {
			getModel().endUpdate();
		}
		if (queue.isEmpty()) {
			isComplete = true;
		}
	}
	
	private void calcNodeReachability(Map<String, Integer> state, Queue<Map<String, Integer>> queue) {
		graph2.setPlaceTokens(state);
		mxCell node1 = nodeMap.get(state);
		int enabledCount = 0;
		int j = 0;
		for (Object vertex : graph2.getChildVertices(graph2.getDefaultParent())) {
			if (vertex instanceof mxCell) {
				Object value = ((mxCell) vertex).getValue();
				if (value instanceof Transition) {
	                if (graph2.isFirable(vertex)) {
	                	graph2.fireTransition(vertex);
	                	Map<String, Integer> newState = graph2.getPlaceTokens();
	                	mxCell node = nodeMap.get(newState);
	                	if (node == null) {
	                		String markingName = "M" + markingMap.size();
	                		node = (mxCell)insertVertex(getDefaultParent(), null, markingName, i * 50, j * 50,
	            					40, 40, "NODE");
	                		markingMap.put(markingName, newState);
	                		nodeMap.put(newState, node);
	                		queue.add(newState);
	                	}
	                	Object[] edges = graph2.getEdgesBetween(node1, node, true);
	                	if (edges.length > 0) {
	                		mxCell edge = (mxCell) edges[0];
	                		String label = (String) edge.getValue();
	                		label += ", t" + graph2.getCellMarkingName(vertex);
	                		edge.setValue(label);
	                	} else {
	                		insertEdge(getDefaultParent(), null, "t" + graph2.getCellMarkingName(vertex), node1, node, "");
	                	}
	                	
	                	enabledCount++;	                	
	                	
	                	graph2.setPlaceTokens(state);
	                	j++;
	                	
	                	// Add to liveSet
	                	liveSet.add(vertex);
	                }
				}
			}
		}
		setCellStyle(node1.getStyle() + ";COMPLETE", new Object[] {node1});
		if (enabledCount == 0) {
		    String marking = (String)node1.getValue();
		    deadList.add(marking);
		}
		        
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
        currentStyle.put(mxConstants.STYLE_FILLCOLOR, "#00FF00");
        //currentStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
        stylesheet.putCellStyle("CURRENT", currentStyle);
        
        Hashtable<String, Object> initialStyle = new Hashtable<String, Object>();
        //initialStyle.put(mxConstants.STYLE_FILLCOLOR, "#00FF00");
        initialStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_DOUBLE_ELLIPSE);
        //initialStyle.put(mxConstants.STYLE_STROKEWIDTH, 4);
        stylesheet.putCellStyle("INITIAL", initialStyle);
		
		Map<String, Object> edge = new HashMap<String, Object>();
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_STROKEWIDTH, 2);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");
	    edge.put(mxConstants.STYLE_EDGE, "PETRI_STYLE");
	    
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
        edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	    //edge.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff");
	    //edge.put(mxConstants.STYLE_LABEL_BORDERCOLOR, "#000000");
        stylesheet.putCellStyle("EDGE", edge);
		getStylesheet().setDefaultEdgeStyle(edge);
		
		// Label position styles		
		Map<String, Object> alignTC = new HashMap<String, Object>();
		alignTC.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
		alignTC.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        stylesheet.putCellStyle("ALIGN_TC", alignTC);
        
        Map<String, Object> alignTR = new HashMap<String, Object>();
        alignTR.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
        alignTR.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT);
        stylesheet.putCellStyle("ALIGN_TR", alignTR);
        
        Map<String, Object> alignTL = new HashMap<String, Object>();
        alignTL.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
        alignTL.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
        stylesheet.putCellStyle("ALIGN_TL", alignTL);
        
        Map<String, Object> alignMR = new HashMap<String, Object>();
        alignMR.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        alignMR.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT);
        stylesheet.putCellStyle("ALIGN_MR", alignMR);
        
        Map<String, Object> alignML = new HashMap<String, Object>();
        alignML.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        alignML.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
        stylesheet.putCellStyle("ALIGN_ML", alignML);
        
        Map<String, Object> alignBC = new HashMap<String, Object>();
        alignBC.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
        alignBC.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        stylesheet.putCellStyle("ALIGN_BC", alignBC);
        
        Map<String, Object> alignBR = new HashMap<String, Object>();
        alignBR.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
        alignBR.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_RIGHT);
        stylesheet.putCellStyle("ALIGN_BR", alignBR);
        
        Map<String, Object> alignBL = new HashMap<String, Object>();
        alignBL.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
        alignBL.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
        stylesheet.putCellStyle("ALIGN_BL", alignBL);
	}
	
	public void setActiveState(Object obj) {
	    if (obj instanceof mxCell) {
	        mxCell vertex = (mxCell)obj;
	        Map<String, Integer> map = getMarkingMap(obj);
	        if (map != null) {
	            Map<String, Integer> currentState = graph.getPlaceTokens();
	            mxCell currentCell = nodeMap.get(currentState);
	            setCurrentCell(vertex);
	            Map<String, Integer> state = map;
	            graph.setPlaceTokens(state);
	            graph.refresh();
	        }
	    }
	}
	
	public void updateActiveMarking() {
            Map<String, Integer> currentState = graph.getPlaceTokens();
            mxCell cell = nodeMap.get(currentState);
                setCurrentCell(cell);
	}
	
	public void setCurrentCell(Object obj) {
	    if (obj == currentCell) {
	        return;
	    }
	    if (currentCell != null) {
            String style = currentCell.getStyle().replaceFirst(";CURRENT", "");
            setCellStyle(style, new Object[] {currentCell});
            currentCell = null;
        }
	    
	    if (obj instanceof mxCell) {
	        
	        
	        mxCell cell = (mxCell)obj;
	        setCellStyle(cell.getStyle() + ";CURRENT", new Object[] {cell});
	        currentCell = cell;
	    }
	}
	
	public void setCellLabelPosition(Object obj, String position) {
	    if (obj instanceof mxCell && ((mxCell)obj).isEdge()) {
	        mxCell cell = (mxCell)obj;
            //String style = cell.getStyle().replaceFirst(";ALIGN_..", "");
            //style += ";ALIGN_" + position;
            //System.out.println(style);
	        String style = "EDGE;ALIGN_" + position;
            setCellStyle(style, new Object[] {cell});
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
				
				Object[] ids = map.keySet().toArray();
				int[] intIDs = new int[ids.length];

				for (int i = 0; i < ids.length; i++) {
				    intIDs[i] = Integer.parseInt((String)ids[i]);
				}
				Arrays.sort(intIDs);
				for (int intID : intIDs) {
					String id = Integer.toString(intID);
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
	        	String message = "Marking is at: ";
	        	message += nodeMap.get(goal).getValue();
	            setSelectionCell(nodeMap.get(goal));
	            JOptionPane.showMessageDialog(null, message);
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
	
	public Map<String, Integer> getInitialMarking() {
		return markingMap.get("M0");
	}
	
	public boolean isReachableMarking(Map<String, Integer> marking) {
	    return nodeMap.get(marking) != null;
	}
	
	public Map<String, Map<String, Integer>> getMarkings() {
		return markingMap;
	}
}
