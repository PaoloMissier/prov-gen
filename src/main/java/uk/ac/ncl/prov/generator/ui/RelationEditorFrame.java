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
import org.openprovenance.prov.java.HasLocation;
import org.openprovenance.prov.java.HasRole;
import org.openprovenance.prov.java.NonAlternateRelation;

import uk.ac.ncl.prov.generator.model.PresentationNonAlternateRelation;

public class RelationEditorFrame extends JFrame {

	static final int DEFAULT_WIDTH = 600;
	static final int DEFAULT_HEIGHT = 400;

	private PresentationNonAlternateRelation presentationRelation;
	private JPanel mainPanel;
	private DefaultTableModel model = new DefaultTableModel();

	public RelationEditorFrame(PresentationNonAlternateRelation presentationRelation) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		this.presentationRelation = presentationRelation;
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
		idTextField.setText(presentationRelation.getId());

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
            	presentationRelation.setId(idTextField.getText());
            	NonAlternateRelation relation = (NonAlternateRelation) presentationRelation.getRelation();
            	
            	for (int i = 0; i < model.getRowCount(); i++) {
            		String key = (String) model.getValueAt(i, 0);
            		String value = (String) model.getValueAt(i, 1);
            		
            		if (key.equals("prov:type")) {
            			if (!relation.getTypes().contains(value)) {
            				relation.addType(value);
            			}
            		} else if (key.equals("prov:label")) {
            			if (!relation.getLabels().contains(value)) {
            				relation.addLabel(value);
            			}
            		} else {
            			if (!value.equals(relation.getValue(key))) {
            				relation.addValue(key, value);
            			}
            		}
            		
            		// Check any special attributes that extended relations may have
            		if (relation instanceof HasLocation) {
            			if (key.equals("prov:location")) {
                			if (!((HasLocation)relation).getLocations().contains(value)) {
                				((HasLocation) relation).addLocation(value);
                			}
            			}
            		} else if (relation instanceof HasRole) {
            			if (key.equals("prov:role")) {
                			if (!((HasRole)relation).getRoles().contains(value)) {
                				((HasRole) relation).addRole(value);
                			}
            			}
            		}
            	}
            	
            	JOptionPane.showMessageDialog(RelationEditorFrame.this,
            		    "Saved Successfully. Please note, this does not save changes to file. Please use File > Save to do so.");
            }
        });
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	RelationEditorFrame.this.dispose();
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
		
		NonAlternateRelation relation = (NonAlternateRelation) presentationRelation.getRelation();
		
		for (String type : relation.getTypes()) {
			model.addRow(new Object[]{"prov:type",type});
		}
		
		for (String label : relation.getLabels()) {
			model.addRow(new Object[]{"prov:label",label});
		}
		
		if (relation instanceof HasLocation) {
			for (String location : ((HasLocation) relation).getLocations()) {
				model.addRow(new Object[]{"prov:location",location});
			}
		} else if (relation instanceof HasRole) {
			for (String role : ((HasRole) relation).getRoles()) {
				model.addRow(new Object[]{"prov:role",role});
			}
		}
		
		for (Map.Entry<String, String> entry : relation.getValues().entrySet()) {
			model.addRow(new Object[]{entry.getKey(),entry.getValue()});
		}
	}

}
