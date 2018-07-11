/**
 * Manages a single instance of a Petri net.  
 */

package com.cjljohnson.PetriNetter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.cjljohnson.PetriNetter.model.Arc;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Transition;
import com.cjljohnson.PetriNetter.view.PetriEdgeFunction;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStyleRegistry;

public class PetriNetManager extends JPanel {
	
	mxGraphComponent petriComponent;
	mxGraphComponent reachComponent;
	JTabbedPane tabbedPane;
	
	public PetriNetManager() {
		initPetriGraph();
	}
	
	private void initPetriGraph() {
		mxStyleRegistry.putValue("PETRI_STYLE", new PetriEdgeFunction());
		
		final PetriGraph graph = new PetriGraph();
		initialisePetriGraph(graph);
		petriComponent = new mxGraphComponent(graph);
		initialiseGraphComponent(petriComponent);
		
		setLayout(new BorderLayout());
        add(petriComponent, BorderLayout.CENTER);
	}
	
	private void initialisePetriGraph(final PetriGraph graph) {
		graph.setCellsResizable(false);
		graph.setMultigraph(false);
		graph.setAllowDanglingEdges(false);
		graph.setSplitEnabled(false);
		graph.setDropEnabled(false);
		graph.checkEnabledTransitions();
		graph.setEdgeLabelsMovable(false);
		
		graph.addListener(mxEvent.CELL_CONNECTED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                mxCell connectionCell = (mxCell) evt.getProperty("edge");
                if (connectionCell.getSource() != null && connectionCell.getTarget() != null) {
                    graph.checkEnabledFromEdge(connectionCell);
                    graph.refresh();
                }
            }
        });
		
		graph.addListener(mxEvent.CELLS_ADDED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
            }
        });
		
		graph.addListener(mxEvent.CELLS_REMOVED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object[] cells = (Object[]) evt.getProperty("cells");
                for (Object cell : cells) {
                	graph.checkEnabledFromEdge(cell);
                }
                graph.refresh();
            }
        });
	}
	
	public void initialiseGraphComponent(final mxGraphComponent graphComponent) {
		final PetriGraph graph = (PetriGraph) graphComponent.getGraph();
		
		//getContentPane().add(graphComponent);
		graphComponent.setGridVisible(true);
		// Sets the background to white
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);
		graphComponent.setBackground(Color.WHITE);
		graphComponent.setEnterStopsCellEditing(true);
		graphComponent.setDragEnabled(false);

		// Double click listener
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			public void mouseReleased(MouseEvent e)
			{
				Object obj = graphComponent.getCellAt(e.getX(), e.getY());

				if (obj != null && obj instanceof mxCell && e.getClickCount() == 2)
				{
					Object value = ((mxCell) obj).getValue();
					if (value instanceof Transition)
					{
							if (graph.fireTransition(obj)) {
								graph.checkEnabledFromTransition(obj);
								graphComponent.refresh();
							}
					}
				}
			}
		});

		// Right click listener
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			public void mousePressed(MouseEvent e)
			{
				// Handles context menu on the Mac where the trigger is on mousepressed
				mouseReleased(e);
			}

			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					showGraphPopupMenu(e);
				}
			}

		});

		new mxRubberband(graphComponent);
		new mxKeyboardHandler(graphComponent);
	}
	
	private void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				petriComponent);
		PetriRightClick menu = new PetriRightClick(this, pt.x, pt.y);
		menu.show(petriComponent, pt.x, pt.y);

		e.consume();
	}
	
	public Action bind(String name, final Action action)
    {
        return bind(name, action, null);
    }

	
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl)
	{
		//AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
		//		BasicGraphEditor.class.getResource(iconUrl)) : null)
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? null : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(petriComponent, e
						.getID(), e.getActionCommand()));
			}
		};
		
		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		
		return newAction;
	}
	
	@SuppressWarnings("serial")
	public Action bind2(String name, final Action action, String iconUrl)
	{
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? null : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(tabbedPane, e
						.getID(), e.getActionCommand()));
			}
		};
		
		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		
		return newAction;
	}
	
	
	
	public mxGraphComponent getPetriComponent() {
		return petriComponent;
	}

	public mxGraphComponent getReachComponent() {
		return reachComponent;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
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
		
		PetriNetManager manager = new PetriNetManager();
		PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
		Object parent = graph.getDefaultParent();
		Arc arc1 = new Arc(3);
		Arc arc2 = new Arc(2);
		Arc arc3 = new Arc(2);
		Arc arc4 = new Arc(4);
		Arc arc5 = new Arc(4);
		Arc arc6 = new Arc(4);
		try
		{
			graph.getModel().beginUpdate();
			Object v1 = graph.addPlace(5, 10, 20, 20);
			Object v2 = graph.addTransition(240, 150);
			Object v3 = graph.addTransition(140, 150);
			Object t3 = graph.addTransition(60, 200);
			Object v4 = graph.addPlace(3, 20, 280, 280);
			graph.insertEdge(parent, null, arc1, v1, v2, null);
			graph.insertEdge(parent, null, arc2, v3, v1, null);
			graph.insertEdge(parent, null, arc3, v2, v4, null);
			graph.insertEdge(parent, null, arc4, v4, v3, null);
			
			graph.insertEdge(parent, null, arc5, v4, t3, null);
			graph.insertEdge(parent, null, arc6, t3, v1, null);
			
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		JFrame frame = new JFrame("Petri Netter");
		frame.setContentPane(manager);
		frame.setJMenuBar(new JMenuBar());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(600, 600);
		frame.setLocationRelativeTo(null);
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
	}

}
