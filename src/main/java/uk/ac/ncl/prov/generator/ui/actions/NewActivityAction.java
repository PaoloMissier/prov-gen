package uk.ac.ncl.prov.generator.ui.actions;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class NewActivityAction extends GraphEditorAction {

	public NewActivityAction() {

		super("Activity");
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.newElement("Activity");
	}
}
