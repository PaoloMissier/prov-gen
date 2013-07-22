package uk.ac.ncl.prov.generator.ui.actions;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class ExpandAction extends GraphEditorAction {

	public ExpandAction() {

		super("Expand Graph");
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.expand();
	}
}
