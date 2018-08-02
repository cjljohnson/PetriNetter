package com.cjljohnson.PetriNetter.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import com.cjljohnson.PetriNetter.PetriGraphActions;
import com.cjljohnson.PetriNetter.editor.PetriEditorActions.HighlightTransitionsAction;

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
        menu.add(editor.bind("Export Image", PetriEditorActions.getExportImageAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk_multiple.png"))));
        UIManager.getIcon("FileView.fileIcon");
        
        // Edit menu
        menu = add(new JMenu("Edit"));
        menu.add(editor.bind("Undo", PetriEditorActions.getUndoAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/arrow_undo.png"))));
        menu.add(editor.bind("Redo", PetriEditorActions.getRedoAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/arrow_redo.png"))));
        
        // View menu
        menu = add(new JMenu("View"));
        
        menu.add(editor.bind("Reset Zoom", PetriEditorActions.getZoomResetAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier.png"))));
        menu.add(editor.bind("Zoom In", PetriEditorActions.getZoomInAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier_zoom_in.png"))));
        menu.add(editor.bind("Zoom Out", PetriEditorActions.getZoomOutAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier_zoom_out.png"))));
        
        menu.addSeparator();
        
        JCheckBoxMenuItem showHighlight = new JCheckBoxMenuItem("Highlight Active Transitions");
        // Define ActionListener
        final HighlightTransitionsAction highlightAction = new HighlightTransitionsAction(editor);
        ActionListener highlightListener = new ActionListener() {
          public void actionPerformed(ActionEvent actionEvent) {
            AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            highlightAction.actionPerformed(actionEvent);
          }
        };
        showHighlight.addActionListener(highlightAction);
        //showHighlight.setAction(new PetriEditorActions.HighlightTransitionsAction(editor));
        menu.add(showHighlight);
        showHighlight.setSelected(true);
        
        // Analysis menu
        menu = add(new JMenu("Analysis"));
        menu.add(editor.bind("Create Reachability Graph", new PetriEditorActions.CreateReachabilityAction(), "/images/reach.gif"));
        menu.add(editor.bind("Boundedness", new PetriEditorActions.ShowBoundedness(), ""));
        menu.add(editor.bind("Liveness", new PetriEditorActions.ShowLiveness(), ""));
    }

}
