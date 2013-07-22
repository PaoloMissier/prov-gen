package uk.ac.ncl.prov.generator.model;

import org.openprovenance.prov.java.AlternateRelation;

public class PresentationAlternateRelation extends PresentationRelation {
	
	private AlternateRelation relation;
	
	public PresentationAlternateRelation(AlternateRelation relation, PresentationNode startNode, PresentationNode endNode) {
		
		super(relation, startNode, endNode);
		this.relation = relation;
	}

	public AlternateRelation getRelation() {
		return relation;
	}

}
