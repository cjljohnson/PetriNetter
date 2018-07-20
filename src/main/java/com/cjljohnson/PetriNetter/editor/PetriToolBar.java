package com.cjljohnson.PetriNetter.editor;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;



public class PetriToolBar extends JToolBar {

    public PetriToolBar(final PetriEditor editor, int orientation) {
        
        super(orientation);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);
        setRollover(true);

        add(editor.bind("New", new PetriEditorActions.NewAction(),
        		UIManager.getIcon("FileView.fileIcon")));
        add(editor.bind("Open", new PetriEditorActions.OpenAction(),
        		UIManager.getIcon("FileChooser.newFolderIcon")));
        add(editor.bind("Save", new PetriEditorActions.SaveAction(true),
        		UIManager.getIcon("FileView.floppyDriveIcon")));
        
        addSeparator();
        
        
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
        
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(cursor);
        bGroup.add(addPlace);
        bGroup.add(addTransition);
        
        cursor.setSelected(true);
        
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
