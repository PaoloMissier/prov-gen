package uk.ac.ncl.prov.generator.ui.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.SwingUtilities;

import uk.ac.ncl.prov.generator.model.PresentationNode;
import uk.ac.ncl.prov.generator.model.PresentationNonAlternateRelation;
import uk.ac.ncl.prov.generator.model.PresentationRelation;
import uk.ac.ncl.prov.generator.model.Ribbon;

public class RelationPainter {

	public RelationPainter() {}

	public void paint(Graphics2D g2D, PresentationRelation e) {

		g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL));

		g2D.setColor(new Color(0, 0, 0));
		
		// If no end node, nothing to draw
		if (e.getEndNode() != null) {
			Ribbon r = getRibbon(e.getStartNode(), e.getEndNode());
			drawEdge(g2D, r, e.isFocus());
			drawEdgeText(g2D, r, e);
		}
	}

	public void drawEdge(Graphics2D g2D, Ribbon r, boolean isFocus) {
			
		if (isFocus) {
			g2D.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL));
		} else {
			g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
		}
		
		g2D.setColor(new Color(0, 0, 0));
				
		g2D.drawLine(r.startX(), r.startY(),
				r.endX(), r.endY());
		
		drawArrowHead(g2D, r);
	}
	
	public void drawArrowHead(Graphics2D g2D, Ribbon r) {
		
		double phi = Math.toRadians(30);  
        int barb = 10; 
        
        double dy = r.endY() - r.startY();  
        double dx = r.endX() - r.startX();  
        double theta = Math.atan2(dy, dx);  

        double x, y, rho = theta + phi;  
        for(int j = 0; j < 2; j++)  
        {  
            x = r.endX() - barb * Math.cos(rho);  
            y = r.endY() - barb * Math.sin(rho);  
            g2D.draw(new Line2D.Double(r.endX(), r.endY(), x, y));  
            rho = theta - phi;  
        }  
    }  
	
	private void drawEdgeText(Graphics2D g2D, Ribbon r, PresentationRelation e) {

//		double lineLength = Math.sqrt(Math.pow(r.startX() - r.endX(), 2)
//				+ Math.pow(r.startY() - r.endY(), 2));
		
//		g2D.translate(r.endX(), (r.startY() + r.endY()) / 2);
		g2D.translate((r.startX() + r.endX()) / 2, (r.startY() + r.endY()) / 2);

		String edgeText;
		
		if (e instanceof PresentationNonAlternateRelation) {
			edgeText = e.getRelation().getClass().getSimpleName() + "[" + ((PresentationNonAlternateRelation) e).getMinSaturation() + "," + ((PresentationNonAlternateRelation) e).getMaxSaturation() + "]";
		} else {
			edgeText = e.getRelation().getClass().getSimpleName();
		}
		
		FontMetrics fm = g2D.getFontMetrics();
		int textWidth = SwingUtilities.computeStringWidth(fm, edgeText);
		g2D.setPaint(Color.BLACK);
		
//		g2D.drawString(edgeText, (int) ((lineLength / 2) - (textWidth / 2)), 20);
		g2D.drawString(edgeText, -((textWidth + (e.getStartNode().getWidth() / 2)) / 2), 20);

		
//		g2D.translate(-r.endX(), -(r.startY() + r.endY()) / 2);
		g2D.translate(-(r.startX() + r.endX()) / 2, -(r.startY() + r.endY()) / 2);
	}
	
	private Ribbon getRibbon(PresentationNode startNode, PresentationNode endNode) {
		
//		// Get Center Points of Nodes
		int startCenterX = startNode.getX() + (startNode.getWidth() / 2);
		int startCenterY = startNode.getY() + (startNode.getHeight() / 2);
//		int endCenterX = endNode.getX() + (endNode.getWidth() / 2);
		int endCenterY = endNode.getY() + (endNode.getHeight() / 2);
		
		int endCenterX = endNode.getX() + endNode.getWidth();
//		int endCenterY = endNode.getY();
		
		return new Ribbon(startCenterX, startCenterY, endCenterX, endCenterY);
	}

}
