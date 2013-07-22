package uk.ac.ncl.prov.generator.ui.painters;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

import uk.ac.ncl.prov.generator.model.PresentationNode;

public class AgentPainter extends NodePainter {

	public AgentPainter() {}

	public void paint(Graphics2D g2D, PresentationNode e) {

		g2D.translate(e.getX(), e.getY());

		drawShadow(g2D, e);
		drawNode(g2D, e);
		drawText(g2D, e);

		g2D.translate(-e.getX(), -e.getY());
	}

	public void drawNode(Graphics2D g2D, PresentationNode e) {

		GradientPaint gp = new GradientPaint(0, 0, new Color(190,227,209), 0,
				e.getHeight(), new Color(118, 197, 158));

		g2D.setPaint(gp);

		g2D.fillRoundRect(0, 0, e.getWidth(), e.getHeight(), 16, 16);

		drawFocusedOutline(g2D, e);
	}
}
