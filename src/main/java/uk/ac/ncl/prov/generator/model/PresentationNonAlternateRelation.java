package uk.ac.ncl.prov.generator.model;

import org.openprovenance.prov.java.NonAlternateRelation;
import org.openprovenance.prov.java.Relation;

public class PresentationNonAlternateRelation extends PresentationRelation {

	private NonAlternateRelation relation;
		
	public PresentationNonAlternateRelation(NonAlternateRelation relation, PresentationNode startNode, PresentationNode endNode) {
		
		super(relation, startNode, endNode);
		this.relation = relation;
	}
	
	public String getId() {
		
		return relation.getId();
	}
	
	public void setId(String id) {
		
		relation.setId(id);
	}
	
	public double getMinSaturation() {
		String minSat = relation.getValue("gen:minSaturation");
		return minSat == null ? 1.0 : Double.parseDouble(relation.getValue("gen:minSaturation"));
	}
	
	public double getMaxSaturation() {
		String maxSat = relation.getValue("gen:maxSaturation");
		return maxSat == null ? 1.0 : Double.parseDouble(relation.getValue("gen:maxSaturation"));	
	}
	
	public void setMinSaturation(double minSaturation) {
		relation.removeValue("gen:minSaturation");
		relation.addValue("gen:minSaturation", String.valueOf(minSaturation));
	}
	
	public void setMaxSaturation(double maxSaturation) {
		relation.removeValue("gen:maxSaturation");
		relation.addValue("gen:maxSaturation", String.valueOf(maxSaturation));
	}
	
	public void setSaturations(double minSaturation, double maxSaturation) {
		setMinSaturation(minSaturation);
		setMaxSaturation(maxSaturation);
	}
	
	public Relation getRelation() {
		return relation;
	}
	
}
