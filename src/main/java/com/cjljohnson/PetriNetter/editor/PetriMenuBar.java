package com.cjljohnson.PetriNetter.editor;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.cjljohnson.PetriNetter.PetriGraphActions;

public class PetriMenuBar extends JMenuBar{
    
    public PetriMenuBar(final PetriEditor editor) {
        
        JMenu menu = null;
        
        // File menu
        menu = add(new JMenu("File"));
        
        menu.add(editor.bind("New", new PetriEditorActions.NewAction(), null));
        menu.add(editor.bind("Open", new PetriEditorActions.OpenAction(), null));
        menu.add(editor.bind("Save", new PetriEditorActions.SaveAction(false), null));
    }

}
