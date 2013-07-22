package uk.ac.ncl.prov.generator.ui.actions;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class NewAgentAction extends GraphEditorAction {

	public NewAgentAction() {

		super("Agent");
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.newElement("Agent");
	}
}
