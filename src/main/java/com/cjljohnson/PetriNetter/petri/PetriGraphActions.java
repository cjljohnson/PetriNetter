/*
 * Contains the actions relating to the PetriGraph component.
 * Mainly contains actions that are accessible by right clicking the Petri Graph.
 * 
 * @author Chris Johnson
 * @version v1.0
 */


package com.cjljohnson.PetriNetter.petri;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cjljohnson.PetriNetter.editor.DefaultFileFilter;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.reachability.ReachabilityGraph;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;

public class PetriGraphActions {

	private static final Action createReachabilityAction = new CreateReachabilityAction(
			"createReachability");

	public static Action getCreatePlaceAction(int x, int y)
	{
		return new CreatePlaceAction("createPlace", x, y);
	}

	public static Action getCreateTransitionAction(int x, int y)
	{
		return new CreateTransitionAction("createTransition", x, y);
	}

	public static Action getCreateReachabilityAction()
	{
		return createReachabilityAction;
	}

	/*
	 * get graph component from event.
	 */
	public static final mxGraph getGraph(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof mxGraphComponent)
		{
			return ((mxGraphComponent) source).getGraph();
		}

		return null;
	}

	/*
	 * Get PetriNetManager component if it is ancestor of event source.
	 */
	public static final PetriNetManager getManager(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof Component)
		{
			Component component = (Component)source;
			while (component != null && !(component instanceof PetriNetManager))
				component = component.getParent();
			return (PetriNetManager)component;
		}

		return null;
	}

	/**
	 * 
	 */
	public static class CreatePlaceAction extends AbstractAction
	{

		/**
		 * 
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 3887344511242268848L;
		private int x;
		private int y;

		/**
		 * 
		 * @param name
		 */
		public CreatePlaceAction(String name, int x, int y)
		{
			super(name);
			this.x = x;
			this.y = y;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			PetriGraph graph = (PetriGraph) getGraph(e);

			if (graph != null)
			{       	
				graph.addPlace(0, -1, x, y);
			}
		}
	}

	/**
	 * 
	 */
	public static class CreateTransitionAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7867341379592538173L;
		private int x;
		private int y;

		/**
		 * 
		 * @param name
		 */
		public CreateTransitionAction(String name, int x, int y)
		{
			super(name);
			this.x = x;
			this.y = y;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			PetriGraph graph = (PetriGraph) getGraph(e);

			if (graph != null)
			{
				graph.addTransition(x, y);
			}
		}
	}

	/**
	 * 
	 */
	public static class CreateReachabilityAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 938191175262154014L;

		/**
		 * 
		 */

		/**
		 * 
		 * @param name
		 */
		public CreateReachabilityAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{

			PetriNetManager manager = getManager(e);
			manager.createReachabilityGraph();
		}
	}

	public static class FireTransitionAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 3506472973595637136L;
		private Object cell;

		/**
		 * 
		 * @param name
		 */
		public FireTransitionAction(Object cell)
		{
			this.cell = cell;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			PetriGraph graph = (PetriGraph) getGraph(e);

			if (graph != null)
			{
				graph.fireTransition(cell);
				graph.checkEnabledFromTransition(cell);
				mxGraphComponent graphComponent = (mxGraphComponent)e.getSource();
				graphComponent.refresh();
			}
		}
	}


	public static class PositionLabelAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3887344511242268848L;
		private Object cell;
		private String position;

		/**
		 * 
		 * @param name
		 */
		public PositionLabelAction(String name, Object cell, String position)
		{
			super(name);
			this.cell = cell;
			this.position = position;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			PetriGraph graph = (PetriGraph) getGraph(e);

			if (graph != null)
			{
				graph.setCellLabelPosition(cell, position);
			}
		}
	}

	public static class PositionPlaceLabelAction extends AbstractAction
	{

		/**
		 * 
		 */

		/**
		 * 
		 */
		private static final long serialVersionUID = 3887344511242268848L;
		private Object cell;
		private double x;
		private double y;

		/**
		 * 
		 * @param name
		 */
		public PositionPlaceLabelAction(String name, Object cell, double x, double y)
		{
			super(name);
			this.cell = cell;
			this.x = x;
			this.y = y;
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			PetriGraph graph = (PetriGraph) getGraph(e);

			if (graph != null)
			{
				graph.setPlaceLabelPosition(cell, x, y);
			}
		}
	}

}

