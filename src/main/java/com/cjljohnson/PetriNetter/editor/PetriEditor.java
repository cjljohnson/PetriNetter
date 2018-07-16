package com.cjljohnson.PetriNetter.editor;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.cjljohnson.PetriNetter.PetriNetManager;

public class PetriEditor extends JFrame{
    
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
        
        JFrame frame = new PetriEditor();
        
        JTabbedPane pane = new JTabbedPane();
        pane.add("Petri Net 1", new PetriNetManager());
        pane.setTabComponentAt(0, new ButtonTabComponent(pane));
        pane.add("Petri Net 2", new PetriNetManager());
        pane.setTabComponentAt(1, new ButtonTabComponent(pane));
        frame.add(pane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
