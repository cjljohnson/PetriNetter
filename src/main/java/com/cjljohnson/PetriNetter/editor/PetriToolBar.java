package com.cjljohnson.PetriNetter.editor;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;



public class PetriToolBar extends JToolBar {

    public PetriToolBar(final PetriEditor editor, int orientation) {
        
        super(orientation);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), getBorder()));
        setFloatable(false);

        add(editor.bind("New", new PetriEditorActions.NewAction(),
                "/images/new.gif"));
        add(editor.bind("Open", new PetriEditorActions.OpenAction(),
                null));
        add(editor.bind("Save", new PetriEditorActions.SaveAction(true),
                null));
    }
}
