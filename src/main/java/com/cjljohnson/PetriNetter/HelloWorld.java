package com.cjljohnson.PetriNetter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cjljohnson.PetriNetter.model.Arc;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Transition;
import com.cjljohnson.PetriNetter.view.PetriEdgeFunction;
//import com.mxgraph.examples.swing.editor.BasicGraphEditor;
//import com.mxgraph.examples.swing.editor.EditorPopupMenu;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxMultiplicity;
import com.mxgraph.view.mxStyleRegistry;
import com.mxgraph.view.mxStylesheet;

public class HelloWorld extends JFrame
{
	mxGraphComponent graphComponent;
	JTabbedPane tabbedPane;





    /**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;
	
	static
    {
        try
        {
            mxResources.add("com/mxgraph/examples/swing/resources/editor");
        }
        catch (Exception e)
        {
            // ignore
        }
    }

	public HelloWorld()
	{
		super("Petri Netter");
		
		mxStyleRegistry.putValue("PETRI_STYLE", new PetriEdgeFunction());
		
		//Hardcode test elements
		Document xmlDocument = mxDomUtils.createDocument();
		
		// Places
		Element place1 = xmlDocument.createElement("Place");
		place1.setAttribute("tokens", "5");
		place1.setAttribute("capacity", "10");
		Element place2 = xmlDocument.createElement("Place");
		place2.setAttribute("tokens", "3");
		place2.setAttribute("capacity", "20");
		xmlDocument.appendChild(place1);
		place1.appendChild(place2);
		
		// Transitions
		Element transition1 = xmlDocument.createElement("Transition");
		Element transition2 = xmlDocument.createElement("Transition");
		
		// Arcs
		Arc arc1 = new Arc(3);
		Arc arc2 = new Arc(2);
		Arc arc3 = new Arc(2);
		Arc arc4 = new Arc(4);
		Arc arc5 = new Arc(4);
		Arc arc6 = new Arc(4);
		
		final PetriGraph graph = new PetriGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> placeStyle = new Hashtable<String, Object>();
		placeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_OPACITY, 100);
		placeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		placeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		placeStyle.put(mxConstants.STYLE_NOLABEL, false);
		placeStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_PERIMETER_SPACING, 4);
		stylesheet.putCellStyle("PLACE", placeStyle);
		
		Hashtable<String, Object> placeCapacityStyle = new Hashtable<String, Object>();
		placeCapacityStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeCapacityStyle.put(mxConstants.STYLE_FILLCOLOR, "none");
		placeCapacityStyle.put(mxConstants.STYLE_STROKECOLOR, "none");
		stylesheet.putCellStyle("CAPACITY", placeCapacityStyle);
		
		Hashtable<String, Object> transitionStyle = new Hashtable<String, Object>();
		transitionStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		transitionStyle.put(mxConstants.STYLE_OPACITY, 100);
		transitionStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		transitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		transitionStyle.put(mxConstants.STYLE_NOLABEL, false);
		transitionStyle.put(mxConstants.STYLE_PERIMETER_SPACING, 4);
		stylesheet.putCellStyle("TRANSITION", transitionStyle);
		

        Hashtable<String, Object> activeTransitionStyle = new Hashtable<String, Object>();
        activeTransitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
        stylesheet.putCellStyle("ACTIVETRANSITION", activeTransitionStyle);
		
		Hashtable<String, Object> arcStyle = new Hashtable<String, Object>();
		arcStyle.put(mxConstants.STYLE_OPACITY, 100);
		arcStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		arcStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		arcStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		arcStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		stylesheet.putCellStyle("ARC", arcStyle);
		
		applyEdgeDefaults(graph);
		
		
		
		
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
			
			graph.setCellsResizable(false);
			graph.setMultigraph(false);
			graph.setAllowDanglingEdges(false);
			graph.setSplitEnabled(false);
			graph.setDropEnabled(false);
			graph.checkEnabledTransitions();
			graph.setEdgeLabelsMovable(false);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
//		JPanel panel = new JPanel();
//		getContentPane().add(panel);
		
		graphComponent = new mxGraphComponent(graph);
//		panel.add(graphComponent);
		initialiseGraphComponent(graphComponent);
		
		tabbedPane = new JTabbedPane();
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel("YEE"));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				graphComponent, null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);
		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 50);
		graphComponent.setMinimumSize(minimumSize);
		
		tabbedPane.addTab("Petri", null, splitPane,
                "Petri Graph");
		getContentPane().add(tabbedPane);
		
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
		
		
//		PetriGraph graph2 = new PetriGraph(xmlDocument);
//		graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent())));
//		mxStylesheet stylesheet2 = graph2.getStylesheet();
//		stylesheet2.putCellStyle("PLACE", placeStyle);
//		stylesheet2.putCellStyle("TRANSITION", transitionStyle);
//		stylesheet2.putCellStyle("ACTIVETRANSITION", activeTransitionStyle);
//		stylesheet2.putCellStyle("ARC", arcStyle);
//		
//		
//		mxGraphComponent graphComponent2 = new mxGraphComponent(graph2);
//		initialiseGraphComponent(graphComponent2);
//		graphComponent2.refresh();
//		panel.add(graphComponent2);
		
		
//		Map<String, Integer> tokenMap = graph.getPlaceTokens();
//		tokenMap.put("6", 10);
//		graph.setPlaceTokens(tokenMap);
//		graphComponent.refresh();
		
		pack();
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

		//final mxGraph graph2 = new PetriGraph(xmlDocument);
		//graphComponent.setGraph(graph2);

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

		// Installs the popup menu in the graph component

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
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
				if (e.isPopupTrigger())
				{
					showGraphPopupMenu(e);
				}
			}

		});


		new mxRubberband(graphComponent);
		new mxKeyboardHandler(graphComponent);
	}
	
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		PetriRightClick menu = new PetriRightClick(this, pt.x, pt.y);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}
	
	/**
     * 
     * @param name
     * @param action
     * @return a new Action bound to the specified string name
     */
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
				action.actionPerformed(new ActionEvent(graphComponent, e
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
	
	private void applyEdgeDefaults(mxGraph graph) {
	    // Settings for edges
	    Map<String, Object> edge = new HashMap<String, Object>();
	    //edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CURVE);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_STROKEWIDTH, 2);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_EDGE, "PETRI_STYLE");

	    graph.getStylesheet().setDefaultEdgeStyle(edge);
	}
	
	public final mxGraphComponent getGraphComponent() {
	    return graphComponent;
	}
	
	

	public static void main(String[] args)
	{
		
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
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(400, 400);
		frame.setVisible(true);
		
//		HelloWorld frame2 = new HelloWorld();
//		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		//frame.setSize(400, 400);
//		frame2.setVisible(true);
	}

}
