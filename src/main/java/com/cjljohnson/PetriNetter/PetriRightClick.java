/*
 * Builds a contextual JPopupMenu when the PetriGraph is right clicked.
 * Contains actions available on right click and component property fields.
 * 
 * @author Chris Johnson
 * @version v1.0
 */

package com.cjljohnson.PetriNetter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.CellEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import com.cjljohnson.PetriNetter.model.Arc;
import com.cjljohnson.PetriNetter.model.PetriGraph;
import com.cjljohnson.PetriNetter.model.Place;
import com.cjljohnson.PetriNetter.model.Transition;
import com.cjljohnson.PetriNetter.reachability.ReachActions;

public class PetriRightClick extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149390414490130748L;

	public PetriRightClick(final PetriNetManager manager, int x, int y)
	{

		final Object cell = manager.getPetriComponent().getCellAt(x, y);

		if (cell != null) {
			Object value = ((mxCell)cell).getValue();
			if (value instanceof Place) {
				placeMenu(manager, cell, (Place)value);
			} else if (value instanceof Arc) {
				arcMenu(manager, cell, (Arc)value);
			} else if (value instanceof Transition) {
				transitionMenu(manager, cell, (Transition)value);
			}
		}

		if (!manager.reachValid()) {
			add(manager.bind("Add Place", PetriGraphActions.getCreatePlaceAction(x, y),
					"/images/place.gif"));

			add(manager.bind("Add Transition", PetriGraphActions.getCreateTransitionAction(x, y),
					"/images/transition.gif"));

			addSeparator();

			add(manager.bind("Reachability Graph", PetriGraphActions.getCreateReachabilityAction(),
					"/images/reach.gif"));
		}

		if (manager.reachValid()) {
			add(manager.bind("Close Reachability Graph", ReachActions.getCloseReachabilityAction(),
					"/images/cancel.png"));
		}
	}

	private void placeMenu(final PetriNetManager manager, final Object cell, final Place place) {

		if (!manager.reachValid()) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setAlignmentX(Component.LEFT_ALIGNMENT);
			// Name
			JPanel namePanel = new JPanel();
			//namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
			//namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			JLabel nameL = new JLabel(" Name:    ");
			final JTextField nameTF = new JTextField(7);
			nameTF.setText(place.getName());
			nameTF.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					String newName = nameTF.getText().trim();
					if (newName.isEmpty()) {
						newName = null;
					}
					if (newName != place.getName()) {
						PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
						graph.setPlaceName(cell, newName);
						graph.refresh();
					}
					return;

				}
			});
			namePanel.add(nameL);
			namePanel.add(nameTF);
			add(namePanel);

			nameTF.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					nameTF.postActionEvent();
				}
			});
			
			
			// Tokens
			JPanel tokensPanel = new JPanel();
			//tokensPanel.setLayout(new BoxLayout(tokensPanel, BoxLayout.X_AXIS));
			//tokensPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
			JLabel tokensL = new JLabel("Tokens:  ");
			final JTextField tokensTF = new JTextField(7);
			tokensTF.setText(Integer.toString(place.getTokens()));
			tokensTF.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int newTokens;
					try {
						newTokens = Integer.parseInt(tokensTF.getText().trim());
						int capacity = place.getCapacity();
						if (newTokens != place.getTokens() && newTokens >= 0 
								&& (newTokens <= capacity || capacity == -1)) {
							PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
							graph.setTokens(cell, newTokens);
							graph.refresh();
						}
					} catch (Exception exc) {
					}
					tokensTF.setText(Integer.toString(((Place)((mxCell)cell).getValue()).getTokens()));
					return;

				}
			});
			tokensPanel.add(tokensL);
			tokensPanel.add(tokensTF);
			add(tokensPanel);

			tokensTF.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					tokensTF.postActionEvent();
				}
			});

			// Capacity
			JPanel capacityPanel = new JPanel();
			JLabel capacityL = new JLabel("Capacity:");
			final JTextField capacityTF = new JTextField(7);
			if (place.getCapacity() == -1) {
				capacityTF.setText("n");
			} else {
				capacityTF.setText(Integer.toString(place.getCapacity()));
			}
			capacityTF.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int newCapacity;
					try {
						if (capacityTF.getText().trim().equalsIgnoreCase("n")) {
							newCapacity = -1;
						} else {
							newCapacity = Integer.parseInt(capacityTF.getText().trim());
						}
						if (newCapacity != place.getCapacity() && 
								(newCapacity > 0 && newCapacity >= place.getTokens()) 
								|| newCapacity == -1) {
							PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
							graph.setCapacity(cell, newCapacity);
							graph.refresh();
						}
					} catch (Exception exc) {

					}
					if (((Place)((mxCell)cell).getValue()).getCapacity() == -1) {
						capacityTF.setText("n");
					} else {
						capacityTF.setText(Integer.toString(((Place)((mxCell)cell).getValue()).getCapacity()));
					}
					return;

				}
			});
			capacityPanel.add(capacityL);
			capacityPanel.add(capacityTF);
			add(capacityPanel);

			capacityTF.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					capacityTF.postActionEvent();
				}
			});
			
			addSeparator();
		}

		JMenu positionMenu = new JMenu("Label position");
		positionMenu.add(manager.bind("Top", new PetriGraphActions.PositionPlaceLabelAction("Top", cell, 0, -0.7)));
		positionMenu.add(manager.bind("Top Left", new PetriGraphActions.PositionPlaceLabelAction("Top Left", cell, -1, -0.5)));
		positionMenu.add(manager.bind("Top Right", new PetriGraphActions.PositionPlaceLabelAction("Top Right", cell, 1, -0.5)));
		positionMenu.add(manager.bind("Left", new PetriGraphActions.PositionPlaceLabelAction("Left", cell, -1, 0.3)));
		positionMenu.add(manager.bind("Right", new PetriGraphActions.PositionPlaceLabelAction("Right", cell, 1, 0.3)));
		positionMenu.add(manager.bind("Bottom", new PetriGraphActions.PositionPlaceLabelAction("Bottom", cell, 0, 1.2)));
		positionMenu.add(manager.bind("Bottom Left", new PetriGraphActions.PositionPlaceLabelAction("Bottom Left", cell, -1, 1)));
		positionMenu.add(manager.bind("Bottom Right", new PetriGraphActions.PositionPlaceLabelAction("Bottom Right", cell, 1, 1)));

		add(positionMenu);

		addSeparator();
	}

	private void arcMenu(final PetriNetManager manager, final Object cell, final Arc arc) {

		// Weight
		if (!manager.reachValid()) {

			JPanel weightPanel = new JPanel();
			JLabel weightL = new JLabel("Weight:");
			final JTextField weightTF = new JTextField(7);
			weightTF.setText(Integer.toString(arc.getWeight()));
			weightTF.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int newWeight;
					try {
						newWeight = Integer.parseInt(weightTF.getText().trim());
						if (arc.getWeight() != newWeight && newWeight > 0) {
							PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
							graph.setArcWeight(cell, newWeight);
						}
					} catch (Exception exc) {

					}
					weightTF.setText(Integer.toString(((Arc)((mxCell)cell).getValue()).getWeight()));
					return;

				}
			});
			weightPanel.add(weightL);
			weightPanel.add(weightTF);
			add(weightPanel);

			weightTF.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					weightTF.postActionEvent();
				}
			});
			addSeparator();
		}

		JMenu positionMenu = new JMenu("Label position");
		positionMenu.add(manager.bind("Top", new PetriGraphActions.PositionLabelAction("Top", cell, "BC")));
		positionMenu.add(manager.bind("Top Left", new PetriGraphActions.PositionLabelAction("Top Left", cell, "BR")));
		positionMenu.add(manager.bind("Top Right", new PetriGraphActions.PositionLabelAction("Top Right", cell, "BL")));
		positionMenu.add(manager.bind("Left", new PetriGraphActions.PositionLabelAction("Left", cell, "MR")));
		positionMenu.add(manager.bind("Right", new PetriGraphActions.PositionLabelAction("Right", cell, "ML")));
		positionMenu.add(manager.bind("Bottom", new PetriGraphActions.PositionLabelAction("Bottom", cell, "TC")));
		positionMenu.add(manager.bind("Bottom Left", new PetriGraphActions.PositionLabelAction("Bottom Left", cell, "TR")));
		positionMenu.add(manager.bind("Bottom Right", new PetriGraphActions.PositionLabelAction("Bottom Right", cell, "TL")));

		add(positionMenu);

		addSeparator();
	}

	private void transitionMenu(PetriNetManager manager, Object cell, Transition transition) {

		boolean isFirable = ((PetriGraph)manager.getPetriComponent().getGraph()).isFirable(cell);

		add(manager.bind("Fire Transition", new PetriGraphActions.FireTransitionAction(cell),
				"/images/lightning_go.png"))
		.setEnabled(isFirable);

		addSeparator();
	}

}