package uk.ac.ncl.prov.generator.ui.actions;

import java.awt.event.KeyEvent;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class NewGraphAction extends GraphEditorAction {

	public NewGraphAction() {

		super("Graph", KeyEvent.VK_N);
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.newGraph();
	}
}
