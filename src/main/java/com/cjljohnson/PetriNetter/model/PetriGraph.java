/*
 * The graph object for Petri nets.
 * 
 * Acts as an interface for creating modifications to the graph.
 * Any changes should be made using these methods rather than on 
 * the objects them selves to maintain the integrity of the graph.
 * 
 * The Petri net specific methods such as addPlace should be used 
 * instead of the generic JGraphX methods so as not to create an 
 * invalid Petri net.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class PetriGraph extends mxGraph{
	
	public PetriGraph() {
		initStyles();
	}
	
	// Overrides method to disallow direct cell editing
	@Override
	public boolean isCellEditable(Object cell)
	{
		return false;
	}
	
	// Overrides method to disallow sub cell selection
	@Override
	public boolean isCellSelectable(Object cell) {
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();
			
			// Don't allow Place sub-cell to be selected
			if ("PLACE_LABEL".equals(value))
			{
				return false;
			}
		}
		return super.isCellSelectable(cell);
	}
	
	// Prevents cells from being minimsed and expanded
	public boolean isCellFoldable(Object cell, boolean collapse)
	{
		return false;
	}
	
	// Don't allow connections to place labels
	public boolean isCellConnectable(Object cell) {
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if ("PLACE_LABEL".equals(value))
			{
				return false;
			}
		}
		return super.isCellSelectable(cell);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * Label string for each component.
	 * 
	 * @see com.mxgraph.view.mxGraph#convertValueToString(java.lang.Object)
	 */
	@Override
	public String convertValueToString(Object cell)
	{
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Place)
			{	
				Place place = (Place)value;
				if (getCellGeometry(cell).isRelative()) 
				{ // Capacity label
				    while (getCellGeometry(cell).isRelative()) 
				    {
				        cell = ((mxCell) cell).getParent();
				    }
				    place = (Place)((mxCell) cell).getValue();
					int capacity = place.getCapacity();
					String capacityLabel = capacity == -1 ? "\u03C9" : Integer.toString(capacity);
					return "k = " + capacityLabel + "\np" + getCellMarkingName(cell);
				} else 
				{
					String tokens = Integer.toString(place.getTokens());
				    return tokens;
				}
			}
			
			if ("PLACE_LABEL".equals(value)) 
			{
			    while (getCellGeometry(cell).isRelative()) 
                {
                    cell = ((mxCell) cell).getParent();
                }
                Place place = (Place)((mxCell) cell).getValue();
                String name = place.getName();
                if (name == null) {
                	name = "p" + getCellMarkingName(cell);
                } else {
                	name += "(p" + getCellMarkingName(cell) + ")";
                }
                
                int capacity = place.getCapacity();
                String capacityLabel = capacity == -1 ? "\u03C9" : Integer.toString(capacity);
                return  name + "\n" + "k = " + capacityLabel;
			}
			
			if (value instanceof Transition)
			{
				return "t" + getCellMarkingName(cell);
			}
			if (value instanceof Arc)
			{
				int weight = ((Arc)value).getWeight();
				return weight == 1 ? "" : Integer.toString(weight);
			}
		}

		return super.convertValueToString(cell);
	}
	

	/**
	 * Returns the validation error message to be displayed when inserting or
	 * changing an edges' connectivity. A return value of null means the edge
	 * is valid, a return value of '' means it's not valid, but do not display
	 * an error message. Any other (non-empty) string returned from this method
	 * is displayed as an error message when trying to connect an edge to a
	 * source and target. This implementation uses the multiplicities, as
	 * well as multigraph and allowDanglingEdges to generate validation
	 * errors.
	 * 
	 * @param edge Cell that represents the edge to validate.
	 * @param source Cell that represents the source terminal.
	 * @param target Cell that represents the target terminal.
	 */
	public String getEdgeValidationError(Object edge, Object source,
			Object target)
	{
		if (edge != null && !isAllowDanglingEdges()
				&& (source == null || target == null))
		{
			return "";
		}

		if (edge != null && model.getTerminal(edge, true) == null
				&& model.getTerminal(edge, false) == null)
		{
			return null;
		}

		// Checks if we're dealing with a loop
		if (!isAllowLoops() && source == target && source != null)
		{
			return "";
		}

		// Checks if the connection is generally allowed
		if (!isValidConnection(source, target))
		{
			return "";
		}

		if (source != null && target != null)
		{
			StringBuffer error = new StringBuffer();
			boolean hasError = false;

			// Checks if the cells are already connected
			// and adds an error message if required			
			if (!multigraph)
			{
				Object[] tmp = mxGraphModel.getEdgesBetween(model, source,
						target, true);

				// Checks if the source and target are not connected by another edge
				if (tmp.length > 1 || (tmp.length == 1 && tmp[0] != edge))
				{
				    hasError = true;
					error.append(mxResources.get("alreadyConnected",
							"Already Connected") + "\n");
				}
			}

			// Gets the number of outgoing edges from the source
			// and the number of incoming edges from the target
			// without counting the edge being currently changed.
			int sourceOut = mxGraphModel.getDirectedEdgeCount(model, source,
					true, edge);
			int targetIn = mxGraphModel.getDirectedEdgeCount(model, target,
					false, edge);

			// Checks the change against each multiplicity rule
			if (multiplicities != null)
			{
				for (int i = 0; i < multiplicities.length; i++)
				{
					String err = multiplicities[i].check(this, edge, source,
							target, sourceOut, targetIn);

					if (err != null)
					{
					    hasError = true;
						error.append(err);
					}
				}
			}

			// Validates the source and target terminals independently
			String err = validateEdge(edge, source, target);

			if (err != null)
			{
			    hasError = true;
				error.append(err);
			}

			return (hasError) ? error.toString() : null;
		}

		return (allowDanglingEdges) ? null : "";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * Petri net specific rules for edge connection.
	 * 
	 * @see com.mxgraph.view.mxGraph#validateEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public String validateEdge(Object edge, Object source, Object target)
	{
	    if (source != null && source instanceof mxCell
	            && target != null && target instanceof mxCell) {
            Object sourceValue = ((mxCell)source).getValue();
            Object targetValue = ((mxCell)target).getValue();
            
            if (sourceValue instanceof Place 
            		&& targetValue instanceof Transition)
            	return null;
            if (sourceValue instanceof Transition
            		&& targetValue instanceof Place)
            	return null;
            
            if (sourceValue instanceof Transition
            		&& targetValue instanceof Transition)
            	return "Transitions can only be connected to places.";
            if (sourceValue instanceof Place
            		&& targetValue instanceof Place)
            	return "Places can only be connected to transitions.";
	    }
	    return "";
	}
	
	/*
	 * Fires a transition cell passed as a parameter if it is enabled.
	 * 
	 * Updates transition highlighting.
	 */
	public boolean fireTransition(Object obj)
	{
		if (!(obj instanceof mxCell))
		{
			return false;
		}
		mxCell t = (mxCell) obj;
		if (!isFirable(obj)) return false;
		Object[] edges = getAllEdges(new Object[] {obj});

		// Fire Transition
		try 
		{
		    model.beginUpdate();
		    for (Object o : edges) 
		    {
		        mxCell edge = (mxCell)o;
		        if (t.equals(edge.getSource())) // Outgoing arc
		        {
		            mxCell out = (mxCell)edge.getTarget();
		            Place place = ((Place)out.getValue());
		            int tokens = place.getTokens();

		            Arc arc = (Arc)edge.getValue();
		            int arcWeight = arc.getWeight();
		            setTokens(out, tokens + arcWeight);
		        } else	// Incoming arc
		        {
		            mxCell in = (mxCell)edge.getSource();
		            Place place = ((Place)in.getValue());
		            int tokens = place.getTokens();

		            Arc arc = (Arc)edge.getValue();
		            int arcWeight = arc.getWeight();

		            setTokens(in, tokens - arcWeight);
		        }
		    }
		    checkEnabledFromTransition(obj);
		} finally
		{
		    model.endUpdate();
		}
		
		return true;
	}
	
	/*
	 * Add place at 0,0.
	 */
	public Object addPlace(int tokens, int capacity) {
        return addPlace(tokens, capacity, 0, 0);
    }
	
	/*
	 * Add new place.
	 * 
	 * Tokens is initial number of tokens in place. Must be in 
	 * range 0 - capacity.
	 * 
	 * Capacity is max tokens possible at the place.  Must be >1 and 
	 * >tokens, or -1 for infinite capacity.
	 */
	public Object addPlace(int tokens, int capacity, int x, int y) {
	    Object cell;
		try {
			getModel().beginUpdate();
		
			Place place = new Place(tokens, capacity, 0, null);
			cell = insertVertex(getDefaultParent(), null, place, x, y,
				40, 40, "PLACE");
			mxGeometry geo1 = new mxGeometry(0, 1.2, 40,
					20);
			geo1.setRelative(true);
			mxCell capLabel = new mxCell("PLACE_LABEL", geo1,
					"CAPACITY");
			capLabel.setVertex(true);
			addCell(capLabel, cell);
		} finally
		{
			getModel().endUpdate();
		}
		return cell;
	}
	
	/*
	 * Add transition at 0,0.
	 */
	public Object addTransition() {
        return addTransition(0, 0);
    }
	
	/*
	 * Add transition at coordinates.
	 */
	public Object addTransition(int x, int y) {
	    Object cell;
		try {
			getModel().beginUpdate();
		
		Transition transition = new Transition();
		cell = insertVertex(getDefaultParent(), null, transition, x, y,
				40, 40, "TRANSITION");
		} finally
		{
			getModel().endUpdate();
		}
		return cell;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * Create edge between two vertices.
	 * 
	 * New edges have an arc weight of 1.
	 * 
	 * must be between place and transition or transition and place.
	 * 
	 * @see com.mxgraph.view.mxGraph#createEdge(java.lang.Object, java.lang.String, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.String)
	 */
	public Object createEdge(Object parent, String id, Object value,
			Object source, Object target, String style)
	{
		if (!(value instanceof Arc)) {
			Arc arc = new Arc(1);
			value = arc;
		}

		Object result = super.createEdge(parent, id, value, source, target, "ARC");
		
		return result;
	}
	
	/*
	 * Update all transition highlighting.
	 */
	public void checkEnabledTransitions() {
		
		try
		{
			getModel().beginUpdate();
		
	    Object[] cells = getChildVertices(getDefaultParent());
	    Object[] edges = getAllEdges(cells);
	    
	    for (Object o : cells) {
	        checkEnabledTransition(o);
	    }
	    
	    for (Object edge : edges ) {
	    	checkEnabledEdge(edge);
	    }
		}
		finally
		{
			getModel().endUpdate();
		}
	}
	
	/*
	 * Checks the enabled status of adjacent transitions to a place
	 */
	public void checkEnabledFromPlace(Object o) {
		if (o instanceof mxCell) {
			Object value = ((mxCell) o).getValue();
			if (value instanceof Place) {
                Place placeValue = (Place)value;
                	mxCell place = (mxCell) o;
                	
                	// Get adjacent transitions
                	List<Object> transitions = new ArrayList<Object>();
                	for (int i = 0; i < place.getEdgeCount(); i++) {
                		// Updated connected edges as well
                		checkEnabledEdge(place.getEdgeAt(i));
                		
                		Object transition = getVertexFromEdge(place.getEdgeAt(i), true);
                		if (transition != null)
                			transitions.add(transition);
                	}
                	
                	// Check adjacent transitions
                	for (Object transition : transitions) {
                		checkEnabledTransition(transition);
                	}

			}
		}
	}
	/*
	 * Checks the enabled status of transitions affected by firing a transition
	 */
	public void checkEnabledFromTransition(Object o) {
		Set<Object> transitions = new HashSet<Object>();
		if (o instanceof mxCell) {
			mxCell t = (mxCell) o;
			Object value = t.getValue();
			if (value instanceof Transition) {
                Transition transitionValue = (Transition)value;
                	// Add self
                	transitions.add(o);
                	
                	// Get adjacent places
                	for (int i = 0; i < t.getEdgeCount(); i++) {
                		// Update connected edge styles as well
                		checkEnabledEdge(t.getEdgeAt(i));
                		
                		mxCell place = (mxCell) getVertexFromEdge(t.getEdgeAt(i),false);
                		if (place != null) {
                			// Get transitions connected to adjacent places
                			for (int j = 0; j < place.getEdgeCount(); j++) {
                				// Update connected edge styles as well
                        		checkEnabledEdge(place.getEdgeAt(j));
                				Object transition = getVertexFromEdge(place.getEdgeAt(j),true);
                				if (transition != null)
                					transitions.add(transition);
                			}
                		}	
                	}
                	
                	// Check affected transitions
                	for (Object transition : transitions) {
                		checkEnabledTransition(transition);
                	}
			}
		}
	}
	
	/*
	 * Checks the enabled status of adjacent transitions to an arc
	 */
	public void checkEnabledFromEdge(Object o) {
		if (o instanceof mxCell) {
			Object value = ((mxCell) o).getValue();
			if (value instanceof Arc) {
                mxCell edge = (mxCell) o;
                Object transition = getVertexFromEdge(o, true);
                checkEnabledTransition(transition);
                checkEnabledEdge(o);
			}
		}
	}
	
	/*
	 * Checks the enabled status of a transition
	 */
	public void checkEnabledTransition(Object o) {
	    if (o instanceof mxCell) {
            Object value = ((mxCell) o).getValue();

            if (value instanceof Transition)
            {
                if (isFirable(o)) {
                    setCellStyle("TRANSITION;ACTIVETRANSITION", new Object[] {o});
                } else {
                    setCellStyle("TRANSITION", new Object[] {o});
                }
            }
        }
	}
	
	/*
	 * Checks whether an edge is enabled and adds or removes the enabled edge style as appropriate
	 * This is based off the current state 
	 */
	public void checkEnabledEdge(Object o) {
		if (o instanceof mxCell) {
			Object value = ((mxCell) o).getValue();
			if (value instanceof Arc)
            {
			    String style = ((mxCell) o).getStyle();
			    if (style.isEmpty()) {
			        style = "ARC";
			    }
			    style = style.replaceAll(";ACTIVETRANSITION", "");
                if (isFirableArc(o))
            	{
                    style += ";ACTIVETRANSITION";
            	}
                setCellStyle(style, new Object[] {o});
            }
		}
		
	}
	
	/*
	 * Checks whether a transition is enabled.
	 */
	public boolean isFirable(Object obj) {
	    
	    mxCell t;
	    
	    if (obj instanceof mxCell) {
	        t = (mxCell) obj;
	        Object value = ((mxCell) obj).getValue();

            if (!(value instanceof Transition))
            {
            	return false;
            }
	    } else {
	        return false;
	    }
	    
	    Object[] edges = getAllEdges(new Object[] {obj});
	    // Can't fire if not connected
	    if (edges.length == 0) return false;
        
        // Check if valid
        for (Object o : edges) 
        {
            if (!isFirableArc(o))
            	return false;
        }
        return true;
	}
	
	/*
	 * Check whether an arc meets the firing condition of the transition it is 
	 * connected to.
	 */
	public boolean isFirableArc(Object o) {
		if (o instanceof mxCell) {
			mxCell edge = (mxCell)o;

			Object e = ((mxCell) o).getValue();

			if (e instanceof Arc)
			{
					mxCell t = (mxCell)getVertexFromEdge(o, true);
					Arc arc = (Arc)e;
					mxCell p = (mxCell)getVertexFromEdge(o, false);
					Place place = (Place)p.getValue();
					int tokens = place.getTokens();
					int capacity = place.getCapacity();
					int weight = arc.getWeight();

					if (t.equals(edge.getSource()))
					{
						// Outbound arc
						if (capacity == -1) return true;  // Infinite capacity
						return capacity >= (weight + tokens);
					} else
					{
						// Inbound arc
						return (tokens - weight) >= 0;
					}
			}
		}
		return false;
	}

	/*
	 * Get the place or transition connected to an edge.
	 * 
	 * Parameter is TRUE to find the transition, FALSE to find the edge.
	 */
	public Object getVertexFromEdge(Object o, boolean transition) {
		if (o != null && o instanceof mxCell) 
		{
			mxCell edge = (mxCell)o;
			mxCell source = (mxCell) edge.getSource();
			mxCell target = (mxCell) edge.getTarget();
			
			if (transition)
			{
				if (source.getValue() instanceof Transition)
					return source;
				if (target.getValue() instanceof Transition)
					return target;
			} else 
			{
				if (source.getValue() instanceof Place)
					return source;
				if (target.getValue() instanceof Place)
					return target;
			}
		}
		return null;
	}
	
	/*
	 * Get a map of current place marking.
	 * 
	 * KEY: Cell id string.
	 * VALUE: Number of tokens at place.
	 */
	public Map<String, Integer> getPlaceTokens() {
		mxGraphModel model = (mxGraphModel) getModel();
		Map<String, Object> cells = model.getCells();
		Map<String, Integer> tokenMap = new TreeMap<String, Integer>();
		for (String id : cells.keySet()) {
			mxCell cell = (mxCell)cells.get(id);

			if (cell.getValue() instanceof Place) {
				Place place = (Place)cell.getValue();
				if (!getCellGeometry(cell).isRelative()) {
					int tokens = place.getTokens();
					tokenMap.put(id, tokens);
				}
			}
		}
		return tokenMap;
	}
	
	/*
	 * Set the marking to a map of cell IDs to tokens.
	 * 
	 * KEY: Cell id string.
     * VALUE: Number of tokens at place.
	 */
	public void setPlaceTokens(Map<String, Integer> tokenMap) {
		try {
			getModel().beginUpdate();
			mxGraphModel model = (mxGraphModel) getModel();
			for (String id : tokenMap.keySet()) {
				mxCell cell = (mxCell)model.getCell(id);
				if (cell != null) {
					if (cell.getValue() instanceof Place) {
						setTokens(cell, tokenMap.get(id));
					}
				}
			}
		}finally {
			getModel().endUpdate();
		}
	}
	
	/*
	 * Get the index of a cell in comparison to other cells of the same type.
	 * 
	 * Used for place and transition reference names.
	 * 
	 */
	public int getCellMarkingName(Object cell) {
		if (cell != null && cell instanceof mxCell)
		{
			while ( ((mxCell)cell).getGeometry().isRelative()) {
				cell = ((mxCell)cell).getParent();
			}
			Object value = ((mxCell) cell).getValue();

			if (value != null)
			{
				int n = 1;
				Object[] vertices = getChildVertices(getDefaultParent());
				for (Object vertex : vertices) {
					if (vertex == cell) {
						return n;
					}
					
					Object vVal = ((mxCell) vertex).getValue();

					if (value.getClass().equals(vVal.getClass()))
					{
						n++;
					}
				}
				
			}
		}
		return 0;
	}
	
	/*
	 * Set the name of a place.
	 */
	public boolean setPlaceName(Object cell, String newName) {
		if (cell == null || !(cell instanceof mxCell) ||
				!(((mxCell)cell).getValue() instanceof Place)) {
			return false;
		}
		Place place = (Place)((mxCell)cell).getValue();

		if (newName != null && newName.isEmpty()) {
			newName = null;
		}

		if (!(newName == null ? place.getName() == null : newName.equals(place.getName()))) {
			

			try {
				model.beginUpdate();
				Place newPlace = place.clone();
				newPlace.setName(newName);
				model.setValue(cell, newPlace);
			} finally {
				model.endUpdate();
			}
			return true;
		}

		return false;
	}
	
	/*
	 * Set number of tokens at a place.
	 * 
	 * Must be >= 0, and >= capacity if vapavity is not infinite.
	 */
	public boolean setTokens(Object cell, int tokens) {
	    if (cell == null || !(cell instanceof mxCell) ||
	            !(((mxCell)cell).getValue() instanceof Place)
	            || tokens < 0) {
	        return false;
	    }
	    Place place = (Place)((mxCell)cell).getValue();
	    
	    int currentTokens = place.getTokens();
	    int capacity = place.getCapacity();
	    
	    if (tokens <= capacity || capacity == -1) {
	        try {
	            model.beginUpdate();
	            Place newPlace = place.clone();
	            newPlace.setTokens(tokens);
	            model.setValue(cell, newPlace);
	            checkEnabledFromPlace(cell);
	        } finally {
	            model.endUpdate();
	        }
	        return true;
	    }
	    return false;
	}
	
	/*
	 * Get number of tokens at a place.
	 */
	public int getTokens(Object cell) {
        if (cell == null || !(cell instanceof mxCell) ||
                !(((mxCell)cell).getValue() instanceof Place)) {
            return -1;
        }
        Place place = (Place)((mxCell)cell).getValue();
        
        return place.getTokens();
    }
	
	/*
	 * Set capacity of a place.
	 * 
	 * Must be >0 and >= current tokens, or =1 for infinite capacity.
	 */
	public boolean setCapacity(Object cell, int capacity) {
        if (cell == null || !(cell instanceof mxCell) ||
                !(((mxCell)cell).getValue() instanceof Place)
                || capacity < -1 || capacity == 0) {
            return false;
        }
        Place place = (Place)((mxCell)cell).getValue();
        
        int currentTokens = place.getTokens();
        int currentCapacity = place.getCapacity();
        
        if (currentTokens <= capacity || capacity == -1) {
            try {
                model.beginUpdate();
                Place newPlace = place.clone();
                newPlace.setCapacity(capacity);
                model.setValue(cell, newPlace);
                checkEnabledFromPlace(cell);
            } finally {
                model.endUpdate();
            }
            return true;
        }
        return false;
    }
	
	/*
	 * Set arc weight.
	 * 
	 * Must be >0.
	 */
	public boolean setArcWeight(Object cell, int weight) {
	    if (cell == null || !(cell instanceof mxCell) ||
	            !(((mxCell)cell).getValue() instanceof Arc)
	            || weight < 1) {
	        return false;
	    }
	    Arc arc = (Arc)((mxCell)cell).getValue();

	    int currentWeight = arc.getWeight();

	    if (currentWeight == weight) {
	        return true;
	    }

	    try {
	        model.beginUpdate();
	        Arc newArc = arc.clone();
	        newArc.setWeight(weight);
	        model.setValue(cell, newArc);
	        checkEnabledFromEdge(cell);
	    } finally {
	        model.endUpdate();
	    }
	    return true;
	}
	
	/*
	 * get list of all place cells.
	 */
	public Object[] getPlaces() {
		List<mxCell> places = new ArrayList<mxCell>();
		
		Object[] vertices = getChildVertices(getDefaultParent());
		for (Object vertex : vertices) {
			mxCell cell = (mxCell)vertex;

			if (cell.getValue() instanceof Place) {
				Place place = (Place)cell.getValue();
				if (!getCellGeometry(cell).isRelative()) {
					places.add(cell);
				}
			}
		}
		return places.toArray();
	}
	
	/*
	 * Get list of all transition cells.
	 */
	public Object[] getTransitions() {
		List<mxCell> transitions = new ArrayList<mxCell>();
		
		Object[] vertices = getChildVertices(getDefaultParent());
		for (Object vertex : vertices) {
			mxCell cell = (mxCell)vertex;

			if (cell.getValue() instanceof Transition) {
				Transition transition = (Transition)cell.getValue();
				transitions.add(cell);
			}
		}
		return transitions.toArray();
	}
	
	/*
	 * Initialise custom cell styles.
	 */
	private void initStyles() {
	    mxStylesheet stylesheet = getStylesheet();
		Hashtable<String, Object> placeStyle = new Hashtable<String, Object>();
		placeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_OPACITY, 100);
		placeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		placeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		placeStyle.put(mxConstants.STYLE_NOLABEL, false);
		placeStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_PERIMETER_SPACING, 4);
		stylesheet.putCellStyle("PLACE", placeStyle);
		
		Hashtable<String, Object> placeCapacityStyle = new Hashtable<String, Object>();
		placeCapacityStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeCapacityStyle.put(mxConstants.STYLE_FILLCOLOR, "none");
		placeCapacityStyle.put(mxConstants.STYLE_STROKECOLOR, "none");
		stylesheet.putCellStyle("CAPACITY", placeCapacityStyle);
		
		Hashtable<String, Object> transitionStyle = new Hashtable<String, Object>();
		transitionStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		transitionStyle.put(mxConstants.STYLE_OPACITY, 100);
		transitionStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		transitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		transitionStyle.put(mxConstants.STYLE_NOLABEL, false);
		transitionStyle.put(mxConstants.STYLE_PERIMETER_SPACING, 4);
		stylesheet.putCellStyle("TRANSITION", transitionStyle);
		

        Hashtable<String, Object> activeTransitionStyle = new Hashtable<String, Object>();
        activeTransitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
        stylesheet.putCellStyle("ACTIVETRANSITION", activeTransitionStyle);
		

		
		// Settings for edges
	    Map<String, Object> edge = new HashMap<String, Object>();
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_STROKEWIDTH, 2);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_EDGE, "PETRI_STYLE");

	    stylesheet.setDefaultEdgeStyle(edge);
	    stylesheet.putCellStyle("ARC", edge);
	    
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
	
	/*
	 * Enable/disable highlighting transitions by setting ACTIVETRANSITION style stroke colour.
	 */
	public void highlightActiveTransitions(boolean highlight) {
		Map<String, Object> style = getStylesheet().getCellStyle("ACTIVETRANSITION", null);
		
		String colour;
		
		if (highlight) {
			colour = "#FF0000";
		} else {
			colour = "#000000";
		}
		
		style.put(mxConstants.STYLE_STROKECOLOR, colour);
		getStylesheet().putCellStyle("ACTIVETRANSITION", style);
		refresh();
	}
	
	/*
	 * Set position of arc labels relative to mid-point.
	 */
	public void setCellLabelPosition(Object obj, String position) {
        if (obj instanceof mxCell && ((mxCell)obj).isEdge()) {
            mxCell cell = (mxCell)obj;
            String style = cell.getStyle().replaceFirst(";ALIGN_..", "");
            
            if (style.isEmpty()) {
                style = "ARC";
            }
            
            style += ";ALIGN_" + position;
            setCellStyle(style, new Object[] {cell});
        }
    }
	
	/*
	 * Set place label position relative to place cell.
	 */
	public void setPlaceLabelPosition(Object obj, double x, double y) {
        if (obj instanceof mxCell && ((mxCell)obj).getValue() instanceof Place) {
            mxCell cell = (mxCell)obj;
            cell.getChildCount();
            mxCell placeLabel = (mxCell) cell.getChildAt(0);
            
            
            
            if (placeLabel != null) {
                mxGeometry geometry = (mxGeometry) getModel().getGeometry(placeLabel).clone();
                
                geometry.setX(x);
                geometry.setY(y);
                getModel().setGeometry(placeLabel, geometry);
            }
        }
    }
	

}
