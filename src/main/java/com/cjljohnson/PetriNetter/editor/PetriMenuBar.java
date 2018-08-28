package com.cjljohnson.PetriNetter.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import com.cjljohnson.PetriNetter.PetriGraphActions;
import com.cjljohnson.PetriNetter.editor.PetriEditorActions.HighlightTransitionsAction;
import com.cjljohnson.PetriNetter.editor.examples.ExampleActions;

public class PetriMenuBar extends JMenuBar{
    
    public PetriMenuBar(final PetriEditor editor) {
        
        JMenu menu = null;
        
        // File menu
        menu = add(new JMenu("File"));
        menu.setMnemonic(KeyEvent.VK_F);
        
        menu.add(editor.bind("New", PetriEditorActions.getNewAction(), UIManager.getIcon("FileView.fileIcon"))).setMnemonic(KeyEvent.VK_N);
        menu.add(editor.bind("Open", PetriEditorActions.getOpenAction(), UIManager.getIcon("FileChooser.newFolderIcon"))).setMnemonic(KeyEvent.VK_O);
        menu.add(editor.bind("Save", PetriEditorActions.getSaveAction(), 
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk.png")))).setMnemonic(KeyEvent.VK_S);
        menu.add(editor.bind("Save As...", PetriEditorActions.getSaveAsAction(), 
                new ImageIcon(
                        PetriEditor.class.getResource("/images/disk_multiple.png")))).setMnemonic(KeyEvent.VK_A);
        menu.add(editor.bind("Export Image", PetriEditorActions.getExportImageAction(),
                new ImageIcon(
                		PetriEditor.class.getResource("/images/image.png")))).setMnemonic(KeyEvent.VK_I);
        UIManager.getIcon("FileView.fileIcon");
        
        // Edit menu
        menu = add(new JMenu("Edit"));
        menu.setMnemonic(KeyEvent.VK_E);
        menu.add(editor.bind("Undo", PetriEditorActions.getUndoAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/arrow_undo.png")))).setMnemonic(KeyEvent.VK_U);
        menu.add(editor.bind("Redo", PetriEditorActions.getRedoAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/arrow_redo.png")))).setMnemonic(KeyEvent.VK_R);
        
        // View menu
        menu = add(new JMenu("View"));
        menu.setMnemonic(KeyEvent.VK_V);
        
        menu.add(editor.bind("Reset Zoom", PetriEditorActions.getZoomResetAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier.png")))).setMnemonic(KeyEvent.VK_R);
        menu.add(editor.bind("Zoom In", PetriEditorActions.getZoomInAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier_zoom_in.png")))).setMnemonic(KeyEvent.VK_I);
        menu.add(editor.bind("Zoom Out", PetriEditorActions.getZoomOutAction(),
                new ImageIcon(
                        PetriEditor.class.getResource("/images/magnifier_zoom_out.png")))).setMnemonic(KeyEvent.VK_O);
        
        menu.addSeparator();
        
        JCheckBoxMenuItem showHighlight = new JCheckBoxMenuItem("Highlight Active Transitions");
        showHighlight.setMnemonic(KeyEvent.VK_H);
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
        menu.setMnemonic(KeyEvent.VK_A);
        menu.add(editor.bind("Create Reachability Graph", new PetriEditorActions.CreateReachabilityAction(), "/images/reach.gif")).setMnemonic(KeyEvent.VK_R);
        menu.add(editor.bind("Place Boundedness", new PetriEditorActions.ShowBoundedness(), "")).setMnemonic(KeyEvent.VK_B);
        menu.add(editor.bind("Semi-Liveness", new PetriEditorActions.ShowLiveness(), "")).setMnemonic(KeyEvent.VK_L);
        menu.add(editor.bind("Deadlock", new PetriEditorActions.ShowDeadlock(), "")).setMnemonic(KeyEvent.VK_D);
        
        // Examples menu
        menu = add(new JMenu("Examples"));
        menu.setMnemonic(KeyEvent.VK_X);
        menu.add(editor.bind("Lock Example", new ExampleActions.LockExampleAction(), "")).setMnemonic(KeyEvent.VK_L);
        menu.add(editor.bind("Buffer Example", new ExampleActions.BufferExampleAction(), "")).setMnemonic(KeyEvent.VK_B);
        menu.add(editor.bind("Dining Philosophers Example", new ExampleActions.DiningExampleAction(), "")).setMnemonic(KeyEvent.VK_D);
        
        // Help menu
        menu = add(new JMenu("Help"));
        menu.setMnemonic(KeyEvent.VK_H);
        menu.add(editor.bind("User Guide", new PetriEditorActions.OpenTutotorialAction(), "")).setMnemonic(KeyEvent.VK_G);
    }

}
