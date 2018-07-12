package com.cjljohnson.PetriNetter.reachability;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import com.mxgraph.swing.mxGraphComponent;

public class ReachRightClick extends JPopupMenu {
    
    public ReachRightClick(final mxGraphComponent reachComponent, int x, int y)
    {
        //final Object cell = hello.getGraphComponent().getCellAt(x, y);
        
        add(new AbstractAction("Find Marking", null) {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                ReachabilityGraph graph = (ReachabilityGraph)reachComponent.getGraph();
                graph.findNodes();
            }
        });
        
    }

}
