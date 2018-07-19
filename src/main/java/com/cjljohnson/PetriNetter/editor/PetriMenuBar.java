package com.cjljohnson.PetriNetter.editor;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import com.cjljohnson.PetriNetter.PetriGraphActions;

public class PetriMenuBar extends JMenuBar{
    
    public PetriMenuBar(final PetriEditor editor) {
        
        JMenu menu = null;
        
        // File menu
        menu = add(new JMenu("File"));
        
        menu.add(editor.bind("New", new PetriEditorActions.NewAction(), "/images/place.gif"));
        menu.add(editor.bind("Open", new PetriEditorActions.OpenAction(), null));
        menu.add(editor.bind("Save", new PetriEditorActions.SaveAction(true), null));
        UIManager.getIcon("FileView.fileIcon");
        
        // Edit menu
        menu = add(new JMenu("Edit"));
        menu.add("Undo");
        
        // Analysis menu
        menu = add(new JMenu("Analysis"));
        menu.add("Reachability");
        menu.add("Boundedness");
        menu.add("Liveness");
    }

}
