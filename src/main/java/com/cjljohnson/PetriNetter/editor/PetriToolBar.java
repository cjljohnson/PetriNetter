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
                "/com/mxgraph/examples/swing/images/new.gif"));
        add(editor.bind("Open", new PetriEditorActions.OpenAction(),
                "/com/mxgraph/examples/swing/images/open.gif"));
        add(editor.bind("Save", new PetriEditorActions.SaveAction(false),
                "/com/mxgraph/examples/swing/images/save.gif"));
    }
}
