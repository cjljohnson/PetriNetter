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
        
           
        
        if (source != null && target != null) {
            graph = source.getView().getGraph();
            isBidirectional = mxGraphModel.getEdgesBetween(graph.getModel(), target.getCell(), source.getCell()).length > 1;
        }
        
        if (!isBidirectional)
            return;
        
        
//        mxPoint p0 = state.getAbsolutePoint(0);
//        mxPoint pe = state.getAbsolutePoint(state.getAbsolutePointCount() - 1);
        double midX = (source.getCenterX() + target.getCenterX()) / 2;
        double midY = (source.getCenterY() + target.getCenterY()) / 2;
        
        double polar = Math.atan2(source.getCenterX() - target.getCenterX(), source.getCenterY() - target.getCenterY());
        polar += Math.PI / 2;
        if (polar > Math.PI)
            polar -= 2 * Math.PI;
        
        double newX = midX + 20 * Math.sin(polar);
        double newY = midY + 20 * Math.cos(polar);
        result.add(new mxPoint(newX, newY));
        result.add(new mxPoint(newX, newY));
        result.add(new mxPoint(newX, newY));
        System.out.println(result);
    }
    
    

}
