package com.cjljohnson.PetriNetter.editor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.cjljohnson.PetriNetter.PetriNetManager;
import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions;
import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions.ToolAction;

public class PetriEditor extends JPanel{
    
    JMenuBar menuBar;
    JToolBar toolBar;
    JTabbedPane pane;
    
    ToolAction selectedTool;
    
    public PetriEditor(String title) {
        JFrame frame = new JFrame(title);
        
        ImageIcon img = new ImageIcon(PetriEditor.class.getResource("/images/place.gif"));
        frame.setIconImage(img.getImage());
        
        this.menuBar = new PetriMenuBar(this);
        this.toolBar = new PetriToolBar(this, JToolBar.HORIZONTAL);
        this.pane = new JTabbedPane();
        pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        //add(menuBar);
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(pane);
        
        newPetriNet();
        
        frame.setJMenuBar(menuBar);
        frame.setContentPane(this);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public PetriNetManager newPetriNet() {
        PetriNetManager manager = new PetriNetManager();
        pane.add("New Petri Net", manager);
        pane.setTabComponentAt(pane.getTabCount() - 1, new ButtonTabComponent(pane));
        selectedTool.setCursor(manager.getPetriComponent());
        
        return manager;
    }
    
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, String iconUrl)
    {        		
        ImageIcon icon = (iconUrl != null) ? new ImageIcon(
                PetriEditor.class.getResource(iconUrl)) : null;
        
        return bind(name, action, icon);
    }
    
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, Icon icon)
    {
        AbstractAction newAction = new AbstractAction(name, icon)
//        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? null : null)
        {
            public void actionPerformed(ActionEvent e)
            {
                action.actionPerformed(new ActionEvent(PetriEditor.this, e
                        .getID(), e.getActionCommand()));
            }
        };
        
        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
        
        return newAction;
    }
    
    public boolean addPetriNet(PetriNetManager manager) {
        pane.add("Petri Net", manager);
        pane.setTabComponentAt(pane.getTabCount() - 1, new ButtonTabComponent(pane));
        
        return true;
    }
    
    public PetriNetManager getActiveGraphManager() {
        return (PetriNetManager)pane.getSelectedComponent();
    }
    
    public final JMenuBar getMenuBar() {
        return menuBar;
    }

    public final void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public final JToolBar getToolBar() {
        return toolBar;
    }

    public final void setToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public final JTabbedPane getPane() {
        return pane;
    }

    public final void setPane(JTabbedPane pane) {
        this.pane = pane;
    }

    public final ToolAction getSelectedTool() {
        return selectedTool;
    }

    public final void setSelectedTool(ToolAction selectedTool) {
        this.selectedTool = selectedTool;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        PetriEditor editor = new PetriEditor("PetriNetter");
        
        //JTabbedPane pane = new JTabbedPane();
//        pane.add("Petri Net 1", new PetriNetManager());
//        pane.setTabComponentAt(0, new ButtonTabComponent(pane));
//        pane.add("Petri Net 2", new PetriNetManager());
//        pane.setTabComponentAt(1, new ButtonTabComponent(pane));
//        frame.add(pane);
//        frame.pack();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
    }
}
