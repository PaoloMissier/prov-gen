package uk.ac.ncl.prov.generator.model;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openprovenance.prov.java.AlternateRelation;
import org.openprovenance.prov.java.Element;
import org.openprovenance.prov.java.NonAlternateRelation;
import org.openprovenance.prov.java.Relation;
import org.openprovenance.prov.java.component1.Activity;
import org.openprovenance.prov.java.component1.Entity;
import org.openprovenance.prov.java.component1.Used;
import org.openprovenance.prov.java.component1.WasEndedBy;
import org.openprovenance.prov.java.component1.WasGeneratedBy;
import org.openprovenance.prov.java.component1.WasInformedBy;
import org.openprovenance.prov.java.component1.WasInvalidatedBy;
import org.openprovenance.prov.java.component1.WasStartedBy;
import org.openprovenance.prov.java.component2.WasDerivedFrom;
import org.openprovenance.prov.java.component3.ActedOnBehalfOf;
import org.openprovenance.prov.java.component3.Agent;
import org.openprovenance.prov.java.component3.WasAssociatedWith;
import org.openprovenance.prov.java.component3.WasAttributedTo;
import org.openprovenance.prov.java.component3.WasInfluencedBy;
import org.openprovenance.prov.java.component4.Bundle;
import org.openprovenance.prov.java.component5.AlternateOf;
import org.openprovenance.prov.java.component5.MentionOf;
import org.openprovenance.prov.java.component5.SpecializationOf;

import uk.ac.ncl.prov.generator.ui.GraphListener;

public class PresentationGraph {

	private Bundle graph;
	private List<PresentationNode> presentationNodes = new ArrayList<PresentationNode>();
	private List<PresentationRelation> presentationRelations = new ArrayList<PresentationRelation>();
	private ArrayList<GraphListener> listeners = new ArrayList<GraphListener>();

	private FocusablePresentationElement focusedElement;
	private boolean isMoving;
	private boolean isConnecting;
	private Ribbon ribbon;

	private int largestX;
	private int largestY;

	public PresentationGraph(Bundle graph) {

		// Maybe should iterate over nodes and relations and create presentation
		// here.
		this.graph = graph;
		generatePresentation();
	}

	private void generatePresentation() {

		List<Relation> allRelations = getAllRelations(graph);
		List<Element> allElements = getAllElements(graph);

		// TODO: PRESENTATION RELATION CAN HAVE MULTIPLE END POINTS??
		for (Relation relation : allRelations) {
			PresentationRelation pr;
			PresentationNode startNode = getNodeById(relation.getStartElement()
					.getId());
			
			if (startNode == null) {
				startNode = new PresentationNode(relation.getStartElement());
				positionNode(startNode);
				presentationNodes.add(startNode);
			}
			
			PresentationNode endNode = null;
			if (relation.getEndElements().size() > 0) {
				endNode = getNodeById(relation.getEndElements().get(0).getId());

				if (endNode == null) {
					endNode = new PresentationNode(relation.getEndElements().get(0));
					positionNode(endNode);
					presentationNodes.add(endNode);
				}
			}

			if (relation instanceof NonAlternateRelation) {
				pr = new PresentationNonAlternateRelation(
						(NonAlternateRelation) relation, startNode, endNode);
			} else {
				pr = new PresentationAlternateRelation(
						(AlternateRelation) relation, startNode, endNode);
			}

			presentationRelations.add(pr);
		}

		for (Element element : allElements) {

			PresentationNode newNode = getNodeById(element.getId());
			if (newNode == null) {
				newNode = new PresentationNode(element);
				presentationNodes.add(newNode);
				positionNode(newNode);
			}
		}

	}

	private PresentationNode getNodeById(String id) {

		for (PresentationNode node : presentationNodes) {
			if (node.getId().equals(id)) {
				return node;
			}
		}

		return null;
	}

	private List<Element> getAllElements(Bundle bundle) {

		List<Element> bundleElements = bundle.getRecords().getElements();
		List<Element> nonLiveElements = new ArrayList<Element>();

		nonLiveElements.addAll(bundleElements);

		for (Bundle b : bundle.getBundles()) {
			nonLiveElements.addAll(getAllElements(b));
		}

		return nonLiveElements;
	}

	// Recurse through bundles to get all the relations
	private List<Relation> getAllRelations(Bundle bundle) {

		List<Relation> bundleRelations = bundle.getRecords().getRelations();
		List<Relation> nonLiveRelations = new ArrayList<Relation>();

		nonLiveRelations.addAll(bundleRelations);

		for (Bundle b : bundle.getBundles()) {
			nonLiveRelations.addAll(getAllRelations(b));
		}

		return nonLiveRelations;
	}

	public Bundle getGraph() {
		return this.graph;
	}

	public PresentationNode createElement(String type, String id,
			int minCardinality, int maxCardinality) {

		Element newElement = null;

		if (type.equals("Entity")) {
			newElement = new Entity();
		} else if (type.equals("Activity")) {
			newElement = new Activity();
		} else if (type.equals("Agent")) {
			newElement = new Agent();
		}

		PresentationNode newPresentationNode = new PresentationNode(newElement);
		newPresentationNode.setId(id);
		newPresentationNode.setCardinalities(minCardinality, maxCardinality);

		graph.addRecord(newElement);
		presentationNodes.add(newPresentationNode);
		positionNode(newPresentationNode);

		notifyListeners();

		return newPresentationNode;
	}

	public PresentationRelation createNonAlternateRelation(String type,
			String id, PresentationNode startNode, PresentationNode endNode,
			double minSaturation, double maxSaturation) {

		NonAlternateRelation newRelation = null;

		if (type.equals("Used")) {
			
			newRelation = new Used();
			((Used) newRelation).setActivity((Activity) startNode.getElement());
			((Used) newRelation).setEntity((Entity) endNode.getElement());
			
		} else if (type.equals("WasGeneratedBy")) {
			
			newRelation = new WasGeneratedBy();
			((WasGeneratedBy) newRelation).setEntity((Entity) startNode
					.getElement());
			((WasGeneratedBy) newRelation).setActivity((Activity) endNode
					.getElement());
			
		} else if (type.equals("WasStartedBy")) {
			
			newRelation = new WasStartedBy();
			((WasStartedBy) newRelation).setActivity((Activity) startNode.getElement());
			((WasStartedBy) newRelation).setTrigger((Entity) endNode.getElement());
			
		} else if (type.equals("WasEndedBy")) {
			
			newRelation = new WasEndedBy();
			((WasEndedBy) newRelation).setActivity((Activity) startNode.getElement());
			((WasEndedBy) newRelation).setTrigger((Entity) endNode.getElement());
			
		} else if (type.equals("WasInformedBy")) {
			
			newRelation = new WasInformedBy();
			((WasInformedBy) newRelation).setEffect((Activity) startNode.getElement());
			((WasInformedBy) newRelation).setCause((Activity) endNode.getElement());
			
		} else if (type.equals("WasInvalidatedBy")) {
			
			newRelation = new WasInvalidatedBy();
			((WasInvalidatedBy) newRelation).setEntity((Entity) startNode.getElement());
			((WasInvalidatedBy) newRelation).setActivity((Activity) endNode.getElement());
			
		} else if (type.equals("WasDerivedFrom")) {
			
			newRelation = new WasDerivedFrom();
			((WasDerivedFrom) newRelation).setGeneratedEntity((Entity) startNode.getElement());
			((WasDerivedFrom) newRelation).setUsedEntity((Entity) endNode.getElement());
			
		} else if (type.equals("WasInfluencedBy")) {
			
			newRelation = new WasInfluencedBy();
			((WasInfluencedBy) newRelation).setInfluencee((Agent) startNode.getElement());
			((WasInfluencedBy) newRelation).setInfluencer((Agent) endNode.getElement());
			
		} else if (type.equals("WasAttributedTo")) {
			
			newRelation = new WasAttributedTo();
			((WasAttributedTo) newRelation).setEntity((Entity) startNode.getElement());
			((WasAttributedTo) newRelation).setAgent((Agent) endNode.getElement());
			
		} else if (type.equals("WasAssociatedWith")) {
			
			newRelation = new WasAssociatedWith();
			((WasAssociatedWith) newRelation).setActivity((Activity) startNode.getElement());
			((WasAssociatedWith) newRelation).setAgent((Agent) endNode.getElement());
			
		} else if (type.equals("ActedOnBehalfOf")) {
			
			newRelation = new ActedOnBehalfOf();
			((ActedOnBehalfOf) newRelation).setSubordinate((Agent) startNode.getElement());
			((ActedOnBehalfOf) newRelation).setResponsible((Agent) endNode.getElement());
			
		} 

		PresentationNonAlternateRelation presentationRelation = new PresentationNonAlternateRelation(
				newRelation, startNode, endNode);
		presentationRelation.setId(id);
		presentationRelation.setSaturations(minSaturation, maxSaturation);

		graph.addRecord(newRelation);
		presentationRelations.add(presentationRelation);
		startNode.getElement().addRelation(newRelation);

		notifyListeners();

		return presentationRelation;
	}
	
	public PresentationRelation createAlternateRelation(String type, String id, PresentationNode startNode, PresentationNode endNode) {
		
		AlternateRelation newRelation = null;

		if (type.equals("AlternateOf")) {
			
			newRelation = new AlternateOf();
			((AlternateOf) newRelation).setAlternate1((Entity) startNode.getElement());
			((AlternateOf) newRelation).setAlternate2((Entity) endNode.getElement());
			
		} else if (type.equals("SpecializationOf")) {
			
			newRelation = new SpecializationOf();
			((SpecializationOf) newRelation).setSpecializedEntity((Entity) startNode
					.getElement());
			((SpecializationOf) newRelation).setGeneralEntity((Entity) endNode
					.getElement());
			
		} else if (type.equals("MentionOf")) {
			
			newRelation = new MentionOf();
			((MentionOf) newRelation).setSpecializedEntity((Entity) startNode
					.getElement());
			((MentionOf) newRelation).setGeneralEntity((Entity) endNode
					.getElement());
			
		}

		PresentationAlternateRelation presentationRelation = new PresentationAlternateRelation(
				newRelation, startNode, endNode);

		graph.addRecord(newRelation);
		presentationRelations.add(presentationRelation);
		startNode.getElement().addRelation(newRelation);

		notifyListeners();

		return presentationRelation;
	}

	public Iterator<PresentationRelation> relations() {

		return presentationRelations.iterator();
	}

	public Iterator<PresentationNode> nodes() {

		return presentationNodes.iterator();
	}

	public PresentationElement getElementAt(int x, int y) {

		for (PresentationNode node : presentationNodes) {

			Rectangle r = new Rectangle(node.getX(), node.getY(),
					node.getWidth(), node.getHeight());

			if (r.contains(x, y))
				return node;
		}

		for (PresentationRelation relation : presentationRelations) {

			PresentationNode startNode = relation.getStartNode();
			PresentationNode endNode = relation.getEndNode();

			if (endNode != null) {
				int startCenterX = startNode.getX() + (startNode.getWidth() / 2);
				int startCenterY = startNode.getY() + (startNode.getHeight() / 2);
				int endCenterX = endNode.getX() + endNode.getWidth();
				int endCenterY = endNode.getY() + (endNode.getHeight() / 2);
	
				Line2D l = new Line2D.Double(startCenterX, startCenterY,
						endCenterX, endCenterY);
	
				if (l.ptSegDist(x, y) < 2) {
					return relation;
				}
			}
		}

		return null;
	}

	public void deleteFocused() {

		if (focusedElement instanceof PresentationNode) {

			PresentationNode presentationNode = (PresentationNode) focusedElement;

			graph.removeRecord(presentationNode.getElement());
			presentationNodes.remove(presentationNode);

			removeEdgesAssociatedWithNode(presentationNode);

		} else if (focusedElement instanceof PresentationRelation) {

			PresentationRelation presentationRelation = (PresentationRelation) focusedElement;
			graph.removeRecord(presentationRelation.getRelation());
			presentationRelations.remove(presentationRelation);
		}

		notifyListeners();
	}

	public void removeEdgesAssociatedWithNode(PresentationNode presentationNode) {

		List<PresentationRelation> edgesToRemove = new ArrayList<PresentationRelation>();

		for (PresentationRelation presentationRelation : presentationRelations) {

			if (presentationRelation.getStartNode().equals(presentationNode)
					|| presentationRelation.getEndNode().equals(
							presentationNode)) {

				edgesToRemove.add(presentationRelation);
			}
		}

		graph.removeRecord(presentationNode.getElement());

		presentationRelations.removeAll(edgesToRemove);
	}

	public FocusablePresentationElement getFocus() {

		return focusedElement;
	}

	public void switchFocus(PresentationElement element) {

		// Unfocus current element
		if (focusedElement != null) {
			focusedElement.setFocus(false);
			focusedElement = null;
		}

		// If the element clicked is focusable, focus on it
		if (element instanceof FocusablePresentationElement) {

			((FocusablePresentationElement) element).setFocus(true);
			focusedElement = (FocusablePresentationElement) element;
		}

		notifyListeners();
	}

	public void moveFocusedNode(int x, int y) {

		((PresentationNode) focusedElement).setPos(x, y);

		largestX = x > largestX ? x : largestX;
		largestY = y > largestY ? y : largestY;
		notifyListeners();
	}

	public boolean isMoving() {

		return isMoving;
	}

	public void setMoving(boolean isMoving) {

		this.isMoving = isMoving;
	}

	public boolean isConnecting() {

		return isConnecting;
	}

	public Ribbon getRibbon() {

		return ribbon;
	}

	public void setRibbon(Ribbon ribbon) {

		this.ribbon = ribbon;
	}

	public void setConnecting(boolean isConnecting) {

		this.isConnecting = isConnecting;
	}

	private void positionNode(PresentationNode pn) {

		int x = 10;
		int y = 10;
		boolean foundSpace = false;

		while (!foundSpace) {
			PresentationElement element = getElementAt(x, y);
			if (element == null || element == pn) {

				pn.setPos(x, y);
				foundSpace = true;
			} else {

				x += 100;
			}
		}

		largestX = x > largestX ? x : largestX;
		largestY = y > largestY ? y : largestY;
	}

	public int getLargestX() {
		return largestX;
	}

	public int getLargestY() {
		return largestY;
	}

	// Notify listeners of graph changes
	public void notifyListeners() {

		for (GraphListener l : listeners) {

			l.graphChanged();
		}
	}

	public void addListener(GraphListener l) {

		listeners.add(l);
	}

}
