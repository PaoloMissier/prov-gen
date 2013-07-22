package uk.ac.ncl.prov.gen.generator;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openprovenance.prov.java.Element;

public class SuperElement extends Element {
	
	private List<Element> elements;
	
	public SuperElement(List<Element> elements) {
		this.elements = elements;
	}
	
	public boolean addElement(Element element) {
		return elements.add(element);
	}
	
	public Element getElement(int i) {
		return elements.get(i);
	}
	
	public boolean removeElement(Element element) {
		return elements.remove(element);
	}
	
	public List<Element> getElements() {
		return elements;
	}

	private void toString(ToStringBuilder toStringBuilder) {
		toStringBuilder.append("elements", this.elements);
	}
	
	public String toString() {
		final ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		toString(toStringBuilder);
		return toStringBuilder.toString();
	}
}
