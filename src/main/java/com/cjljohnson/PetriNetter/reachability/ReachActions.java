/*
 * Contains the actions relating to the ReachabilityGraph component.
 * Mainly contains actions that are accessible by right clicking the Reachability Graph.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.reachability;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.petri.PetriNetManager;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;

public class ReachActions {
	
	private static final CloseReachabilityAction closeReachabilityAction = new CloseReachabilityAction("Close Reachability");
	
	public static CloseReachabilityAction getCloseReachabilityAction() {
		return closeReachabilityAction;
	}
    
	/*
	 * Get reachabiltiy graph from event.
	 */
    public static final mxGraph getGraph(ActionEvent e)
    {
        Object source = e.getSource();

        if (source instanceof mxGraphComponent)
        {
            return ((mxGraphComponent) source).getGraph();
        }

        return null;
    }

    /*
     * Position label of arc
     */
    public static class PositionLabelAction extends AbstractAction
    {
        private static final long serialVersionUID = 3887344511242268848L;
        private mxGraphComponent reachComponent;
        private Object cell;
        private String position;

        /**
         * 
         * @param name
         */
        public PositionLabelAction(String name, mxGraphComponent reachComponent, 
                Object cell, String position)
        {
            super(name);
            this.reachComponent = reachComponent;
            this.cell = cell;
            this.position = position;
        }

        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            ReachabilityGraph reach = (ReachabilityGraph)reachComponent.getGraph();

            if (reach != null)
            {
                reach.setCellLabelPosition(cell, position);
            }
        }
    }
    
    /*
     * Set the cell to current cell.
     */
    public static class GoToNodeAction extends AbstractAction
    {
        private static final long serialVersionUID = 3887344511242268848L;
        private mxGraphComponent reachComponent;
        private Object cell;

        /**
         * 
         * @param name
         */
        public GoToNodeAction(String name, mxGraphComponent reachComponent, 
                Object cell)
        {
            super(name);
            this.reachComponent = reachComponent;
            this.cell = cell;
        }

        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            ReachabilityGraph reach = (ReachabilityGraph)reachComponent.getGraph();

            if (reach != null)
            {
                if (reach.getMarkingMap(cell) != null)
                {
                    reach.setActiveState(cell);
                }
            }
        }
    }
    
    /*
     * Close reachabiltiy graph
     */
    public static class CloseReachabilityAction extends AbstractAction
    {

        private static final long serialVersionUID = 938191175262154014L;
        /**
         * 
         * @param name
         */
        public CloseReachabilityAction(String name)
        {
            super(name);
        }

        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
        	Component component = (Component)e.getSource();
            while (!(component instanceof PetriNetManager) && component != null) {
            	component = component.getParent();
            }
            if (component == null) {
            	return;
            }
            PetriNetManager manager = (PetriNetManager)component;
            
            // Add reach change to undoable edit
		    mxGraphModel model = (mxGraphModel)manager.getPetriComponent().getGraph().getModel();
		    try {
		    	model.beginUpdate();
		    	model.execute(new ReachabilityChange(manager, false, null, null));
		    	manager.revertToFinalised();
		    } finally {
		    	model.endUpdate();
		    }
        }
    }
}
