package com.cjljohnson.PetriNetter.editor.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.cjljohnson.PetriNetter.PetriNetManager;
import com.cjljohnson.PetriNetter.editor.PetriEditor;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Place;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

public class PetriToolActions {
    
    
    public static abstract class ToolAction extends AbstractAction {
        
        PetriEditor editor;
        
        public ToolAction(PetriEditor editor) {
            this.editor = editor;
        }
        
        
        public abstract void onClick(MouseEvent e, PetriNetManager manager);

        public void actionPerformed(ActionEvent e) {
            editor.setSelectedTool(this);
            setCursor();
        }
        
        public abstract void setCursor();
        
    }
    
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
        public void setCursor() {
            // TODO Auto-generated method stub
            
        }
    }
    
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
            mxGraphComponent graphComponent = manager.getPetriComponent();
            PetriGraph graph = (PetriGraph)graphComponent.getGraph();
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                    manager.getPetriComponent());
            
            if (graph != null && (graphComponent.getCellAt(e.getX(), e.getY()) == null))
            {
                graph.addPlace(0, -1, pt.x, pt.y);
                e.consume();
            }
        }

        @Override
        public void setCursor() {
            // TODO Auto-generated method stub
            
        }
    }
    
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
            mxGraphComponent graphComponent = manager.getPetriComponent();
            PetriGraph graph = (PetriGraph)graphComponent.getGraph();
            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                    manager.getPetriComponent());
            
            if (graph != null && (graphComponent.getCellAt(e.getX(), e.getY()) == null))
            {
                graph.addTransition(pt.x, pt.y);
                e.consume();
            }
            
        }

        @Override
        public void setCursor() {
            
            
        }
    }
    
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
        public void setCursor() {
            
            
        }
    }
    
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
        public void setCursor() {
            
            
        }
    }
    


}
