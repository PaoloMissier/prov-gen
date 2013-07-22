package uk.ac.ncl.prov.generator.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.openprovenance.prov.java.Element;

import uk.ac.ncl.prov.generator.model.PresentationNode;

public class NodeEditorFrame extends JFrame {

	static final int DEFAULT_WIDTH = 600;
	static final int DEFAULT_HEIGHT = 400;

	private PresentationNode presentationNode;
	private JPanel mainPanel;
	private DefaultTableModel model = new DefaultTableModel();

	public NodeEditorFrame(PresentationNode presentationNode) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		this.presentationNode = presentationNode;
		setUpFrame();
	}

	public void setUpFrame() {

		setUpMainPanel();

		setTitle("Node Editor Tool");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setResizable(true);
		setVisible(true);
	}

	public void setUpMainPanel() {

		mainPanel = new JPanel(new MigLayout());
		mainPanel.setFocusable(true);

		JLabel idLabel = new JLabel("ID");
		final JTextField idTextField = new JTextField(15);
		idTextField.setText(presentationNode.getId());

		JLabel keyLabel = new JLabel("Key");
		final JTextField keyField = new JTextField(15);

		JLabel valueLabel = new JLabel("Value");
		final JTextField valueField = new JTextField(15);
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{keyField.getText(),valueField.getText()});
            }
        });

		model.addColumn("Key");
		model.addColumn("Value");
		populateTable();
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	presentationNode.setId(idTextField.getText());
            	Element element = presentationNode.getElement();
            	
            	for (int i = 0; i < model.getRowCount(); i++) {
            		String key = (String) model.getValueAt(i, 0);
            		String value = (String) model.getValueAt(i, 1);
            		
            		if (key.equals("prov:type")) {
            			if (!element.getTypes().contains(value)) {
            				element.addType(value);
            			}
            		} else if (key.equals("prov:label")) {
            			if (!element.getLabels().contains(value)) {
            				element.addLabel(value);
            			}
            		} else if (key.equals("prov:location")) {
            			if (!element.getLocations().contains(value)) {
            				element.addLocation(value);
            			}
            		} else {
            			if (!value.equals(element.getValue(key))) {
            				element.addValue(key, value);
            			}
            		}
            	}
            	
            	JOptionPane.showMessageDialog(NodeEditorFrame.this,
            			"Saved Successfully. Please note, this does not save changes to file. Please use File > Save to do so.");
            }
        });
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	NodeEditorFrame.this.dispose();
            }
        });

		mainPanel.add(idLabel);
		mainPanel.add(idTextField, "wrap");

		mainPanel.add(keyLabel);
		mainPanel.add(keyField);

		mainPanel.add(valueLabel, "gap");
		mainPanel.add(valueField);
		
		mainPanel.add(addButton, "wrap");

		mainPanel.add(new JLabel("Attributes"), "wrap");

		mainPanel.add(scrollPane, "span 6, wrap");
		mainPanel.add(saveButton);
		mainPanel.add(closeButton, "gap");

		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	private void populateTable() {
		
		Element element = presentationNode.getElement();
		
		for (String type : element.getTypes()) {
			model.addRow(new Object[]{"prov:type",type});
		}
		
		for (String label : element.getLabels()) {
			model.addRow(new Object[]{"prov:label",label});
		}
		
		for (String location : element.getLocations()) {
			model.addRow(new Object[]{"prov:location",location});
		}
		
		for (Map.Entry<String, String> entry : element.getValues().entrySet()) {
			model.addRow(new Object[]{entry.getKey(),entry.getValue()});
		}
	}

}
