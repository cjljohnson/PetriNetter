package com.cjljohnson.PetriNetter.editor;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.Document;

import com.cjljohnson.PetriNetter.DefaultFileFilter;
import com.cjljohnson.PetriNetter.PetriNetManager;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.reachability.ReachabilityChange;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
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

public class PetriEditorActions {
	
	private static String OVERWRITE_EXISTING_FILE = "Overwrite existing file?";
	private static String TRANSPARENT_BACKGROUND = "Set background to transparent?";
	private static String FILE = "File";
	private static String ALL_SUPPORTED_FORMATS = "All Supported Formats";

    private static NewAction newAction = new NewAction();
    private static OpenAction openAction = new OpenAction();
    private static SaveAction saveAction = new SaveAction(false);
    private static SaveAction saveAsAction = new SaveAction(true);
    private static ExportImageAction exportImageAction = new ExportImageAction();
    private static CreateReachabilityAction createReachabilityAction = new CreateReachabilityAction();
    private static ShowBoundedness showBoundedness = new ShowBoundedness();
    private static ShowLiveness showLiveness = new ShowLiveness();
    private static HistoryAction undoAction = new HistoryAction(true);
    private static HistoryAction redoAction = new HistoryAction(false);
    private static FinaliseNetAction finaliseNetAction = new FinaliseNetAction();
    private static RevertToFinalisedNetAction revertToFinaliseNetAction = new RevertToFinalisedNetAction();
    private static ZoomAction zoomIn = new ZoomAction(-1);
    private static ZoomAction zoomOut = new ZoomAction(1);
    private static ZoomAction zoomReset = new ZoomAction(0);
    
    /* GETTERS */

    public static NewAction getNewAction() {
        return newAction;
    }

    public static OpenAction getOpenAction() {
        return openAction;
    }

    public static SaveAction getSaveAction() {
        return saveAction;
    }

    public static SaveAction getSaveAsAction() {
        return saveAsAction;
    }
    
    public static ExportImageAction getExportImageAction() {
        return exportImageAction;
    }

    public static CreateReachabilityAction getCreateReachabilityAction() {
        return createReachabilityAction;
    }

    public static ShowBoundedness getShowBoundedness() {
        return showBoundedness;
    }

    public static ShowLiveness getShowLiveness() {
        return showLiveness;
    }

    public static final HistoryAction getUndoAction() {
        return undoAction;
    }

    public static final HistoryAction getRedoAction() {
        return redoAction;
    }
    
    public static FinaliseNetAction getFinaliseNetAction() {
		return finaliseNetAction;
	}

	public static RevertToFinalisedNetAction getRevertToFinaliseNetAction() {
		return revertToFinaliseNetAction;
	}
	
	public static ZoomAction getZoomInAction() {
	    return zoomIn;
	}
	public static ZoomAction getZoomOutAction() {
        return zoomOut;
    }
	public static ZoomAction getZoomResetAction() {
        return zoomReset;
    }



    /* ACTIONS */


	@SuppressWarnings("serial")
    public static class NewAction extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            editor.newPetriNet();
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
        protected void openXmlPng(PetriGraph graph, File file)
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
                            graph.getModel());
                    //                 editor.setCurrentFile(file);
                    //                 resetEditor(editor);
                    return;
                }
            }

            JOptionPane.showMessageDialog(null,
                    mxResources.get("imageContainsNoDiagramData"));
        }

        /**
         * @throws IOException
         *
         */
        protected void openGD(PetriGraph graph, File file,
                String gdText)
        {

            // Replaces file extension with .mxe
            String filename = file.getName();
            filename = filename.substring(0, filename.length() - 4) + ".mxe";

            if (new File(filename).exists()
                    && JOptionPane.showConfirmDialog(null,
                    		OVERWRITE_EXISTING_FILE) != JOptionPane.YES_OPTION)
            {
                return;
            }

            ((mxGraphModel) graph.getModel()).clear();
            mxGdCodec.decode(gdText, graph);
            //editor.setCurrentFile(new File(lastDir + "/" + filename));
        }

        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            //         BasicGraphEditor editor = getEditor(e);
            PetriEditor editor = (PetriEditor)e.getSource();
            PetriGraph graph = new PetriGraph();


            String wd = (lastDir != null) ? lastDir : System
                    .getProperty("user.dir");

            JFileChooser fc = new JFileChooser(wd);

            // Adds file filter for supported file format
            DefaultFileFilter defaultFilter = new DefaultFileFilter(
                    ".pnet", "PetriNetter File"
                    + " (.pnet)")
            {

//                public boolean accept(File file)
//                {
//                    String lcase = file.getName().toLowerCase();
//
//                    return super.accept(file)
//                            || lcase.endsWith(".png")
//                            || lcase.endsWith(".vdx");
//                }
            };
            fc.addChoosableFileFilter(defaultFilter);

//            fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
//                    "mxGraph Editor " + FILE
//                    + " (.mxe)"));
//            fc.addChoosableFileFilter(new DefaultFileFilter(".png",
//                    "PNG+XML  " + FILE
//                    + " (.png)"));
//
//            // Adds file filter for VDX import
//            fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
//                    "XML Drawing  " + FILE
//                    + " (.vdx)"));
//
//            // Adds file filter for GD import
//            fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
//                    "Graph Drawing  " + FILE
//                    + " (.txt)"));

            fc.setFileFilter(defaultFilter);

            int rc = fc.showOpenDialog(null);
            if (rc == JFileChooser.APPROVE_OPTION)
            {
                lastDir = fc.getSelectedFile().getParent();

                try
                {
                    if (fc.getSelectedFile().getAbsolutePath()
                            .toLowerCase().endsWith(".png"))
                    {
                        openXmlPng(graph, fc.getSelectedFile());
                    }
                    else if (fc.getSelectedFile().getAbsolutePath()
                            .toLowerCase().endsWith(".txt"))
                    {
                        openGD(graph, fc.getSelectedFile(),
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
                        //                                 editor.setCurrentFile(fc
                        //                                         .getSelectedFile());

                        //                                 resetEditor(editor);
                    }
                    PetriNetManager manager = new PetriNetManager(graph);
                    manager.setCurrentFile(fc.getSelectedFile());
                    editor.addPetriNet(manager);
                    editor.updateTitle(manager);

                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            editor,
                            ex.toString(),
                            mxResources.get("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
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
        protected void saveXmlPng(PetriEditor editor, String filename,
                Color bg) throws IOException
        {
            PetriNetManager manager = (PetriNetManager)editor.getPane()
                    .getSelectedComponent();
            
            
            
            mxGraphComponent graphComponent = manager.getPetriComponent();
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

                    manager.setModified(false);
                    manager.setCurrentFile(new File(filename));
                    editor.updateTitle(manager);
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

            PetriEditor editor = (PetriEditor)e.getSource();

            if (editor != null)
            {
                PetriNetManager manager = editor.getActiveGraphManager();
                
                if (manager == null) {
                	return;
                }
                
                mxGraphComponent graphComponent = manager.getPetriComponent();
                mxGraph graph = graphComponent.getGraph();
                FileFilter selectedFilter = null;
                DefaultFileFilter pnetFilter = new DefaultFileFilter(".pnet",
                      "PetriNetter " + FILE + " (.pnet)");
//                DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
//                        "PNG+XML " + FILE + " (.png)");
//                FileFilter vmlFileFilter = new DefaultFileFilter(".html",
//                        "VML " + FILE + " (.html)");
                String filename = null;
                boolean dialogShown = false;

                if (showDialog || manager.getCurrentFile() == null)
                {
                    String wd;

                    if (lastDir != null)
                    {
                        wd = lastDir;
                    }
                    //                  else if (editor.getCurrentFile() != null)
                    //                  {
                    //                      wd = editor.getCurrentFile().getParent();
                    //                  }
                    else
                    {
                        wd = System.getProperty("user.dir");
                    }

                    JFileChooser fc = new JFileChooser(wd);

                    // Adds the default file format
                    FileFilter defaultFilter = pnetFilter;
                    fc.addChoosableFileFilter(defaultFilter);

//                    // Adds special vector graphics formats and HTML
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
//                            "mxGraph Editor " + FILE
//                            + " (.mxe)"));
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
//                            "Graph Drawing " + FILE
//                            + " (.txt)"));
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
//                            "SVG " + FILE + " (.svg)"));
//                    fc.addChoosableFileFilter(vmlFileFilter);
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".html",
//                            "HTML " + FILE + " (.html)"));
//
//                    // Adds a filter for each supported image format
//                    Object[] imageFormats = ImageIO.getReaderFormatNames();
//
//                    // Finds all distinct extensions
//                    HashSet<String> formats = new HashSet<String>();
//
//                    for (int i = 0; i < imageFormats.length; i++)
//                    {
//                        String ext = imageFormats[i].toString().toLowerCase();
//                        formats.add(ext);
//                    }
//
//                    imageFormats = formats.toArray();
//
//                    for (int i = 0; i < imageFormats.length; i++)
//                    {
//                        String ext = imageFormats[i].toString();
//                        fc.addChoosableFileFilter(new DefaultFileFilter("."
//                                + ext, ext.toUpperCase() + " "
//                                        + FILE + " (." + ext + ")"));
//                    }
//
//                    // Adds filter that accepts all supported image formats
//                    fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
//                            mxResources.get("allImages")));
                    
                    fc.setFileFilter(defaultFilter);
                    int rc = fc.showDialog(null, "Save");
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
                            		OVERWRITE_EXISTING_FILE) != JOptionPane.YES_OPTION)
                    {
                        return;
                    }
                }
                else
                {
                    filename = manager.getCurrentFile().getAbsolutePath();
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
//                    else if (selectedFilter == vmlFileFilter)
//                    {
//                        mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
//                                .createVmlDocument(graph, null, 1, null, null)
//                                .getDocumentElement()), filename);
//                    }
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

                        //editor.setModified(false);
                        manager.setCurrentFile(new File(filename));
                        editor.updateTitle(manager);
                    }
                    else if (ext.equalsIgnoreCase("pnet"))
                    {
                        mxCodec codec = new mxCodec();
                        String xml = mxXmlUtils.getXml(codec.encode(graph
                                .getModel()));

                        mxUtils.writeFile(xml, filename);

                        manager.setCurrentFile(new File(filename));
                        manager.setModified(false);
                        editor.updateTitle(manager);
                    }
                    else if (ext.equalsIgnoreCase("txt"))
                    {
                        String content = mxGdCodec.encode(graph);

                        mxUtils.writeFile(content, filename);
                    }
                    else
                    {
//                        Color bg = null;
//                        
//                        int option = JOptionPane.NO_OPTION;
//                        
//                        if (ext.equalsIgnoreCase("gif") || ext
//                                .equalsIgnoreCase("png")) {
//                        	option = JOptionPane.showConfirmDialog(graphComponent, 
//                        			TRANSPARENT_BACKGROUND);
//                        	
//                        	if (option == JOptionPane.CANCEL_OPTION) {
//                        		return;
//                        	}
//                        }
//
//                        if (option == JOptionPane.NO_OPTION)
//                        {
//                            bg = graphComponent.getBackground();
//                        }
//
//                        if (selectedFilter == xmlPngFilter
//                                || (manager.getCurrentFile() != null
//                                && ext.equalsIgnoreCase("png") && !dialogShown))
//                        {
//                            saveXmlPng(editor, filename, bg);
//                        }
//                        else
//                        {
//                            BufferedImage image = mxCellRenderer
//                                    .createBufferedImage(graph, null, 1, bg,
//                                            graphComponent.isAntiAlias(), null,
//                                            graphComponent.getCanvas());
//
//                            if (image != null)
//                            {
//                                ImageIO.write(image, ext, new File(filename));
//                            }
//                            else
//                            {
//                                JOptionPane.showMessageDialog(graphComponent,
//                                        mxResources.get("noImageData"));
//                            }
//                        }
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
   public static class ExportImageAction extends AbstractAction
   {

       /**
        * 
        */
       protected String lastDir = null;

       /**
        * 
        */
       public ExportImageAction()
       {
       }

       /**
        * 
        */
       public void actionPerformed(ActionEvent e)
       {

           PetriEditor editor = (PetriEditor)e.getSource();

           if (editor != null)
           {
               PetriNetManager manager = editor.getActiveGraphManager();
               
               if (manager == null) {
               	return;
               }
               
               mxGraphComponent graphComponent = manager.getPetriComponent();
               mxGraph graph = graphComponent.getGraph();
               FileFilter selectedFilter = null;
               DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
                       "PNG+XML " + FILE + " (.png)");
               String filename = null;
               boolean dialogShown = false;


               String wd;

               if (lastDir != null)
               {
            	   wd = lastDir;
               }
               //                  else if (editor.getCurrentFile() != null)
               //                  {
               //                      wd = editor.getCurrentFile().getParent();
               //                  }
               else
               {
            	   wd = System.getProperty("user.dir");
               }

               JFileChooser fc = new JFileChooser(wd);

               // Adds the default file format
               FileFilter defaultFilter = xmlPngFilter;
               fc.addChoosableFileFilter(defaultFilter);

               // Add other image types
               fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
            		   "SVG " + FILE + " (.svg)"));
               
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
            					   + FILE + " (." + ext + ")"));
               }

               // Adds filter that accepts all supported image formats
               fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
            		   mxResources.get("allImages")));
               fc.setFileFilter(defaultFilter);
               int rc = fc.showDialog(null, "Save");
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
            				   OVERWRITE_EXISTING_FILE) != JOptionPane.YES_OPTION)
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
                   else
                   {
                       Color bg = null;

                       int option = JOptionPane.NO_OPTION;
                       
                       if (ext.equalsIgnoreCase("gif") || ext
                               .equalsIgnoreCase("png")) {
                       	option = JOptionPane.showConfirmDialog(graphComponent, 
                       			TRANSPARENT_BACKGROUND);
                       	
                       	if (option == JOptionPane.CANCEL_OPTION) {
                       		return;
                       	}
                       }

                       if (option == JOptionPane.NO_OPTION)
                       {
                           bg = graphComponent.getBackground();
                       }


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

    @SuppressWarnings("serial")
    public static class CreateReachabilityAction extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            if (editor != null)
            {
                PetriNetManager manager = editor.getActiveGraphManager();
                if (manager != null)
                {
                	if (manager.reachValid())
                	{
                		// Close reachability graph
            		    mxGraphModel model = (mxGraphModel)manager.getPetriComponent().getGraph().getModel();
            		    try {
            		    	model.beginUpdate();
            		    	model.execute(new ReachabilityChange(manager, false, null, null));
            		    	manager.revertToFinalised();
            		    } finally {
            		    	model.endUpdate();
            		    }
                	}
                	else 
                	{
                		manager.createReachabilityGraph();
                	}
                } else 
                {
                    JOptionPane.showMessageDialog(editor, "A Petri Net must be selected to "
                            + "create a reachability graph.");
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ShowBoundedness extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            if (editor != null)
            {
                PetriNetManager manager = editor.getActiveGraphManager();
                if (manager != null)
                {
                    if (manager.reachValid() == true)
                    {
                        manager.calcBoundedness();
                    } else 
                    {
                        JOptionPane.showMessageDialog(editor, "Reachability must be calculated "
                                + "before boundedness.");
                    }
                } else 
                {
                    JOptionPane.showMessageDialog(editor, "A Petri Net must be selected to "
                            + "calculate boundedness.");
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ShowLiveness extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            if (editor != null)
            {
                PetriNetManager manager = editor.getActiveGraphManager();
                if (manager != null)
                {
                    if (manager.reachValid() == true)
                    {
                        manager.calcLiveness();
                    } else 
                    {
                        JOptionPane.showMessageDialog(editor, "Reachability must be calculated "
                                + "before liveness.");
                    }
                } else 
                {
                    JOptionPane.showMessageDialog(editor, "A Petri Net must be selected to "
                            + "calculate liveness.");
                }
            }
        }
    }
    
    @SuppressWarnings("serial")
    public static class ShowDeadlock extends AbstractAction
    {
        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            if (editor != null)
            {
                PetriNetManager manager = editor.getActiveGraphManager();
                if (manager != null)
                {
                    if (manager.reachValid() == true)
                    {
                        manager.calcDeadlock();
                    } else 
                    {
                        JOptionPane.showMessageDialog(editor, "Reachability must be calculated "
                                + "before deadlock.");
                    }
                } else 
                {
                    JOptionPane.showMessageDialog(editor, "A Petri Net must be selected to "
                            + "calculate deadlock.");
                }
            }
        }
    }

    /**
     *
     */
    @SuppressWarnings("serial")
    public static class HistoryAction extends AbstractAction
    {
        /**
         * 
         */
        protected boolean undo;

        /**
         * 
         */
        public HistoryAction(boolean undo)
        {
            this.undo = undo;
        }

        /**
         * 
         */
        public void actionPerformed(ActionEvent e)
        {
            PetriEditor editor = (PetriEditor)e.getSource();
            if (editor != null)
            {
                PetriNetManager manager = editor.getActiveGraphManager();
                if (manager != null)
                {
                    if (undo)
                    {
                        manager.getUndoManager().undo();
                    }
                    else
                    {
                        manager.getUndoManager().redo();
                    }
                }
            }
        }
    }
    
    /**
    *
    */
   @SuppressWarnings("serial")
   public static class HighlightTransitionsAction extends AbstractAction
   {
       /**
        * 
        */
	   private PetriEditor editor;

       /**
        * 
        */
       public HighlightTransitionsAction(PetriEditor editor)
       {
    	   this.editor = editor;
       }

       /**
        * 
        */
       public void actionPerformed(ActionEvent e)
       {
    	   AbstractButton aButton = (AbstractButton) e.getSource();
           if (editor != null && aButton != null)
           {
               boolean selected = aButton.getModel().isSelected();
               editor.setHighlightTransitions(selected);
           }
       }
   }
   
   @SuppressWarnings("serial")
   public static class FinaliseNetAction extends AbstractAction
   {

       /**
        * 
        */
       public void actionPerformed(ActionEvent e)
       {
           PetriEditor editor = (PetriEditor)e.getSource();
           if (editor != null)
           {
               PetriNetManager manager = editor.getActiveGraphManager();
               if (manager != null)
               {
                   manager.finaliseNet();
               }
           }
       }
   }
   
   @SuppressWarnings("serial")
   public static class RevertToFinalisedNetAction extends AbstractAction
   {

       /**
        * 
        */
       public void actionPerformed(ActionEvent e)
       {
           PetriEditor editor = (PetriEditor)e.getSource();
           if (editor != null)
           {
               PetriNetManager manager = editor.getActiveGraphManager();
               if (manager != null)
               {
                   manager.revertToFinalised();
               }
           }
       }
   }
   
   @SuppressWarnings("serial")
   public static class ZoomAction extends AbstractAction
   {
       private int type;
       
       public ZoomAction(int type) {
           this.type = type;
       }

       /**
        * 
        */
       public void actionPerformed(ActionEvent e)
       {
           PetriEditor editor = (PetriEditor)e.getSource();
           if (editor != null)
           {
               PetriNetManager manager = editor.getActiveGraphManager();
               if (manager != null)
               {
                   mxGraphComponent graphComponent = manager.getPetriComponent();
                   if (graphComponent != null) {
                       if (type < 0) {
                           graphComponent.zoomIn();
                       } else if (type > 0) {
                           graphComponent.zoomOut();
                       } else {
                           graphComponent.zoomTo(1, true);
                       }
                   }
               }
           }
       }
   }
   
   @SuppressWarnings("serial")
   public static class OpenTutotorialAction extends AbstractAction
   {
       /**
        * 
        */
       public void actionPerformed(ActionEvent e)
       {
    	   JEditorPane editorpane= new JEditorPane();
           JScrollPane editorScrollPane = new JScrollPane(editorpane);
           editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
           //File file = new File(getClass().getClassLoader().getResource("tutorial/petri-tutorial.html").getur);
           
           try {
        	URI uri = getClass().getClassLoader().getResource("tutorial/petri-tutorial.html").toURI();
			editorpane.setPage(uri.toURL());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
           editorpane.setEditable(false);
           JFrame frame = new JFrame("User Guide");
           ImageIcon img = new ImageIcon(PetriEditor.class.getResource("/images/place.gif"));
           frame.setIconImage(img.getImage());
           frame.add(editorScrollPane);
           
           frame.setLocationRelativeTo(null);
           frame.pack();
           frame.setSize(800, 500);
           frame.setVisible(true);
       }
   }

}
