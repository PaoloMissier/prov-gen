package uk.ac.ncl.prov.generator.ui.actions;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class NewRelationAction extends GraphEditorAction {

	public NewRelationAction() {

		super("Relation");
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.newEdge();
	}
}
