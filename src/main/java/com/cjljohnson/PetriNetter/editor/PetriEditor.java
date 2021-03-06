/*
 * The top level lightweight swing component.
 * Encapsulates all the other components such as the menu bar,
 * tool bar and PetriNetManager components.
 * 
 * Also responsible for configuring the JFrame the program runs in.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions;
import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions.ToolAction;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.petri.PetriNetManager;
import com.mxgraph.util.mxResources;

public class PetriEditor extends JPanel{
    
    JMenuBar menuBar;
    JToolBar toolBar;
    JTabbedPane pane;
    
    ToolAction selectedTool;
    boolean highlightTransitions;
    
    public PetriEditor(String title) {
        JFrame frame = new JFrame(title);
        ImageIcon img = new ImageIcon(PetriEditor.class.getResource("/images/place.gif"));
        frame.setIconImage(img.getImage());
        
        this.menuBar = new PetriMenuBar(this);
        this.toolBar = new PetriToolBar(this, JToolBar.HORIZONTAL);
        this.pane = new JTabbedPane();
        pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.highlightTransitions = true;
        
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(pane);
        
        newPetriNet();
        
        new EditorKeyboardHandler(this);
        
        frame.setJMenuBar(menuBar);
        frame.setContentPane(this);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we)
            {
            	boolean closeWindow = closeAllTabs();
            	if (closeWindow) {
            		System.exit(0);
            	}
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    /*
     * Create new petri net in new tab.
     */
    public PetriNetManager newPetriNet() {
        PetriNetManager manager = new PetriNetManager();
        
        // Get name
        int count = 1;
        for (int i = 0; i < pane.getTabCount(); i++) {
        	String title = pane.getTitleAt(i);
        	
        	if (true) {
        		Pattern pattern = Pattern.compile("New Petri Net \\((\\d+)\\)");
        		Matcher matcher = pattern.matcher(title);
        		if (matcher.find()) {
        			int val = Integer.parseInt(matcher.group(1));
        			if (count < val) {
        				count = val;
        			}
        		}
        		
        		count++;
        	}
        }
        
        String newTitle = "New Petri Net";
        if (count > 0) {
        	newTitle += String.format(" (%d)", count);
        }
        
        pane.add(newTitle, manager);
        pane.setTabComponentAt(pane.getTabCount() - 1, new ButtonTabComponent(this));
        selectedTool.setCursor(manager.getPetriComponent());
        PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
        graph.highlightActiveTransitions(highlightTransitions);
        pane.setSelectedIndex(pane.getTabCount() - 1);
        
        return manager;
    }
    
    /*
     * Update the title of a tab
     */
    public boolean updateTitle(Component component) {
        if (!(component instanceof PetriNetManager)) {
            return false;
        }
        PetriNetManager manager = (PetriNetManager)component;
        File file = manager.getCurrentFile();
        
        String title;
        
        if (file != null) {
            title = file.getName();
        } else {
            title = "Petri Net";
        }
            
        pane.setTitleAt(pane.indexOfComponent(component), title);
        return true;
    }
    
    /*
     * Set the tab title.
     */
    public boolean setTabTitle(Component component, String newTitle) {
        pane.setTitleAt(pane.indexOfComponent(component), newTitle);
        return true;
    }
    
    /*
     * Close all tabs
     */
    public boolean closeAllTabs() {
    	while (pane.getTabCount() > 0) {
    		boolean successful = closeTab(0);
    		if (!successful) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /*
     * Close a tab with regard to save status.
     */
    public boolean closeTab(int i) {
    	
    	if (i == -1 || i >= pane.getTabCount()) {
    		throw new IndexOutOfBoundsException("Tried to close non-existant tab.");
    	}

    	PetriNetManager manager;
    	
    	manager = (PetriNetManager)pane.getComponentAt(i);
    	
    	String message = pane.getTitleAt(i) + " has been modified. Do you want to save changes?";
    	
    	if (manager != null && manager.getModified()) {
    		int selection = JOptionPane.showConfirmDialog(manager,
                    message);
    		
    		if (selection == JOptionPane.CANCEL_OPTION) {
    			return false;
    		}
    		else if (selection == JOptionPane.YES_OPTION) {
    			Component current = pane.getSelectedComponent();
    			pane.setSelectedIndex(i);
    			PetriEditorActions.getSaveAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "save"));
    			pane.setSelectedComponent(current);
    		}
    	}
    	
    pane.remove(i);
    return true;	
    }
    
    /*
     * Enable or disable transition highlighting of all Petri nets.
     */
    public void setHighlightTransitions(boolean highlight) {
    	this.highlightTransitions = highlight;
    	for (int i = 0; i < pane.getTabCount(); i++) {
    		PetriNetManager manager = (PetriNetManager)pane.getComponentAt(i);
    		PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
    		graph.highlightActiveTransitions(highlight);
    	}
    }
    
   /*
    * Binds actions to this component so this component is the event source.
    * 
    * Adapted from JGraphX example code:
    * https://github.com/jgraph/jgraphx/blob/master/examples/com/mxgraph/examples/swing/editor/BasicGraphEditor.java
    */
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, String iconUrl)
    {        		
        ImageIcon icon = (iconUrl != null) ? new ImageIcon(
                PetriEditor.class.getResource(iconUrl)) : null;
        
        return bind(name, action, icon);
    }
    
    /*
     * Binds actions to this component so this component is the event source.
     * 
     * Adapted from JGraphX example code:
     * https://github.com/jgraph/jgraphx/blob/master/examples/com/mxgraph/examples/swing/editor/BasicGraphEditor.java
     */
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, Icon icon)
    {
        AbstractAction newAction = new AbstractAction(name, icon)
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
    
    /*
     * Add a PetriNetManager to a new tab.
     */
    public boolean addPetriNet(PetriNetManager manager) {
        pane.add("Petri Net", manager);
        PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
        graph.highlightActiveTransitions(highlightTransitions);
        pane.setTabComponentAt(pane.getTabCount() - 1, new ButtonTabComponent(this));
        pane.setSelectedIndex(pane.getTabCount() - 1);
        
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
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        
        PetriEditor editor = new PetriEditor("PetriNetter");
        
    }
}
