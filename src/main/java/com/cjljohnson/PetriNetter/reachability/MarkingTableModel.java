/*
 * Table model for the marking table.
 * 
 * Calculates the row and column labels.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter.reachability;

import java.util.Arrays;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.mxgraph.model.mxGraphModel;

public class MarkingTableModel extends AbstractTableModel{

	protected Map<String, Map<String, Integer>> markingMap;
	protected PetriGraph graph;
	protected Object[] columnNames;
	protected String[] cellIDs;

	public MarkingTableModel(Map<String, Map<String, Integer>> markingMap, PetriGraph graph) {
		this.markingMap = markingMap;
		this.graph = graph;
		
		
		// Sort columns
		Object[] ids = markingMap.get("M0").keySet().toArray();
		int[] intIDs = new int[ids.length];

		for (int i = 0; i < ids.length; i++) {
		    intIDs[i] = Integer.parseInt((String)ids[i]);
		}
		Arrays.sort(intIDs);
		
		this.cellIDs = new String[intIDs.length];
		for (int i = 0; i < ids.length; i++) {
			cellIDs[i] = Integer.toString(intIDs[i]);
		}
	}

	public int getColumnCount() {
		return markingMap.get("M0").size() + 1;
	}

	public int getRowCount() {
		return markingMap.size();
	}

	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return "M" + row;
		} else {
			Map<String, Integer> tokenMap = markingMap.get("M" + row);
			return tokenMap.get(cellIDs[col-1]);
		}
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Marking";
		} else {
			Object cell = ((mxGraphModel)graph.getModel()).getCell((String) cellIDs[column-1]);
			return "p" + graph.getCellMarkingName(cell);
		}
	}

}
