package uk.ac.ncl.prov.generator.model;

import org.openprovenance.prov.java.Relation;

public abstract class PresentationRelation extends FocusablePresentationElement {

	private Relation relation;
	private PresentationNode startNode;
	private PresentationNode endNode;
	
	public PresentationRelation(Relation relation, PresentationNode startNode, PresentationNode endNode) {
		
		this.relation = relation;
		this.startNode = startNode;
		this.endNode = endNode;
	}
	
	public Relation getRelation() {
		return this.relation;
	}
	
	public PresentationNode getStartNode() {
		
		return startNode;
	}
	
	public PresentationNode getEndNode() {
		
		return endNode;
	}
	
}
