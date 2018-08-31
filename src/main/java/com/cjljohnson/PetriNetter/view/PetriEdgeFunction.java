/*
 * Custom edge style function that draws opposing edges with opposite curves 
 * so they do not overlap.
 * 
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.view;

import java.util.List;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle.mxEdgeStyleFunction;
import com.mxgraph.view.mxGraph;

public class PetriEdgeFunction implements mxEdgeStyleFunction{

    public void apply(mxCellState state, mxCellState source, mxCellState target, List<mxPoint> points,
            List<mxPoint> result) {

        mxGraph graph = state.getView().getGraph();
        boolean isBidirectional = false;
        
           
        // Find if there are opposing edges between two cells
        if (source != null && target != null) {
            graph = source.getView().getGraph();
            isBidirectional = mxGraphModel.getEdgesBetween(graph.getModel(), target.getCell(), source.getCell()).length > 1;
        }
        
        // If not then do nothing.
        if (!isBidirectional)
            return;
        
        // Find edge mid-point
        double midX = (source.getCenterX() + target.getCenterX()) / 2;
        double midY = (source.getCenterY() + target.getCenterY()) / 2;
        
        // Find polar angle, rotate clockwise
        double polar = Math.atan2(source.getCenterX() - target.getCenterX(), source.getCenterY() - target.getCenterY());
        polar += Math.PI / 2;
        
        if (polar > Math.PI)
            polar -= 2 * Math.PI;
        
        // Add new point 20 units away
        double newX = midX + 20 * Math.sin(polar);
        double newY = midY + 20 * Math.cos(polar);
        result.add(new mxPoint(newX, newY));
    }
    
    

}
