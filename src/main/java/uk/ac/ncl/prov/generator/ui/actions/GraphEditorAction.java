package uk.ac.ncl.prov.generator.ui.actions;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

public class GraphEditorAction extends AbstractAction {

	private final int SHORTCUT_MASK = Toolkit.getDefaultToolkit()
			.getMenuShortcutKeyMask();

	public GraphEditorAction(String name) {

		super(name);
	}

	public GraphEditorAction(String name, int mnemonicKey) {

		super(name);
		
		putValue(MNEMONIC_KEY, mnemonicKey);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(mnemonicKey,
				SHORTCUT_MASK));
	}

	/* Determine from which frame the event orginated */
	public static GraphEditorFrame frameFromEvent(ActionEvent event) {
		Component jc = (Component) event.getSource();
		while (jc != null) {

			if (jc instanceof GraphEditorFrame) {
				break;
			}

			if (jc instanceof JPopupMenu) {
				jc = ((JPopupMenu) jc).getInvoker();
			} else {
				jc = jc.getParent();
			}
		}
		return (GraphEditorFrame) jc;
	}

	final public void actionPerformed(ActionEvent event) {
		
		GraphEditorFrame graphEditorFrame = frameFromEvent(event);
		actionPerformed(graphEditorFrame);
	}

	public void actionPerformed(GraphEditorFrame graphEditorFrame) {}

}
