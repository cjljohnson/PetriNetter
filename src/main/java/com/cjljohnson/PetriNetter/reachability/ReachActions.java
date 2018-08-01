package com.cjljohnson.PetriNetter.reachability;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class ReachActions {
    
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
}
