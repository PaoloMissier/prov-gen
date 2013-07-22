package uk.ac.ncl.prov.generator.ui.actions;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class NewEntityAction extends GraphEditorAction {

	public NewEntityAction() {

		super("Entity");
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.newElement("Entity");
	}
}
