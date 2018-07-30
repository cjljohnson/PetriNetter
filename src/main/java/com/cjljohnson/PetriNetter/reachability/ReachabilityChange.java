package com.cjljohnson.PetriNetter.reachability;

import javax.swing.JSplitPane;

import com.cjljohnson.PetriNetter.PetriNetManager;
import com.mxgraph.model.mxIGraphModel.mxAtomicGraphModelChange;
import com.mxgraph.swing.mxGraphComponent;

public class ReachabilityChange extends mxAtomicGraphModelChange {
	
	private PetriNetManager manager;
	private boolean reachValid;
	private mxGraphComponent reachComponent;
	private JSplitPane splitPane;
	
	public ReachabilityChange(PetriNetManager manager, boolean reachValid, mxGraphComponent reachComponent,
			JSplitPane splitPane) {
		this.manager = manager;
		this.reachValid = reachValid;
		this.reachComponent = reachComponent;
		this.splitPane = splitPane;
	}

    @Override
    public void execute() {
    	boolean oldReachValid = manager.reachValid();
    	mxGraphComponent oldReachComponent = manager.getReachComponent();
    	JSplitPane oldSplitPane = manager.getSplitPane();
    	
    	manager.setReachComponent(reachValid, reachComponent, splitPane);
    	
    	this.reachValid = oldReachValid;
    	this.reachComponent = oldReachComponent;
    	this.splitPane = oldSplitPane;
    }

}
