/**
 * Manages a single instance of a Petri net.  
 */

package com.cjljohnson.PetriNetter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;

import com.cjljohnson.PetriNetter.editor.PetriEditor;
import com.cjljohnson.PetriNetter.model.Arc;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Place;
import com.cjljohnson.PetriNetter.model.Transition;
import com.cjljohnson.PetriNetter.reachability.MarkingTableModel;
import com.cjljohnson.PetriNetter.reachability.ReachRightClick;
import com.cjljohnson.PetriNetter.reachability.ReachabilityChange;
import com.cjljohnson.PetriNetter.reachability.ReachabilityGraph;
import com.cjljohnson.PetriNetter.view.PetriEdgeFunction;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxGraphModel.mxChildChange;
import com.mxgraph.model.mxGraphModel.mxRootChange;
import com.mxgraph.model.mxGraphModel.mxValueChange;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStyleRegistry;

public class PetriNetManager extends JPanel {
	
	private mxGraphComponent petriComponent;
	private mxGraphComponent reachComponent;
	private JSplitPane splitPane;
	private boolean reachValid;
	private boolean modified;
	private File currentFile;
	private mxUndoManager undoManager;
	private Object[] finalisedNet;
	
	static {
	    mxStyleRegistry.putValue("PETRI_STYLE", new PetriEdgeFunction());
	}
	
	protected mxIEventListener undoHandler = new mxIEventListener()
    {
        public void invoke(Object source, mxEventObject evt)
        {
            undoManager.undoableEditHappened((mxUndoableEdit) evt
                    .getProperty("edit"));
        }
    };
	
	protected mxIEventListener changeTracker = new mxIEventListener()
	{
		public void invoke(Object source, mxEventObject evt)
		{
//			reachValid = false;
//			if (reachComponent != null)
//				reachComponent.setEnabled(false);
			setModified(true);
			System.out.println(evt.getProperties());
			if(reachComponent != null && (changeGraphModel(evt) || checkMarking(evt))) {
				ArrayList<Object> changes = (ArrayList<Object>) evt.getProperty("changes");
				for (Object change : changes) {
					if (change instanceof ReachabilityChange || change instanceof mxRootChange) {
						return;
					}
				}
			    
			    // Add reach change to undoable edit
			    mxGraphModel model = (mxGraphModel)petriComponent.getGraph().getModel();
			    
			    ReachabilityChange reachChange = new ReachabilityChange(PetriNetManager.this, false, null, null);
			    reachChange.execute();
			    mxUndoableEdit edit = (mxUndoableEdit) evt
	                    .getProperty("edit");
			    edit.add(reachChange);
			}
		}
		
		private boolean changeGraphModel(mxEventObject evt) {
		    ArrayList<Object> changes = (ArrayList<Object>) evt.getProperty("changes");
		    mxUndoableEdit edit = (mxUndoableEdit) evt
                    .getProperty("edit");
		    if (changes != null) {
		        for (Object change : changes) {
		            // Graph nodes added/deleted
		            if (change instanceof mxChildChange) {
		                return true;
		            }
		            if (change instanceof mxValueChange) {
		                mxValueChange valueChange = (mxValueChange)change;
		                // Arc value change
		                if (valueChange.getValue() instanceof Arc) {
		                    return true;
		                }
		                // Capacity change
		                if (valueChange.getValue() instanceof Place) {
                            Place newPlace = (Place)valueChange.getValue();
                            Place oldPlace = (Place)valueChange.getPrevious();
                            if (newPlace.getCapacity() != oldPlace.getCapacity()) {
                                return true;
                            }
                        }
		            }
		        }
		    }
		    return false;
		}
		
		private boolean checkMarking(mxEventObject evt) {
		    ArrayList<Object> changes = (ArrayList<Object>) evt.getProperty("changes");
		    if (changes != null) {
                for (Object change : changes) {
                    if (change instanceof mxValueChange) {
                        boolean isReachableMarking = ((ReachabilityGraph)reachComponent.getGraph())
                                .isReachableMarking(((PetriGraph)petriComponent.getGraph()).getPlaceTokens());
                        ((ReachabilityGraph)reachComponent.getGraph()).updateActiveMarking(); // Always update marking
//                        if (isReachableMarking) {
//                            
//                            return false;
//                        } else {
//                            return true;
//                        }
                        
                    }
                }
		    }
		    return false;
		}
	};
	
	public PetriNetManager() {
        this(new PetriGraph());
    }
	

	public PetriNetManager(final PetriGraph graph) {
		init(graph);
		reachValid = false;
		
		undoManager = new mxUndoManager();
		// Adds the command history to the model and view
        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        // Keeps the selection in sync with the command history
        mxIEventListener undoHandler = new mxIEventListener()
        {
            public void invoke(Object source, mxEventObject evt)
            {
                List<mxUndoableChange> changes = ((mxUndoableEdit) evt
                        .getProperty("edit")).getChanges();
                graph.setSelectionCells(graph
                        .getSelectionCellsForChanges(changes));
            }
        };

        undoManager.addListener(mxEvent.UNDO, undoHandler);
        undoManager.addListener(mxEvent.REDO, undoHandler);
	}
	
	private void init(final PetriGraph graph) {
		
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
		graph.setKeepEdgesInBackground(true);
		
		graph.addListener(mxEvent.CELL_CONNECTED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                mxCell connectionCell = (mxCell) evt.getProperty("edge");
                if (connectionCell.getSource() != null && connectionCell.getTarget() != null) {
                    graph.checkEnabledFromEdge(connectionCell);
                    graph.refresh();
                }
                //disableReachComponent();
            }
        });
		
		graph.addListener(mxEvent.CELLS_ADDED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
            	//disableReachComponent();
            }
        });
		
		graph.addListener(mxEvent.CELLS_REMOVED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object[] cells = (Object[]) evt.getProperty("cells");
                //disableReachComponent();
                for (Object cell : cells) {
                	graph.checkEnabledFromEdge(cell);
                }
                graph.refresh();
            }
        });
		
		graph.getModel().addListener(mxEvent.CHANGE, changeTracker);
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
		
		
		// Tool listener
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {

            public void mouseClicked(MouseEvent e)
            {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    doToolAction(e);
                }
            }
        });

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
		
		// Zoom listener
		graphComponent.addMouseWheelListener(new MouseWheelListener()
        {
            /**
             * 
             */
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if (e.isControlDown())
                {
                    if (e.getWheelRotation() < 0)
                    {
                        graphComponent.zoomIn();
                    }
                    else
                    {
                        graphComponent.zoomOut();
                    }
                }
            }

        });

		new mxRubberband(graphComponent);
		new mxKeyboardHandler(graphComponent);
	}
	
	private void doToolAction(MouseEvent e) {
	    Component c = e.getComponent();
	    while (c != null && !(c instanceof PetriEditor)) {
	        c = c.getParent();
	    }
	    if (c instanceof PetriEditor) {
	        PetriEditor editor = (PetriEditor)c;
	        editor.getSelectedTool().onClick(e, this);
	    }
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
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				PetriNetManager.class.getResource(iconUrl)) : null)
//		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? null : null)
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
	
	public void createReachabilityGraph() {
		// Ask user reachability size
		int iterations = -1;
		JPanel popupPanel = new JPanel();
		JTextField iterTF = new JTextField();
		iterTF.setText("200");
		
		popupPanel.setLayout(new BoxLayout(popupPanel, BoxLayout.Y_AXIS));
		popupPanel.add(new JLabel("Max iterations (1-1000):"));
		popupPanel.add(iterTF);
		
		
		while (iterations == -1) {
			int result = JOptionPane.showConfirmDialog(null, popupPanel, "Reachability Graph",
	                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		    if (result == JOptionPane.OK_OPTION) {
		    	try {
	                iterations = Integer.parseInt(iterTF.getText());
	            } catch (Exception e) {
	            }
		    	if (iterations < 1 || iterations > 1000) {
		    		iterations = -1;
		    		JOptionPane.showMessageDialog(null, "Max iterations must be between 1 and 1000.");
		    	}
		    } else {
		    	return;
		    }
		}
		
		// Finalise net
		finaliseNet();
		
		JSplitPane splitPane = this.splitPane;
		if (splitPane == null) 
		{
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					petriComponent, null);
			splitPane.setOneTouchExpandable(false);
			splitPane.setDividerLocation(Math.min(getWidth() / 2, getWidth() / 2));
			splitPane.setResizeWeight(0.5);
			//removeAll();
			//add(splitPane, BorderLayout.CENTER);
		}
		
		final ReachabilityGraph reach = new ReachabilityGraph((PetriGraph)petriComponent.getGraph(), iterations);
		
		// define layout
		mxFastOrganicLayout layout = new mxFastOrganicLayout(reach);
		//mxIGraphLayout layout = new mxEdgeLabelLayout(reach);
        
        layout.setDisableEdgeStyle(false);
        //layout.setForceConstant(reach.getChildVertices(reach.getDefaultParent()).length * 2);
        //layout.setMinDistanceLimit(40);
        // layout graph
        layout.execute(reach.getDefaultParent());
        
        final mxGraphComponent reachComponent = new mxGraphComponent(reach);
        
        reachComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {

            public void mouseReleased(MouseEvent e)
            {
                Object obj = reachComponent.getCellAt(e.getX(), e.getY());

                if (reachValid && obj != null && obj instanceof mxCell && e.getClickCount() == 2)
                {
                    
                    Object value = ((mxCell) obj).getValue();
                    if (reach.getMarkingMap(obj) != null)
                    {
                        reach.setActiveState(obj);
                    }
                }
            }
        });
        reachComponent.setConnectable(false);
        reachComponent.setToolTips(true);
        //reachComponent.setGridVisible(true);
		// Sets the background to white
        reachComponent.getViewport().setOpaque(true);
        reachComponent.getViewport().setBackground(Color.WHITE);
        reachComponent.setBackground(Color.WHITE);
        reachComponent.setDragEnabled(false);
        
        
        reachComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {

            /**
             * 
             */
            public void mousePressed(MouseEvent e)
            {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            /**
             * 
             */
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger() && reachValid)
                {
                 // Reach Right Click
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                            reachComponent);
                    ReachRightClick menu = new ReachRightClick(PetriNetManager.this, reachComponent, pt.x, pt.y);
                    menu.show(reachComponent, pt.x, pt.y);

                    e.consume();
                }
            }

        });
        
        //this.reachComponent = reachComponent;
		
		
		
		// TABLE
		PetriGraph graph = ((PetriGraph)petriComponent.getGraph());
		Object[] places = ((PetriGraph)petriComponent.getGraph()).getPlaces();
		System.out.println(Arrays.toString(places));
		String[] columnNames = new String[places.length];
		int i = 0;
		for (Object place : places) {
			columnNames[i] = "p" + ((PetriGraph)petriComponent.getGraph()).getCellMarkingName(place);
			i++;
		}
		JTable table = new JTable(new MarkingTableModel(reach.getMarkings(), graph));
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table.getDefaultRenderer(Object.class);
		renderer.setHorizontalAlignment( SwingConstants.CENTER );
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
	    .setHorizontalAlignment(SwingConstants.CENTER);
		table.setFillsViewportHeight(true);
		JScrollPane tableScrollPane = new JScrollPane(table);
		
		reach.setMinimumGraphSize(reach.getGraphBounds());
		Dimension dim = new Dimension(400, 300);
		//new Dimension(reach.getGraphBounds().getX(), reach.getGraphBounds().getY())
		reachComponent.setPreferredSize(dim);
		
		// Close button
		
		
		JSplitPane reachSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, reachComponent, tableScrollPane);
		reachSplit.setResizeWeight(1.0);
		
		splitPane.setRightComponent(reachSplit);
		
		
		// Create undoable edit
		mxGraphModel model = (mxGraphModel)(petriComponent.getGraph()).getModel();
		model.execute(new ReachabilityChange(this, true, reachComponent, splitPane));
		
		
		reachSplit.setDividerLocation(0.9);
		
//		petriComponent.getGraph().setCellsDisconnectable(false);
//		petriComponent.getGraph().setCellsDeletable(false);
//		petriComponent.getGraph().setCellsCloneable(false);
		
	}
	
	public void calcBoundedness() {
		if (reachValid) {
			ReachabilityGraph reach = (ReachabilityGraph) reachComponent.getGraph();
			reach.showBounded();
		}
	}
	
	public void calcLiveness() {
		if (reachValid) {
			ReachabilityGraph reach = (ReachabilityGraph) reachComponent.getGraph();
			reach.showLive();
		}
	}
	
	public void disableReachComponent() {
		reachValid = false;
		if (reachComponent != null) {
			reachComponent = null;
			splitPane = null;
			removeAll();
			add(petriComponent, BorderLayout.CENTER);
			validate();
		}
			
//			reachComponent.setEnabled(false);
//			if (splitPane != null && splitPane.getRightComponent() instanceof mxGraphComponent) {
//				splitPane.setRightComponent(new JPanel());
//			}
	}
	
	public void setReachComponent(boolean reachValid, mxGraphComponent reachComponent, JSplitPane splitPane) {
		this.reachValid = reachValid;
		this.reachComponent = reachComponent;
		
		petriComponent.setEnabled(!reachValid);
		petriComponent.setGridVisible(!reachValid);
		petriComponent.getGraph().setCellsSelectable(!reachValid);
		
		if (this.splitPane != splitPane) {
			if (splitPane == null) {
				removeAll();
				add(petriComponent, BorderLayout.CENTER);
				validate();
			} else {
				removeAll();
				add(splitPane, BorderLayout.CENTER);
				splitPane.setLeftComponent(petriComponent);
				validate();
				System.out.println(splitPane.getLeftComponent());
			}
			this.splitPane = splitPane;
		}
	}
	
	public void finaliseNet() {
		mxGraph graph = petriComponent.getGraph();
		finalisedNet = graph.cloneCells(graph.getChildCells(graph.getDefaultParent()));
	}
	
	public boolean revertToFinalised() {
		if (finalisedNet != null) {
			mxGraph graph = petriComponent.getGraph();
			mxCell root = new mxCell();
			try {
				graph.getModel().beginUpdate();

			root.insert(new mxCell());
			graph.getModel().setRoot(root);
			graph.addCells(graph.cloneCells(finalisedNet));
			} finally {
				graph.getModel().endUpdate();
			}
			return true;
		} else {
			return false;
		}
	}
	
	
	public mxGraphComponent getPetriComponent() {
		return petriComponent;
	}

	public mxGraphComponent getReachComponent() {
		return reachComponent;
	}
	
	public File getCurrentFile() {
	    return currentFile;
	}
	
	public void setCurrentFile(File file) {
	    currentFile = file;
	}
	
	public boolean getModified() {
		return modified;
	}
	
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	public boolean reachValid() {
		return reachValid;
	}
	public final mxUndoManager getUndoManager() {
        return undoManager;
    }

    public final void setUndoManager(mxUndoManager undoManager) {
        this.undoManager = undoManager;
    }  

    public JSplitPane getSplitPane() {
		return splitPane;
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
		//frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
	}

}
