package uk.ac.ncl.prov.generator.ui.actions;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class SeedAction extends GraphEditorAction {

	public SeedAction() {

		super("Expand Graph");
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.seed();
	}
}
