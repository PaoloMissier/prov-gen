package uk.ac.ncl.prov.generator.ui.actions;

import java.awt.event.KeyEvent;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

final public class ExitAction extends GraphEditorAction {

	public ExitAction() {

		super("Quit", KeyEvent.VK_Q);
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {

		graphEditorFrame.dispose();
	}
}
