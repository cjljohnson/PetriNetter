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

	public static final mxGraph getGraph(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof mxGraphComponent)
		{
			return ((mxGraphComponent) source).getGraph();
		}

		return null;
	}

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

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class NewAction extends AbstractAction
	{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraphComponent graphComponent = (mxGraphComponent)e.getSource();
			if (JOptionPane.showConfirmDialog(graphComponent,
					mxResources.get("loseChanges")) == JOptionPane.YES_OPTION)
			{
				mxGraph graph = graphComponent.getGraph();

				// Check modified flag and display save dialog
				mxCell root = new mxCell();
				root.insert(new mxCell());
				graph.getModel().setRoot(root);
				graphComponent.zoomAndCenter();
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class SaveAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected boolean showDialog;

		/**
		 * 
		 */
		protected String lastDir = null;

		/**
		 * 
		 */
		public SaveAction(boolean showDialog)
		{
			this.showDialog = showDialog;
		}

		/**
		 * Saves XML+PNG format.
		 */
		protected void saveXmlPng(mxGraphComponent graphComponent, String filename,
				Color bg) throws IOException
		{
			mxGraph graph = graphComponent.getGraph();

			// Creates the image for the PNG file
			BufferedImage image = mxCellRenderer.createBufferedImage(graph,
					null, 1, bg, graphComponent.isAntiAlias(), null,
					graphComponent.getCanvas());

			// Creates the URL-encoded XML data
			mxCodec codec = new mxCodec();
			String xml = URLEncoder.encode(
					mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
			mxPngEncodeParam param = mxPngEncodeParam
					.getDefaultEncodeParam(image);
			param.setCompressedText(new String[] { "mxGraphModel", xml });

			// Saves as a PNG file
			FileOutputStream outputStream = new FileOutputStream(new File(
					filename));
			try
			{
				mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
						param);

				if (image != null)
				{
					encoder.encode(image);
				}
				else
				{
					JOptionPane.showMessageDialog(graphComponent,
							mxResources.get("noImageData"));
				}
			}
			finally
			{
				outputStream.close();
			}
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			//BasicGraphEditor editor = getEditor(e);

			//if (editor != null)
			//{
			mxGraphComponent graphComponent = (mxGraphComponent)e.getSource();
			mxGraph graph = graphComponent.getGraph();
			FileFilter selectedFilter = null;
			DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
					"PNG+XML " + mxResources.get("file") + " (.png)");
			FileFilter vmlFileFilter = new DefaultFileFilter(".html",
					"VML " + mxResources.get("file") + " (.html)");
			String filename = null;
			boolean dialogShown = false;

			//if (showDialog || editor.getCurrentFile() == null)
			if (showDialog)
			{
				String wd;

				if (lastDir != null)
				{
					wd = lastDir;
				}
				//                   else if (editor.getCurrentFile() != null)
				//                   {
				//                       wd = editor.getCurrentFile().getParent();
				//                   }
				else
				{
					wd = System.getProperty("user.dir");
				}

				JFileChooser fc = new JFileChooser(wd);

				// Adds the default file format
				FileFilter defaultFilter = xmlPngFilter;
				fc.addChoosableFileFilter(defaultFilter);

				// Adds special vector graphics formats and HTML
				fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
						"mxGraph Editor " + mxResources.get("file")
						+ " (.mxe)"));
				fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
						"Graph Drawing " + mxResources.get("file")
						+ " (.txt)"));
				fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
						"SVG " + mxResources.get("file") + " (.svg)"));
				fc.addChoosableFileFilter(vmlFileFilter);
				fc.addChoosableFileFilter(new DefaultFileFilter(".html",
						"HTML " + mxResources.get("file") + " (.html)"));

				// Adds a filter for each supported image format
				Object[] imageFormats = ImageIO.getReaderFormatNames();

				// Finds all distinct extensions
				HashSet<String> formats = new HashSet<String>();

				for (int i = 0; i < imageFormats.length; i++)
				{
					String ext = imageFormats[i].toString().toLowerCase();
					formats.add(ext);
				}

				imageFormats = formats.toArray();

				for (int i = 0; i < imageFormats.length; i++)
				{
					String ext = imageFormats[i].toString();
					fc.addChoosableFileFilter(new DefaultFileFilter("."
							+ ext, ext.toUpperCase() + " "
									+ mxResources.get("file") + " (." + ext + ")"));
				}

				// Adds filter that accepts all supported image formats
				fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
						mxResources.get("allImages")));
				fc.setFileFilter(defaultFilter);
				int rc = fc.showDialog(null, mxResources.get("save"));
				dialogShown = true;

				if (rc != JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				else
				{
					lastDir = fc.getSelectedFile().getParent();
				}

				filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();

				if (selectedFilter instanceof DefaultFileFilter)
				{
					String ext = ((DefaultFileFilter) selectedFilter)
							.getExtension();

					if (!filename.toLowerCase().endsWith(ext))
					{
						filename += ext;
					}
				}

				if (new File(filename).exists()
						&& JOptionPane.showConfirmDialog(graphComponent,
								mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
				{
					return;
				}

				try
				{
					String ext = filename
							.substring(filename.lastIndexOf('.') + 1);

					if (ext.equalsIgnoreCase("svg"))
					{
						mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
								.drawCells(graph, null, 1, null,
										new CanvasFactory()
								{
									public mxICanvas createCanvas(
											int width, int height)
									{
										mxSvgCanvas canvas = new mxSvgCanvas(
												mxDomUtils.createSvgDocument(
														width, height));
										canvas.setEmbedded(true);

										return canvas;
									}

								});

						mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()),
								filename);
					}
					else if (selectedFilter == vmlFileFilter)
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createVmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("html"))
					{
						mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
								.createHtmlDocument(graph, null, 1, null, null)
								.getDocumentElement()), filename);
					}
					else if (ext.equalsIgnoreCase("mxe")
							|| ext.equalsIgnoreCase("xml"))
					{
						mxCodec codec = new mxCodec();
						String xml = mxXmlUtils.getXml(codec.encode(graph
								.getModel()));

						mxUtils.writeFile(xml, filename);

					}
					else if (ext.equalsIgnoreCase("txt"))
					{
						String content = mxGdCodec.encode(graph);

						mxUtils.writeFile(content, filename);
					}
					else
					{
						Color bg = null;

						if ((!ext.equalsIgnoreCase("gif") && !ext
								.equalsIgnoreCase("png"))
								|| JOptionPane.showConfirmDialog(
										graphComponent, mxResources
										.get("transparentBackground")) != JOptionPane.YES_OPTION)
						{
							bg = graphComponent.getBackground();
						}

						if (selectedFilter == xmlPngFilter)
						{
							saveXmlPng(graphComponent, filename, bg);
						}
						else
						{
							BufferedImage image = mxCellRenderer
									.createBufferedImage(graph, null, 1, bg,
											graphComponent.isAntiAlias(), null,
											graphComponent.getCanvas());

							if (image != null)
							{
								ImageIO.write(image, ext, new File(filename));
							}
							else
							{
								JOptionPane.showMessageDialog(graphComponent,
										mxResources.get("noImageData"));
							}
						}
					}
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	public static class OpenAction extends AbstractAction
	{
		/**
		 * 
		 */
		protected String lastDir;

		/**
		 * 
		 */

		protected void resetGraphComponent(mxGraphComponent graphComponent) {
			graphComponent.zoomAndCenter();
		}

		/**
		 * Reads XML+PNG format.
		 */
		protected void openXmlPng(mxGraphComponent graphComponent, File file)
				throws IOException
		{
			Map<String, String> text = mxPngTextDecoder
					.decodeCompressedText(new FileInputStream(file));

			if (text != null)
			{
				String value = text.get("mxGraphModel");

				if (value != null)
				{
					Document document = mxXmlUtils.parseXml(URLDecoder.decode(
							value, "UTF-8"));
					mxCodec codec = new mxCodec(document);
					codec.decode(document.getDocumentElement(), 
							graphComponent.getGraph().getModel());
					resetGraphComponent(graphComponent);
					return;
				}
			}

			JOptionPane.showMessageDialog(graphComponent,
					mxResources.get("imageContainsNoDiagramData"));
		}

		/**
		 * @throws IOException
		 *
		 */
		protected void openGD(mxGraphComponent graphComponent, File file,
				String gdText)
		{
			mxGraph graph = graphComponent.getGraph();

			// Replaces file extension with .mxe
			String filename = file.getName();
			filename = filename.substring(0, filename.length() - 4) + ".mxe";

			if (new File(filename).exists()
					&& JOptionPane.showConfirmDialog(graphComponent,
							mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
			{
				return;
			}

			((mxGraphModel) graph.getModel()).clear();
			mxGdCodec.decode(gdText, graph);
			graphComponent.zoomAndCenter();
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraphComponent graphComponent = (mxGraphComponent)e.getSource();

			if (JOptionPane.showConfirmDialog(graphComponent,
					mxResources.get("loseChanges")) == JOptionPane.YES_OPTION)
			{
				mxGraph graph = graphComponent.getGraph();

				if (graph != null)
				{
					String wd = (lastDir != null) ? lastDir : System
							.getProperty("user.dir");

					JFileChooser fc = new JFileChooser(wd);

					// Adds file filter for supported file format
					DefaultFileFilter defaultFilter = new DefaultFileFilter(
							".mxe", mxResources.get("allSupportedFormats")
							+ " (.mxe, .png, .vdx)")
					{

						public boolean accept(File file)
						{
							String lcase = file.getName().toLowerCase();

							return super.accept(file)
									|| lcase.endsWith(".png")
									|| lcase.endsWith(".vdx");
						}
					};
					fc.addChoosableFileFilter(defaultFilter);

					fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
							"mxGraph Editor " + mxResources.get("file")
							+ " (.mxe)"));
					fc.addChoosableFileFilter(new DefaultFileFilter(".png",
							"PNG+XML  " + mxResources.get("file")
							+ " (.png)"));

					// Adds file filter for VDX import
					fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
							"XML Drawing  " + mxResources.get("file")
							+ " (.vdx)"));

					// Adds file filter for GD import
					fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
							"Graph Drawing  " + mxResources.get("file")
							+ " (.txt)"));

					fc.setFileFilter(defaultFilter);

					int rc = fc.showDialog(null,
							mxResources.get("openFile"));

					if (rc == JFileChooser.APPROVE_OPTION)
					{
						lastDir = fc.getSelectedFile().getParent();

						try
						{
							if (fc.getSelectedFile().getAbsolutePath()
									.toLowerCase().endsWith(".png"))
							{
								openXmlPng(graphComponent, fc.getSelectedFile());
							}
							else if (fc.getSelectedFile().getAbsolutePath()
									.toLowerCase().endsWith(".txt"))
							{
								openGD(graphComponent, fc.getSelectedFile(),
										mxUtils.readFile(fc
												.getSelectedFile()
												.getAbsolutePath()));
							}
							else
							{
								Document document = mxXmlUtils
										.parseXml(mxUtils.readFile(fc
												.getSelectedFile()
												.getAbsolutePath()));

								mxCodec codec = new mxCodec(document);
								codec.decode(
										document.getDocumentElement(),
										graph.getModel());
							}
						}
						catch (IOException ex)
						{
							ex.printStackTrace();
							JOptionPane.showMessageDialog(
									graphComponent,
									ex.toString(),
									mxResources.get("error"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}
}

