package com.cjljohnson.PetriNetter.editor.examples;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;

import com.cjljohnson.PetriNetter.PetriNetManager;
import com.cjljohnson.PetriNetter.editor.PetriEditor;
import com.cjljohnson.PetriNetter.model.Arc;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;

public class ExampleActions {
	
	@SuppressWarnings("serial")
    public static class LockExampleAction extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            PetriGraph graph = new PetriGraph();
            
            try {
            	graph.getModel().beginUpdate();
            
            	Object p1= graph.addPlace(3, -1, 60, 210);
            	Object p2= graph.addPlace(0, -1, 220, 30);
            	Object p3= graph.addPlace(0, -1, 210, 380);
            	Object p4= graph.addPlace(1, -1, 150, 290);
            	
            	graph.setPlaceName(p1, "Idle");
            	graph.setPlaceName(p2, "Read");
            	graph.setPlaceName(p3, "Write");
            	graph.setPlaceName(p4, "Lock");
            	
            	graph.setPlaceLabelPosition(p1, -1, 0.3);
            	graph.setPlaceLabelPosition(p2, 0, -0.7);
            	
            	Object t1= graph.addTransition(250, 170);
            	Object t2= graph.addTransition(60, 50);
            	Object t3= graph.addTransition(250, 260);
            	Object t4= graph.addTransition(60, 350);
            	
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p1, t1, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p1, t3, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t1, p2, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p2, t2, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t2, p1, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t3, p3, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p3, t4, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t4, p1, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t4, p4, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p4, t3, "ARC");
            	
            	
            	graph.checkEnabledTransitions();
            	
            } finally {
            	graph.getModel().endUpdate();
            }
            
            PetriNetManager manager = new PetriNetManager(graph);
            editor.addPetriNet(manager);
        }
    }
	
	@SuppressWarnings("serial")
    public static class BufferExampleAction extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            PetriGraph graph = new PetriGraph();
            
            try {
            	graph.getModel().beginUpdate();
            
            	Object p1= graph.addPlace(1, -1, 100, 50);
            	Object p2= graph.addPlace(0, -1, 100, 210);
            	Object p3= graph.addPlace(2, 5, 280, 130);
            	Object p4= graph.addPlace(1, -1, 460, 50);
            	Object p5= graph.addPlace(0, -1, 460, 210);
            	
            	graph.setPlaceName(p1, "Idle");
            	graph.setPlaceName(p2, "Producing");
            	graph.setPlaceName(p3, "Buffer");
            	graph.setPlaceName(p4, "Idle");
            	graph.setPlaceName(p5, "Consuming");
            	
            	Object t1= graph.addTransition(20, 130);
            	Object t2= graph.addTransition(180, 130);
            	Object t3= graph.addTransition(380, 130);
            	Object t4= graph.addTransition(540, 130);
            	
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p1, t1, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t1, p2, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p2, t2, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t2, p1, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t2, p3, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(3), p3, t3, "ARC;ALIGN_BC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t3, p5, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p5, t4, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), t4, p4, "ARC");
            	graph.insertEdge(graph.getDefaultParent(), null, new Arc(), p4, t3, "ARC");
            	
            	
            	graph.checkEnabledTransitions();
            	
            } finally {
            	graph.getModel().endUpdate();
            }
            
            PetriNetManager manager = new PetriNetManager(graph);
            editor.addPetriNet(manager);
        }
    }
	
	@SuppressWarnings("serial")
    public static class DiningExampleAction extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            PetriGraph graph = new PetriGraph();
            
            //FileInputStream stream = new FileInputStream(new FIle());
            
            
            
            Document document;
			try {
				document = mxXmlUtils
				        .parseXml(mxUtils.readInputStream(this.getClass().getResourceAsStream("/tutorial/diningPhilosophers.pnet")));
			

            mxCodec codec = new mxCodec(document);
            codec.decode(
                    document.getDocumentElement(),
                    graph.getModel());
            
            
            PetriNetManager manager = new PetriNetManager(graph);
            editor.addPetriNet(manager);
            
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }

}
