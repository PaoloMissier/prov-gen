package uk.ac.ncl.prov.generator.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openprovenance.prov.java.Construct;
import org.openprovenance.prov.java.Element;
import org.openprovenance.prov.java.Record;

public class PresentationNode extends FocusablePresentationElement {

	private Element element;
	private int x, y;
	
	public PresentationNode(Element element) {
		
		this.element = element;
	}
	
	public int getX() {
		
		return x;
	}
	
	public void setX(int x) {
		
		this.x = x;
	}
	
	public int getY() {
		
		return y;
	}
	
	public void setY(int y) {
		
		this.y = y;
	}
	
	public void setPos(int x, int y) {
		
		this.x = x;
		this.y = y;
	}
	
	public int getHeight() {
		
		return 40;
	}
	
	public int getWidth() {
		
		return 80;
	}
	
	public String getId() {
		
		return element.getId();
	}
	
	public void setId(String id) {
		
		element.setId(id);
	}
	
	public int getMinCardinality() {
		String minCard = element.getValue("gen:minCardinality");
		return minCard == null ? 1 : Integer.parseInt(element.getValue("gen:minCardinality"));
	}
	
	public int getMaxCardinality() {
		String maxCard = element.getValue("gen:maxCardinality");
		return maxCard == null ? 1 : Integer.parseInt(element.getValue("gen:maxCardinality"));	
	}
	
	public void setMinCardinality(int minCardinality) {
		element.removeValue("gen:minCardinality");
		element.addValue("gen:minCardinality", String.valueOf(minCardinality));
	}
	
	public void setMaxCardinality(int maxCardinality) {
		element.removeValue("gen:maxCardinality");
		element.addValue("gen:maxCardinality", String.valueOf(maxCardinality));
	}
	
	public void setCardinalities(int minCardinality, int maxCardinality) {
		setMinCardinality(minCardinality);
		setMaxCardinality(maxCardinality);
	}
	
	public Element getElement() {
		
		return element;
	}
	
	private void equals(Object object, EqualsBuilder equalsBuilder) {
		if (!(object instanceof PresentationNode)) {
			equalsBuilder.appendSuper(false);
			return;
		}
		if (this == object) {
			return;
		}
		final PresentationNode that = ((PresentationNode) object);
		equalsBuilder.append(this.getId(), that.getId());
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof PresentationNode)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		final EqualsBuilder equalsBuilder = new EqualsBuilder();
		equals(object, equalsBuilder);
		return equalsBuilder.isEquals();
	}
	
	private void hashCode(HashCodeBuilder hashCodeBuilder) {
		hashCodeBuilder.append(this.getId());
	}
	
	public int hashCode() {
		final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		this.hashCode(hashCodeBuilder);
		return hashCodeBuilder.toHashCode();
	}
	
}
