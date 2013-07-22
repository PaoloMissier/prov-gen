package uk.ac.ncl.prov.generator.ui.actions;

import java.awt.event.KeyEvent;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

public class OpenGraphAction extends GraphEditorAction {

	public OpenGraphAction() {

		super("Open Graph...", KeyEvent.VK_O);
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.openGraph();
	}

}
