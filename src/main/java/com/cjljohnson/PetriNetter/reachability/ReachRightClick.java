package com.cjljohnson.PetriNetter.reachability;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import com.cjljohnson.PetriNetter.PetriGraphActions;
import com.cjljohnson.PetriNetter.PetriNetManager;
import com.cjljohnson.PetriNetter.model.Arc;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Place;
import com.cjljohnson.PetriNetter.model.Transition;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

public class ReachRightClick extends JPopupMenu {
    
    public ReachRightClick(final mxGraphComponent reachComponent, int x, int y)
    {
        //final Object cell = hello.getGraphComponent().getCellAt(x, y);
        
        final Object cell = reachComponent.getCellAt(x, y);

        if (cell != null && ((mxCell)cell).isEdge()) {
            edgeMenu(reachComponent, cell);
        }
        
        add(new AbstractAction("Find Marking", null) {
            
            

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                ReachabilityGraph graph = (ReachabilityGraph)reachComponent.getGraph();
                graph.findNodes();
            }
        });
        
    }
    
    private void edgeMenu(mxGraphComponent reachComponent, Object cell) {
        
        JMenu positionMenu = new JMenu("Label position");
        positionMenu.add(new ReachActions.PositionLabelAction("Top", reachComponent, cell, "BC"));
        positionMenu.add(new ReachActions.PositionLabelAction("Top Left", reachComponent, cell, "BR"));
        positionMenu.add(new ReachActions.PositionLabelAction("Top Right", reachComponent, cell, "BL"));
        positionMenu.add(new ReachActions.PositionLabelAction("Left", reachComponent, cell, "MR"));
        positionMenu.add(new ReachActions.PositionLabelAction("Right", reachComponent, cell, "ML"));
        positionMenu.add(new ReachActions.PositionLabelAction("Bottom", reachComponent, cell, "TC"));
        positionMenu.add(new ReachActions.PositionLabelAction("Bottom Left", reachComponent, cell, "TR"));
        positionMenu.add(new ReachActions.PositionLabelAction("Bottom Right", reachComponent, cell, "TL"));
        

        add(positionMenu);
        
        addSeparator();
        
//        add(manager.bind("Fire Transition", new PetriGraphActions.FireTransitionAction(cell),
//                        "/images/lightning_go.png"))

        addSeparator();
    }

}
