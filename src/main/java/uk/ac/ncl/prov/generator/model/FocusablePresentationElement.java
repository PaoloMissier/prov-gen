package uk.ac.ncl.prov.generator.model;

public abstract class FocusablePresentationElement extends PresentationElement implements Focusable {

	private boolean isFocus;
	
	public void setFocus(boolean isFocus) {
		this.isFocus = isFocus;
	}
	
	public boolean isFocus() {
		return isFocus;
	}
}
