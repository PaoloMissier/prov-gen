package uk.ac.ncl.prov.generator.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.openprovenance.prov.java.Element;
import org.openprovenance.prov.java.component1.Activity;
import org.openprovenance.prov.java.component1.Entity;
import org.openprovenance.prov.java.component3.Agent;

import uk.ac.ncl.prov.generator.model.PresentationElement;
import uk.ac.ncl.prov.generator.model.PresentationGraph;
import uk.ac.ncl.prov.generator.model.PresentationNode;
import uk.ac.ncl.prov.generator.model.PresentationNonAlternateRelation;
import uk.ac.ncl.prov.generator.model.Ribbon;
import uk.ac.ncl.prov.generator.ui.painters.GraphPainter;

public class GraphEditor extends JComponent implements GraphListener, MouseListener, MouseMotionListener, KeyListener {

	private PresentationGraph presentationGraph;
	private GraphPainter graphPainter = new GraphPainter();

	private final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private Cursor currentCursor = DEFAULT_CURSOR;

	private Point offset = new Point();

	public GraphEditor(PresentationGraph presentationGraph) {

		this.presentationGraph = presentationGraph;
		this.presentationGraph.addListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	public void graphChanged() {

		repaint();
	}
	
	public Dimension getPreferredSize() {
		
		return new Dimension(presentationGraph.getLargestX() + 100, presentationGraph.getLargestY() + 100);
	}

	public void paint(Graphics g) {

		super.paint(g);

		Graphics2D g2D = (Graphics2D) g;
		Dimension size = getSize();

		GradientPaint gp = new GradientPaint(0, 0, new Color(239, 230, 213),
				size.width, 0, new Color(215, 207, 191));

		g2D.setPaint(gp);
		g2D.fillRect(0, 0, size.width, size.height);

		graphPainter.paint(g2D, presentationGraph);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {

			PresentationElement element = presentationGraph.getElementAt(
					e.getX(), e.getY());

			if (element instanceof PresentationNode) {

				NodeEditorFrame nodeEditorFrame = new NodeEditorFrame((PresentationNode) element);
			} else if (element instanceof PresentationNonAlternateRelation) {
				
				RelationEditorFrame relationEditorFrame = new RelationEditorFrame((PresentationNonAlternateRelation) element);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {

		requestFocus();

		if ((e.getButton() == 1)) {

			PresentationElement element = presentationGraph.getElementAt(
					e.getX(), e.getY());
			presentationGraph.switchFocus(element);

			if (element instanceof PresentationNode) {

				if (presentationGraph.isConnecting()) {

					presentationGraph.setRibbon(new Ribbon(e.getX(), e.getY(),
							e.getX(), e.getY()));
				} else {

					PresentationNode node = (PresentationNode) element;

					presentationGraph.setMoving(true);
					offset.x = e.getX() - node.getX();
					offset.y = e.getY() - node.getY();
				}
			} 
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		PresentationElement element = presentationGraph.getElementAt(e.getX(),
				e.getY());

		if (presentationGraph.isConnecting()) {

			/*
			 * If the end of the ribbon is a Element then create a concrete
			 * connection
			 */
			if (element instanceof PresentationNode) {

				Ribbon ribbon = presentationGraph.getRibbon();

				PresentationNode startElement = (PresentationNode) presentationGraph
						.getElementAt(ribbon.startX(), ribbon.startY());
				PresentationNode endElement = (PresentationNode) element;
				
				String[] possibilities = generateRelationPossibilities(startElement, endElement);

				JTextField idField = new JTextField();
				JComboBox relationType = new JComboBox(possibilities);
				JTextField minSaturationField = new JTextField();
				JTextField maxSaturationField = new JTextField();
				
				final JComponent[] inputs = new JComponent[] { new JLabel("ID"), idField,
						new JLabel("Type"),
						relationType, new JLabel("Min Saturation"), minSaturationField,
						new JLabel("Max Saturation"), maxSaturationField };

				JOptionPane.showMessageDialog(this, inputs, "Create an edge",
						JOptionPane.PLAIN_MESSAGE);

				try {
					String id = idField.getText();
					String type = (String) relationType.getSelectedItem();
					double minSaturation = Double.parseDouble(minSaturationField.getText());
					double maxSaturation = Double.parseDouble(maxSaturationField.getText());

					if (type != null && minSaturation <= maxSaturation) {

						if (type.equals("AlternateOf") || type.equals("SpecializationOf") || type.equals("MentionOf")) {
							presentationGraph.createAlternateRelation(type, id, startElement, endElement);
						} else {
							presentationGraph.createNonAlternateRelation(type, id, startElement, endElement, minSaturation, maxSaturation);
						}
					}
				} catch (Exception ex) {

					System.out.println(ex);
				}

				presentationGraph.setConnecting(false);
				presentationGraph.setRibbon(null);
			} else {

				presentationGraph.setConnecting(false);
				presentationGraph.setRibbon(null);
			}

		}

		revalidate();
		repaint();
		presentationGraph.setMoving(false);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		Ribbon ribbon = presentationGraph.getRibbon();

		if (ribbon != null) {

			/* Update the end point of the ribbon if we have one */
			ribbon.setEnd(e.getX(), e.getY());
			repaint();
		} else {

			if (presentationGraph.isMoving()) {

				/* Redraw the node in its new position */
				int x = e.getX() - offset.x;
				int y = e.getY() - offset.y;
				presentationGraph.moveFocusedNode(x, y);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		PresentationElement element = presentationGraph.getElementAt(e.getX(),
				e.getY());

		Cursor newCursor = DEFAULT_CURSOR;
		if (element != null) {

			newCursor = HAND_CURSOR;
		}

		if (currentCursor != newCursor) {
			setCursor(newCursor);
			currentCursor = newCursor;
		}
	}

	@Override
	public void keyPressed(KeyEvent ke) {

	}

	@Override
	public void keyReleased(KeyEvent ke) {

		if (ke.getKeyCode() == KeyEvent.VK_DELETE || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

			presentationGraph.deleteFocused();
		} else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {

			PresentationElement element = presentationGraph.getFocus();

			if (element instanceof PresentationNode) {

				NodeEditorFrame nodeEditorFrame = new NodeEditorFrame((PresentationNode) element);
			} else if (element instanceof PresentationNonAlternateRelation) {
				
				RelationEditorFrame relationEditorFrame = new RelationEditorFrame((PresentationNonAlternateRelation) element);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	private String[] generateRelationPossibilities(PresentationNode startNode, PresentationNode endNode) {
		
		Map<String, List<String>> possibilities = getAllPossibilities();
		Element startElement = startNode.getElement();
		Element endElement = endNode.getElement();
		String key = getMappingKey(startElement, endElement);
		
		List<String> useablePossibilities = possibilities.get(key);
		return useablePossibilities.toArray(new String[useablePossibilities.size()]);
	}
	
	// Generate all possible mappings so we can display only relevant ones to user upon edge creation
	private Map<String, List<String>> getAllPossibilities() {
		
		Map<String, List<String>> possibilities = new HashMap<String, List<String>>();
		
		List<String> entityToEntityPossibilities = new ArrayList<String>();
		entityToEntityPossibilities.add("WasDerivedFrom");
		entityToEntityPossibilities.add("AlternateOf");
		entityToEntityPossibilities.add("SpecializationOf");
		entityToEntityPossibilities.add("MentionOf");
		possibilities.put("entityentity", entityToEntityPossibilities);
		
		List<String> entityToActivityPossibilities = new ArrayList<String>();
		entityToActivityPossibilities.add("WasGeneratedBy");
		entityToActivityPossibilities.add("WasInvalidatedBy");
		possibilities.put("entityactivity", entityToActivityPossibilities);
		
		List<String> entityToAgentPossibilities = new ArrayList<String>();
		entityToAgentPossibilities.add("WasAttributedTo");
		possibilities.put("entityagent", entityToAgentPossibilities);
		
		List<String> activityToActivityPossibilities = new ArrayList<String>();
		activityToActivityPossibilities.add("WasInformedBy");
		possibilities.put("activityactivity", activityToActivityPossibilities);
		
		List<String> activityToEntityPossibilities = new ArrayList<String>();
		activityToEntityPossibilities.add("Used");
		activityToEntityPossibilities.add("WasStartedBy");
		activityToEntityPossibilities.add("WasEndedBy");
		possibilities.put("activityentity", activityToEntityPossibilities);
		
		List<String> activityToAgentPossibilities = new ArrayList<String>();
		activityToAgentPossibilities.add("WasAssociatedWith");
		possibilities.put("activityagent", activityToAgentPossibilities);
		
		List<String> agentToAgentPossibilities = new ArrayList<String>();
		agentToAgentPossibilities.add("ActedOnBehalfOf");
		agentToAgentPossibilities.add("WasInfluencedBy");
		possibilities.put("agentagent", agentToAgentPossibilities);
		
		return possibilities;
	}
	
	// From the elements, get the key to look into possible relations
	private String getMappingKey(Element startElement, Element endElement) {
		
		String start = "";
		String end = "";
		
		if (startElement instanceof Entity) {
			start = "entity";
		} else if (startElement instanceof Activity) {
			start = "activity";
		} else if (startElement instanceof Agent) {
			start = "agent";
		}
		
		if (endElement instanceof Entity) {
			end = "entity";
		} else if (endElement instanceof Activity) {
			end = "activity";
		} else if (endElement instanceof Agent) {
			end = "agent";
		}
		
		return start + end;
	}
}