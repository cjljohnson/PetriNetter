package com.cjljohnson.PetriNetter.reachability;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cjljohnson.PetriNetter.PetriNetManager;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;

public class ReachActions {
	
	private static final CloseReachabilityAction closeReachabilityAction = new CloseReachabilityAction("Close Reachability");
	
	public static CloseReachabilityAction getCloseReachabilityAction() {
		return closeReachabilityAction;
	}
    
    public static final mxGraph getGraph(ActionEvent e)
    {
        Object source = e.getSource();

        if (source instanceof mxGraphComponent)
        {
            return ((mxGraphComponent) source).getGraph();
        }

        return null;
    }

    public static class PositionLabelAction extends AbstractAction
    {

        /**
         * 
         */

        /**
         * 
         */
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
    
    public static class CloseReachabilityAction extends AbstractAction
    {

        /**
         * 
         */
        private static final long serialVersionUID = 938191175262154014L;

        /**
         * 
         */

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
            	System.out.println("YEE");
            }
            if (component == null) {
            	return;
            }
            PetriNetManager manager = (PetriNetManager)component;
            
            // Add reach change to undoable edit
		    mxGraphModel model = (mxGraphModel)manager.getPetriComponent().getGraph().getModel();
		    
		    ReachabilityChange reachChange = new ReachabilityChange(manager, false, null, null);
		    reachChange.execute();
		    
		    mxUndoableEdit edit = new mxUndoableEdit(this);
		    edit.add(reachChange);
		    
		    manager.getUndoManager().undoableEditHappened(edit);
		    
		    manager.revertToFinalised();

        }
    }
}
