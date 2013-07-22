package uk.ac.ncl.prov.generator.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FileUtils;
import org.openprovenance.prov.java.JProvUtility;
import org.openprovenance.prov.java.NSBundle;
import org.openprovenance.prov.java.component4.Bundle;

import uk.ac.ncl.prov.gen.generator.Generator;
import uk.ac.ncl.prov.generator.model.PresentationGraph;
import uk.ac.ncl.prov.generator.ui.actions.ExitAction;
import uk.ac.ncl.prov.generator.ui.actions.ExpandAction;
import uk.ac.ncl.prov.generator.ui.actions.NewActivityAction;
import uk.ac.ncl.prov.generator.ui.actions.NewAgentAction;
import uk.ac.ncl.prov.generator.ui.actions.NewEntityAction;
import uk.ac.ncl.prov.generator.ui.actions.NewGraphAction;
import uk.ac.ncl.prov.generator.ui.actions.NewRelationAction;
import uk.ac.ncl.prov.generator.ui.actions.OpenGraphAction;
import uk.ac.ncl.prov.generator.ui.actions.SaveGraphAction;
import uk.ac.ncl.prov.generator.ui.actions.SeedAction;

public class GraphEditorFrame extends JFrame {

	static final int DEFAULT_WIDTH = 800;
	static final int DEFAULT_HEIGHT = 600;

	/* UI Actions */
	private Action newGraphAction = new NewGraphAction();
	private Action newActivityAction = new NewActivityAction();
	private Action newAgentAction = new NewAgentAction();
	private Action newEntityAction = new NewEntityAction();
	private Action newRelationAction = new NewRelationAction();
	private Action openGraphAction = new OpenGraphAction();
	private Action saveGraphAction = new SaveGraphAction();
	private Action exitAction = new ExitAction();
	private Action seedAction = new SeedAction();
	private Action expandAction = new ExpandAction();

	private JScrollPane editorPane;

	private List<JComponent> disabledItems = new ArrayList<JComponent>();

	private GraphEditor editor;
	private PresentationGraph presentationGraph;

	private NSBundle bundle;
	
	public GraphEditorFrame() {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		setUpFrame();
	}

	public void setUpFrame() {

		setTitle("Provenance Generation Tool");
		setJMenuBar(createMenuBar());
		setUpMainPanel();

		/* Disable items that should not be clickable right now */
		toggleItems(false);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setResizable(true);
		setVisible(true);
	}

	public JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());

		return menuBar;
	}

	public JMenu createFileMenu() {

		JMenu fileMenu = new JMenu("File");

		JMenuItem newGraphMenuItem = new JMenuItem(newGraphAction);
		JMenuItem newActivityMenuItem = new JMenuItem(newActivityAction);
		JMenuItem newAgentMenuItem = new JMenuItem(newAgentAction);
		JMenuItem newEntityMenuItem = new JMenuItem(newEntityAction);
		JMenuItem newRelationMenuItem = new JMenuItem(newRelationAction);
		JMenuItem openGraphMenuItem = new JMenuItem(openGraphAction);
		JMenuItem saveGraphMenuItem = new JMenuItem(saveGraphAction);
		JMenuItem exitMenuItem = new JMenuItem(exitAction);

		JMenuItem newNodeMenu = new JMenu("Node");
		newNodeMenu.add(newActivityMenuItem);
		newNodeMenu.add(newAgentMenuItem);
		newNodeMenu.add(newEntityMenuItem);

		JMenu newMenu = new JMenu("New");

		newMenu.add(newGraphMenuItem);
		newMenu.add(newNodeMenu);
		newMenu.add(newRelationMenuItem);

		fileMenu.add(newMenu);

		fileMenu.add(openGraphMenuItem);
		fileMenu.add(saveGraphMenuItem);

		fileMenu.addSeparator();

		fileMenu.add(exitMenuItem);

		disabledItems.add(newNodeMenu);
		disabledItems.add(newRelationMenuItem);
		disabledItems.add(saveGraphMenuItem);

		return fileMenu;
	}

	public void setUpMainPanel() {

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setFocusable(true);

		mainPanel.add(setUpToolBar(), BorderLayout.WEST);

		editorPane = new JScrollPane();
		editorPane
				.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		editorPane.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		mainPanel.add(editorPane, BorderLayout.CENTER);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	public JPanel setUpToolBar() {

		JPanel toolbar = new JPanel(new BorderLayout());

		toolbar.add(createButtonPanel(), BorderLayout.NORTH);
		/* TODO: Perhaps add variable bar in here? */

		return toolbar;
	}

	public JPanel createButtonPanel() {

		JPanel buttonPanel = new JPanel(new MigLayout());

		JButton newActivityButton = new JButton("New Activity");
		JButton newAgentButton = new JButton("New Agent");
		JButton newEntityButton = new JButton("New Entity");
		JButton newRelationButton = new JButton("New Relation");
		JButton seedButton = new JButton("Seed");
		JButton expandButton = new JButton("Expand");

		newActivityButton.addActionListener(newActivityAction);
		newAgentButton.addActionListener(newAgentAction);
		newEntityButton.addActionListener(newEntityAction);
		newRelationButton.addActionListener(newRelationAction);
		seedButton.addActionListener(seedAction);
		expandButton.addActionListener(expandAction);

		buttonPanel.add(newActivityButton, "sg toolbarButton, wrap");
		buttonPanel.add(newAgentButton, "sg toolbarButton, wrap");
		buttonPanel.add(newEntityButton, "sg toolbarButton, wrap");
		buttonPanel.add(newRelationButton, "sg toolbarButton, wrap");
		buttonPanel.add(seedButton, "sg toolbarButton, wrap");
		buttonPanel.add(expandButton, "sg toolbarButton, wrap");

		disabledItems.add(newActivityButton);
		disabledItems.add(newAgentButton);
		disabledItems.add(newEntityButton);
		disabledItems.add(newRelationButton);
		disabledItems.add(seedButton);
		disabledItems.add(expandButton);

		return buttonPanel;
	}

	/* Enables and disables UI items */
	public void toggleItems(boolean enabled) {

		Iterator<JComponent> it = disabledItems.iterator();

		while (it.hasNext()) {

			it.next().setEnabled(enabled);
		}
	}

	// Dealing with graph actions

	public void newGraph() {

		if (presentationGraph != null) {

			int option = JOptionPane
					.showConfirmDialog(
							this,
							"Do you really want to create a new graph? Any unsaved progress will be lost!",
							"Be Careful!", JOptionPane.YES_NO_OPTION);

			if (option == 1)
				return;
		}

		presentationGraph = new PresentationGraph(new Bundle());

		this.editor = new GraphEditor(presentationGraph);

		this.editorPane.setViewportView(this.editor);
		editorPane.setFocusable(true);

		/*
		 * A graph has been opened so some UI elements should be enabled
		 */
		toggleItems(true);
	}

	public void openGraph() {

		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			try {
				JProvUtility u = new JProvUtility();
				bundle = u.convertASNToJava(file.getAbsolutePath());

				this.presentationGraph = new PresentationGraph(bundle);
				this.editor = new GraphEditor(presentationGraph);

				this.editorPane.setViewportView(this.editor);
				editorPane.setFocusable(true);

				/*
				 * A graph has been opened so some UI elements should be enabled
				 */
				toggleItems(true);

			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void saveGraph() {

		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			JProvUtility u = new JProvUtility();
			String asn = u.convertJavaToASN(presentationGraph.getGraph(), bundle.getNamespaces());

			try {
				FileUtils.fileWrite(file.getAbsolutePath(), asn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void newElement(String type) {

		JTextField idField = new JTextField();
		JTextField minWeightField = new JTextField();
		JTextField maxWeightField = new JTextField();

		final JComponent[] inputs = new JComponent[] { new JLabel("ID"),
				idField, new JLabel("Min Cardinality"), minWeightField,
				new JLabel("Max Cardinality"), maxWeightField };

		JOptionPane.showMessageDialog(this, inputs, "Create a new element",
				JOptionPane.PLAIN_MESSAGE);

		try {
			String id = idField.getText();
			int minWeight = Integer.parseInt(minWeightField.getText());
			int maxWeight = Integer.parseInt(maxWeightField.getText());

			if (id != null && !id.equals("") && minWeight <= maxWeight) {
				presentationGraph.createElement(type, id, minWeight, maxWeight);
			}
		} catch (Exception e) {

			System.out.println(e);
		}
	}

	public void newEdge() {

		presentationGraph.setConnecting(true);
	}

	public void seed() {

		Generator generator = new Generator(presentationGraph.getGraph());
		generator.seed(true);
	}

	public void expand() {
		
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			Generator generator = new Generator(presentationGraph.getGraph());
			Bundle newBundle = generator.expand();
			
			JProvUtility u = new JProvUtility();
			String asn = u.convertJavaToASN(newBundle, bundle.getNamespaces());

			try {
				FileUtils.fileWrite(file.getAbsolutePath(), asn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
