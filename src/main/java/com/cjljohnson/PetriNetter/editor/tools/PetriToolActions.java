/*
 * Contains ToolActions that change the mouse look and 
 * behaviour when clicking on the PetriGraph.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.editor.tools;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.cjljohnson.PetriNetter.editor.PetriEditor;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Place;
import com.cjljohnson.PetriNetter.petri.PetriNetManager;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

public class PetriToolActions {
    
    
    public static abstract class ToolAction extends AbstractAction {
        
        PetriEditor editor;
        
        public ToolAction(PetriEditor editor) {
            this.editor = editor;
        }
        
        
        public abstract void onClick(MouseEvent e, PetriNetManager manager);
        
        /*
         * (non-Javadoc)
         * Sets selected tool of editor to this tool and sets the cursor.
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            editor.setSelectedTool(this);
            for (Component manager : editor.getPane().getComponents()) {
                if (manager instanceof PetriNetManager) {
                    setCursor(((PetriNetManager)manager).getPetriComponent());
                }
            }
            
        }
        
        /*
         * Sets the cursor.
         */
        public abstract void setCursor(mxGraphComponent graph);
        
        /*
         * Enable or dsiable graph handlers depending on tool.
         */
        protected void setGraphHandlers(mxGraphComponent graph, boolean enabled) {
        	if (!enabled) {
        		graph.getGraph().clearSelection();
        	}
            graph.getGraphHandler().setEnabled(enabled);
            graph.getSelectionCellsHandler().setEnabled(enabled);
            graph.getConnectionHandler().setEnabled(enabled);
        }
        
    }
    
    /*
     * Set the cursor to the default cursor mode.
     */
    public static class CursorAction extends ToolAction
    {
        private static final long serialVersionUID = 3887344511242268848L;

        /**
         * 
         * @param name
         */
        public CursorAction(PetriEditor editor)
        {
            super(editor);
        }

        @Override
        public void onClick(MouseEvent e, PetriNetManager manager) {
            
        }

        @Override
        public void setCursor(mxGraphComponent graph) {
            setGraphHandlers(graph, true);
            graph.getConnectionHandler().getMarker().setHotspotEnabled(true);
            graph.getGraphControl().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            graph.getGraphHandler().DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
        }
    }
    
    /*
     * Set to place creation mode.
     */
    public static class CreatePlaceAction extends ToolAction
    {
        private static final long serialVersionUID = 3887344511242268848L;

        /**
         * 
         * @param name
         */
        public CreatePlaceAction(PetriEditor editor)
        {
            super(editor);
        }

        @Override
        public void onClick(MouseEvent e, PetriNetManager manager) {
        	if (manager.reachValid()) {
        		return;
        	}
        	
            mxGraphComponent graphComponent = manager.getPetriComponent();
            PetriGraph graph = (PetriGraph)graphComponent.getGraph();
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                    manager.getPetriComponent());
            
            if (graph != null && ((graphComponent.getCellAt(e.getX(), e.getY()) == null)
                    || ((mxCell)graphComponent.getCellAt(e.getX(), e.getY()))
                    .getGeometry().isRelative()))
            {
                graph.addPlace(0, -1, pt.x, pt.y);
                e.consume();
            }
        }

        @Override
        public void setCursor(mxGraphComponent graph) {
            setGraphHandlers(graph, false);
            graph.getGraphControl().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            graph.getGraphHandler().DEFAULT_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);
            graph.getGraphControl().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            		new ImageIcon(PetriEditor.class.getResource("/images/placetool.gif")).getImage(),
            		new Point(0,0),"custom cursor"));
        }
    }
    
    /*
     * Set to transition creation mode.
     */
    public static class CreateTransitionAction extends ToolAction
    {
        private static final long serialVersionUID = 3887344511242268848L;

        /**
         * 
         * @param name
         */
        public CreateTransitionAction(PetriEditor editor)
        {
            super(editor);
        }

        @Override
        public void onClick(MouseEvent e, PetriNetManager manager) {
        	if (manager.reachValid()) {
        		return;
        	}
        	
            mxGraphComponent graphComponent = manager.getPetriComponent();
            PetriGraph graph = (PetriGraph)graphComponent.getGraph();
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                    manager.getPetriComponent());
            
            if (graph != null && ((graphComponent.getCellAt(e.getX(), e.getY()) == null)
                    || ((mxCell)graphComponent.getCellAt(e.getX(), e.getY()))
                    .getGeometry().isRelative()))
            {
                graph.addTransition(pt.x, pt.y);
                e.consume();
            }
            
        }

        @Override
        public void setCursor(mxGraphComponent graph) {
            setGraphHandlers(graph, false);
            graph.getGraphControl().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            graph.getGraphHandler().DEFAULT_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);
            graph.getGraphControl().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            		new ImageIcon(PetriEditor.class.getResource("/images/transitiontool.gif")).getImage(),
            		new Point(0,0),"custom cursor"));
        }
    }
    
    /*
     * Create arc mode.
     */
    public static class CreateArcAction extends ToolAction
    {
        private static final long serialVersionUID = 3887344511242268848L;

        /**
         * 
         * @param name
         */
        public CreateArcAction(PetriEditor editor)
        {
            super(editor);
        }

        @Override
        public void onClick(MouseEvent e, PetriNetManager manager) {
            
        }

        @Override
        public void setCursor(mxGraphComponent graph) {
            setGraphHandlers(graph, true);
            graph.getConnectionHandler().getMarker().setHotspotEnabled(false);
            graph.getGraphControl().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            graph.getGraphHandler().DEFAULT_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);
            graph.getGraphControl().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            		new ImageIcon(PetriEditor.class.getResource("/images/arctool2.gif")).getImage(),
            		new Point(0,0),"custom cursor"));
        }
    }
    
    /*
     * Add token mode.
     */
    public static class AddTokenAction extends ToolAction
    {
        private static final long serialVersionUID = 3887344511242268848L;

        /**
         * 
         * @param name
         */
        public AddTokenAction(PetriEditor editor)
        {
            super(editor);
        }

        @Override
        public void onClick(MouseEvent e, PetriNetManager manager) {
        	if (manager.reachValid()) {
        		return;
        	}
        	
            mxGraphComponent graphComponent = manager.getPetriComponent();
            PetriGraph graph = (PetriGraph)graphComponent.getGraph();
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                    manager.getPetriComponent());
            
            Object cell = graphComponent.getCellAt(e.getX(), e.getY());
            if (cell != null && ((mxCell)cell).getValue() instanceof Place)
            {
                graph.setTokens(cell, graph.getTokens(cell) + 1);
                graph.refresh();
                e.consume();
            }
            
        }

        @Override
        public void setCursor(mxGraphComponent graph) {
            setGraphHandlers(graph, false);
            graph.getGraphControl().setCursor(new Cursor(Cursor.HAND_CURSOR));
            graph.getGraphHandler().DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

        }
    }
    
    /*
     * Remove token mode
     */
    public static class RemoveTokenAction extends ToolAction
    {
        private static final long serialVersionUID = 3887344511242268848L;

        /**
         * 
         * @param name
         */
        public RemoveTokenAction(PetriEditor editor)
        {
            super(editor);
        }

        @Override
        public void onClick(MouseEvent e, PetriNetManager manager) {
        	if (manager.reachValid()) {
        		return;
        	}
        	
            mxGraphComponent graphComponent = manager.getPetriComponent();
            PetriGraph graph = (PetriGraph)graphComponent.getGraph();
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                    manager.getPetriComponent());
            
            Object cell = graphComponent.getCellAt(e.getX(), e.getY());
            if (cell != null && ((mxCell)cell).getValue() instanceof Place)
            {
                graph.setTokens(cell, graph.getTokens(cell) - 1);
                graph.refresh();
                e.consume();
            }
            
        }

        @Override
        public void setCursor(mxGraphComponent graph) {
            setGraphHandlers(graph, false);
            graph.getGraphControl().setCursor(new Cursor(Cursor.HAND_CURSOR));
            graph.getGraphHandler().DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
        }
    }
    


}
