package uk.ac.ncl.prov.generator.ui.actions;

import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class SaveGraphAction extends GraphEditorAction {

	public SaveGraphAction() {

		super("Save", KeyEvent.VK_S);
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.saveGraph();
	}
}
