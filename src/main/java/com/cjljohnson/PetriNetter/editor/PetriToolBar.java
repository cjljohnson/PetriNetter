package com.cjljohnson.PetriNetter.editor;

import javax.swing.BorderFactory;
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
        
    }
}
