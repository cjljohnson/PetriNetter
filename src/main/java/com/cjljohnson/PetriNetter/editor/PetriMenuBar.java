package com.cjljohnson.PetriNetter.editor;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import com.cjljohnson.PetriNetter.PetriGraphActions;

public class PetriMenuBar extends JMenuBar{
    
    public PetriMenuBar(final PetriEditor editor) {
        
        JMenu menu = null;
        
        // File menu
        menu = add(new JMenu("File"));
        
        menu.add(editor.bind("New", PetriEditorActions.getNewAction(), UIManager.getIcon("FileView.fileIcon")));
        menu.add(editor.bind("Open", PetriEditorActions.getOpenAction(), UIManager.getIcon("FileChooser.newFolderIcon")));
        menu.add(editor.bind("Save", PetriEditorActions.getSaveAction(), 
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk.png"))));
        menu.add(editor.bind("Save As...", PetriEditorActions.getSaveAsAction(), 
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk_multiple.png"))));
        UIManager.getIcon("FileView.fileIcon");
        
        // Edit menu
        menu = add(new JMenu("Edit"));
        menu.add("Undo");
        
        // Analysis menu
        menu = add(new JMenu("Analysis"));
        menu.add(editor.bind("Create Reachability Graph", new PetriEditorActions.CreateReachabilityAction(), "/images/reach.gif"));
        menu.add(editor.bind("Boundedness", new PetriEditorActions.ShowBoundedness(), ""));
        menu.add(editor.bind("Liveness", new PetriEditorActions.ShowLiveness(), ""));
    }

}
