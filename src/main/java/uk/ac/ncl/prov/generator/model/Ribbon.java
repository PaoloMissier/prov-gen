package uk.ac.ncl.prov.generator.model;

/* The Ribbon class acts as an intermediate stage for a connection before an end point is chosen. 
 * It expands as the user drags their mouse in the FocusableController mouseDragged method */
public class Ribbon {

	private int startX, startY, endX, endY;

	public Ribbon(int startX, int startY, int endX, int endY) {

		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}
	
	public int startX() {
		
		return startX;
	}
	
	public int startY() {
		
		return startY;
	}
	
	public int endX() {
		
		return endX;
	}
	
	public int endY() {
		
		return endY;
	}

	public void setEnd(int endX, int endY) {

		this.endX = endX;
		this.endY = endY;
	}

	/* Is this ribbon shorter than the one in the argument */
	public boolean shorterThan(Ribbon other) {

		/* Pythagorian to determine line length */
		double thisLength = Math.sqrt(Math.pow(startX - endX, 2)
				+ Math.pow(startY - endY, 2));
		double otherLength = Math.sqrt(Math.pow(other.startX - other.endX, 2)
				+ Math.pow(other.startY - other.endY, 2));

		return thisLength < otherLength;
	}

}
