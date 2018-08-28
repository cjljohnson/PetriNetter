package com.cjljohnson.PetriNetter.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions;
import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions.CursorAction;
import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions.ToolAction;



public class PetriToolBar extends JToolBar {

    public PetriToolBar(final PetriEditor editor, int orientation) {
        
        super(orientation);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);
        setRollover(true);

        add(editor.bind("New", PetriEditorActions.getNewAction(),
        		UIManager.getIcon("FileView.fileIcon"))).setToolTipText("New");
        add(editor.bind("Open", PetriEditorActions.getOpenAction(),
        		UIManager.getIcon("FileChooser.newFolderIcon"))).setToolTipText("Open");
        add(editor.bind("Save", PetriEditorActions.getSaveAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk.png")))).setToolTipText("Save");
        add(editor.bind("Save As", PetriEditorActions.getSaveAsAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk_multiple.png")))).setToolTipText("Save As");
        add(editor.bind("Export Image", PetriEditorActions.getExportImageAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/image.png")))).setToolTipText("Export image");
        
        addSeparator();
        
        add(editor.bind("Undo", PetriEditorActions.getUndoAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/arrow_undo.png")))).setToolTipText("Undo");
        add(editor.bind("Redo", PetriEditorActions.getRedoAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/arrow_redo.png")))).setToolTipText("Redo");
        
        addSeparator();
        add(editor.bind("Reset Zoom", PetriEditorActions.getZoomResetAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier.png")))).setToolTipText("Reset Zoom level");
        add(editor.bind("Zoom In", PetriEditorActions.getZoomInAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier_zoom_in.png")))).setToolTipText("Zoom in");
        add(editor.bind("Zoom Out", PetriEditorActions.getZoomOutAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier_zoom_out.png")))).setToolTipText("Zoom out");
        
        
        
        addSeparator();
        
        JToggleButton cursor = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/cursor.png")));
        cursor.setToolTipText("Cursor");
        add(cursor);
        JToggleButton addPlace = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/place.gif")));
        addPlace.setToolTipText("Add Place");
        add(addPlace);
        JToggleButton addTransition = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/transition.gif")));
        addTransition.setToolTipText("Add Transition");
        add(addTransition);
        JToggleButton addArc = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/arc.gif")));
        addArc.setToolTipText("Add Arc");
        add(addArc);
        JToggleButton addToken = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/add.png")));
        addToken.setToolTipText("Add Token to Place");
        add(addToken);
        JToggleButton removeToken = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/delete.png")));
        removeToken.setToolTipText("Remove Token from Place");
        add(removeToken);
        
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(cursor);
        bGroup.add(addPlace);
        bGroup.add(addTransition);
        bGroup.add(addArc);
        bGroup.add(addToken);
        bGroup.add(removeToken);
        
        cursor.addActionListener(new PetriToolActions.CursorAction(editor));
        addPlace.addActionListener(new PetriToolActions.CreatePlaceAction(editor));
        addTransition.addActionListener(new PetriToolActions.CreateTransitionAction(editor));
        addArc.addActionListener(new PetriToolActions.CreateArcAction(editor));
        addToken.addActionListener(new PetriToolActions.AddTokenAction(editor));
        removeToken.addActionListener(new PetriToolActions.RemoveTokenAction(editor));
        
        cursor.setSelected(true);
        System.out.println(cursor.getActionListeners()[0]);
        editor.setSelectedTool((ToolAction)cursor.getActionListeners()[0]);
        
        addSeparator();
        
        Action reach = editor.bind("Create Reachability Graph", new PetriEditorActions.CreateReachabilityAction(),
        		 "/images/reach.gif");
        reach.putValue(Action.SHORT_DESCRIPTION, "Create Reachability Graph");
        add(reach);
        
        Action boundedness = editor.bind("Place Boundedness", new PetriEditorActions.ShowBoundedness(),
        		UIManager.getIcon("Tree.leafIcon"));
        boundedness.putValue(Action.SHORT_DESCRIPTION, "Calculate Boundedness");
        add(boundedness);
        
        Action liveness = editor.bind("Semi-Liveness", new PetriEditorActions.ShowLiveness(),
        		UIManager.getIcon("Tree.leafIcon"));
        liveness.putValue(Action.SHORT_DESCRIPTION, "Calculate Semi-Liveness");
        add(liveness);
        
        Action deadlock = editor.bind("Deadlock", new PetriEditorActions.ShowDeadlock(),
                UIManager.getIcon("Tree.leafIcon"));
        deadlock.putValue(Action.SHORT_DESCRIPTION, "Calculate Deadlock");
        add(deadlock);
        
//        addSeparator();
//        add(editor.bind("Finalise net", PetriEditorActions.getFinaliseNetAction(),
//                new ImageIcon(
//                        PetriEditor.class.getResource("/images/star.png"))));
//        add(editor.bind("Revert to finalised net", PetriEditorActions.getRevertToFinaliseNetAction(),
//        		new ImageIcon(
//        				PetriEditor.class.getResource("/images/star_revert.png"))));
        
        
        
    }
}
