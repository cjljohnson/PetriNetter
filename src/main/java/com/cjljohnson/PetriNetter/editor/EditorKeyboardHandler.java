package com.cjljohnson.PetriNetter.editor;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.cjljohnson.PetriNetter.editor.tools.PetriToolActions;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;

public class EditorKeyboardHandler {
	
	private PetriEditor editor;
	
	public EditorKeyboardHandler(PetriEditor editor) {
		this.editor = editor;
		
		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		SwingUtilities.replaceUIInputMap(editor,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);

		inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		SwingUtilities.replaceUIInputMap(editor,
				JComponent.WHEN_FOCUSED, inputMap);
		SwingUtilities.replaceUIActionMap(editor, createActionMap());
	}
	
	protected InputMap getInputMap(int condition)
	{
		InputMap map = new InputMap();

			map.put(KeyStroke.getKeyStroke("control S"), "save");
			map.put(KeyStroke.getKeyStroke("control shift S"), "saveAs");
			map.put(KeyStroke.getKeyStroke("control N"), "new");
			map.put(KeyStroke.getKeyStroke("control T"), "new");
			map.put(KeyStroke.getKeyStroke("control O"), "open");
			map.put(KeyStroke.getKeyStroke("control I"), "image");

			map.put(KeyStroke.getKeyStroke("control Z"), "undo");
			map.put(KeyStroke.getKeyStroke("control Y"), "redo");
			map.put(KeyStroke.getKeyStroke("control shift Z"), "undo");
		return map;
	}
	
	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap()
	{
		ActionMap map = new ActionMap();

		map.put("save", editor.bind("Save", PetriEditorActions.getSaveAction(), (String)null));
		map.put("saveAs", editor.bind("Save As", PetriEditorActions.getSaveAsAction(), (String)null));
		map.put("new", editor.bind("New", PetriEditorActions.getNewAction(), (String)null));
		map.put("open", editor.bind("Open", PetriEditorActions.getOpenAction(), (String)null));
		map.put("image", editor.bind("Open", PetriEditorActions.getExportImageAction(), (String)null));
		map.put("undo", editor.bind("Undo", PetriEditorActions.getUndoAction(), (String)null));
		map.put("redo", editor.bind("Redo", PetriEditorActions.getRedoAction(), (String)null));

		return map;
	}

}
