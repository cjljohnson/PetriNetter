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
	protected Object[] cellIDs;

	public MarkingTableModel(Map<String, Map<String, Integer>> markingMap, PetriGraph graph) {
		this.markingMap = markingMap;
		this.graph = graph;
		this.columnNames = columnNames;
		this.cellIDs = (Object[]) markingMap.get("M0").keySet().toArray();
	}

	public int getColumnCount() {
		return markingMap.get("M0").size() + 1;
	}

	public int getRowCount() {
		return markingMap.size();
	}

	public Object getValueAt(int row, int col) {
		Object[] entries = markingMap.entrySet().toArray();
		Map.Entry entry=(Map.Entry)entries[row];
		if (col == 0) {
			return entry.getKey();
		} else {
			Map<String, Integer> tokenMap = (Map<String, Integer>) entry.getValue();
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
