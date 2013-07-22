package uk.ac.ncl.prov.generator.core;

import uk.ac.ncl.prov.generator.ui.GraphEditorFrame;

public class ProvGen {

	public static void main(String[] args) {

		/*
		 * This is the entry point, maybe in future, we could read in some
		 * config files for colour choices
		 */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GraphEditorFrame();
			}
		});

	}
}
