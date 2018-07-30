package com.cjljohnson.PetriNetter;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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

public class PetriRightClick extends JPopupMenu
{

    /**
     * 
     */
    private static final long serialVersionUID = 4149390414490130748L;

    public PetriRightClick(final PetriNetManager manager, int x, int y)
    {
        //		boolean selected = !hello.getGraphComponent().getGraph()
        //				.isSelectionEmpty();

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

        add(manager.bind("Place", PetriGraphActions.getCreatePlaceAction(x, y),
                "/images/place.gif"));

        add(manager.bind("Transition", PetriGraphActions.getCreateTransitionAction(x, y),
                "/images/transition.gif"));

        addSeparator();

        add(manager.bind("Reach", PetriGraphActions.getCreateReachabilityAction(),
                "/images/reach.gif"));

        //	    add(manager.bind2("Reach", PetriGraphActions.getCreateReachabilityAction(),
        //	            "/petri/images/reach.gif"));

        addSeparator();

        //		add(hello.bind("undo", new HistoryAction(true),
        //				"/com/mxgraph/examples/swing/images/undo.gif"));

        add(manager.bind("Delete", mxGraphActions.getDeleteAction(),
                "/images/cross.png"))
        .setEnabled(true);


        //		add(manager.bind("New", new PetriGraphActions.NewAction(), 
        //				"/com/mxgraph/examples/swing/images/new.gif"));
        //		
        //		add(manager.bind("Save As", new PetriGraphActions.SaveAction(true), 
        //				"/com/mxgraph/examples/swing/images/save.gif"));
        //		
        //		add(manager.bind("Open", new PetriGraphActions.OpenAction(),
        //				"/com/mxgraph/examples/swing/images/open.gif"));

        //		add(hello.bind("Load", new PetriGraphActions.LoadAction(true), "/com/mxgraph/examples/swing/images/load.gif"));
    }

    private void placeMenu(final PetriNetManager manager, final Object cell, final Place place) {

        // Tokens
        JPanel tokensPanel = new JPanel();
        JLabel tokensL = new JLabel("Tokens:  ");
        final JTextField tokensTF = new JTextField(5);
        tokensTF.setText(Integer.toString(place.getTokens()));
        tokensTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int newTokens;
                try {
                    newTokens = Integer.parseInt(tokensTF.getText());
                    int capacity = place.getCapacity();
                    if (newTokens != place.getTokens() && newTokens >= 0 
                            && (newTokens <= capacity || capacity == -1)) {
                        PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
                        graph.setTokens(cell, newTokens);
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

        // Capacity
        JPanel capacityPanel = new JPanel();
        JLabel capacityL = new JLabel("Capacity:");
        final JTextField capacityTF = new JTextField(5);
        if (place.getCapacity() == -1) {
            capacityTF.setText("n");
        } else {
            capacityTF.setText(Integer.toString(place.getCapacity()));
        }
        capacityTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int newCapacity;
                try {
                    if (capacityTF.getText().equalsIgnoreCase("n")) {
                        newCapacity = -1;
                    } else {
                        newCapacity = Integer.parseInt(capacityTF.getText());
                    }
                    if (newCapacity != place.getCapacity() && 
                            (newCapacity > 0 && newCapacity >= place.getTokens()) 
                            || newCapacity == -1) {
                        PetriGraph graph = (PetriGraph)manager.getPetriComponent().getGraph();
                        graph.setCapacity(cell, newCapacity);
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

        addSeparator();
    }

    private void arcMenu(final PetriNetManager manager, final Object cell, final Arc arc) {
        // Weight
        JPanel weightPanel = new JPanel();
        JLabel weightL = new JLabel("Weight:");
        final JTextField weightTF = new JTextField(5);
        weightTF.setText(Integer.toString(arc.getWeight()));
        weightTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int newWeight;
                try {
                    newWeight = Integer.parseInt(weightTF.getText());
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

        addSeparator();
    }

    private void transitionMenu(PetriNetManager manager, Object cell, Transition transition) {

        boolean isFirable = ((PetriGraph)manager.getPetriComponent().getGraph()).isFirable(cell);

        add(
                manager.bind("Fire Transition", new PetriGraphActions.FireTransitionAction(cell),
                        "/images/lightning_go.png"))
        .setEnabled(isFirable);

        addSeparator();
    }

}