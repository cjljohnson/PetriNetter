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
        		UIManager.getIcon("FileView.fileIcon")));
        add(editor.bind("Open", PetriEditorActions.getOpenAction(),
        		UIManager.getIcon("FileChooser.newFolderIcon")));
        add(editor.bind("Save", PetriEditorActions.getSaveAction(),
        		UIManager.getIcon("FileView.floppyDriveIcon")));
        add(editor.bind("Save As", PetriEditorActions.getSaveAsAction(),
                UIManager.getIcon("FileView.floppyDriveIcon")));
        
        addSeparator();
        
        add(editor.bind("Undo", PetriEditorActions.getUndoAction(),
                UIManager.getIcon("FileView.floppyDriveIcon")));
        add(editor.bind("Redo", PetriEditorActions.getRedoAction(),
                UIManager.getIcon("FileView.floppyDriveIcon")));
        
        
        JToggleButton cursor = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/place.gif")));
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
        JToggleButton addToken = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/transition.gif")));
        addToken.setToolTipText("Add Token to Place");
        add(addToken);
        JToggleButton removeToken = new JToggleButton(new ImageIcon(
                PetriEditor.class.getResource("/images/transition.gif")));
        removeToken.setToolTipText("Remove Token to Place");
        add(removeToken);
        
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(cursor);
        bGroup.add(addPlace);
        bGroup.add(addTransition);
        bGroup.add(addToken);
        bGroup.add(removeToken);
        
        cursor.addActionListener(new PetriToolActions.CursorAction(editor));
        addPlace.addActionListener(new PetriToolActions.CreatePlaceAction(editor));
        addTransition.addActionListener(new PetriToolActions.CreateTransitionAction(editor));
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
        
        Action boundedness = editor.bind("Boundedness", new PetriEditorActions.ShowBoundedness(),
        		UIManager.getIcon("Tree.leafIcon"));
        boundedness.putValue(Action.SHORT_DESCRIPTION, "Calculate Boundedness");
        add(boundedness);
        
        Action liveness = editor.bind("Liveness", new PetriEditorActions.ShowLiveness(),
        		UIManager.getIcon("Tree.closedIcon"));
        liveness.putValue(Action.SHORT_DESCRIPTION, "Calculate Liveness");
        add(liveness);
        
    }
}
