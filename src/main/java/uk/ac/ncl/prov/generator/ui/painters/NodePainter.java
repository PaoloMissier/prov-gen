package uk.ac.ncl.prov.generator.ui.painters;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import uk.ac.ncl.prov.generator.model.PresentationNode;

public abstract class NodePainter {

	public NodePainter() {}

	public void paint(Graphics2D g2D, PresentationNode n) {

		g2D.translate(n.getX(), n.getY());

		drawShadow(g2D, n);
		drawElement(g2D, n);

		g2D.translate(-n.getX(), -n.getY());
	}

	public void drawElement(Graphics2D g2D, PresentationNode n) {

		GradientPaint gp = new GradientPaint(0, 0, new Color(207, 219, 232), 0,
				n.getHeight(), new Color(165, 188, 213));

		g2D.setPaint(gp);

		g2D.fillRoundRect(0, 0, n.getWidth(), n.getHeight(), 16, 16);
	}
	
	public void drawFocusedOutline(Graphics2D g2D, PresentationNode n) {
	
		/* If this Element is focused, outline it */
		if (n.isFocus()) {

			g2D.setPaint(Color.BLACK);
			g2D.drawRoundRect(0, 0, n.getWidth(), n.getHeight(), 16, 16);
		}
	}

	public void drawShadow(Graphics2D g2D, PresentationNode n) {

		/* Draw a lighter alpha as we get further away from Element */
		final int shadowAlphas[] = { 20, 15, 10, 5, 5 };
		for (int i = 0; i < 5; i++) {
			g2D.setColor(new Color(0, 0, 0, shadowAlphas[i]));
			g2D.fillRoundRect(2 - i, 4 - i, n.getWidth() + (2 * i) - 1, n
					.getHeight()
					+ (2 * i) - 1, 24, 24);
		}
	}
	
	public void drawText(Graphics2D g, PresentationNode n) {

		FontMetrics fm = g.getFontMetrics();
		int IdWidth = SwingUtilities.computeStringWidth(fm, n.getId());

		g.setPaint(Color.BLACK);
		
		/* Center the text in the Element */
		g.drawString(n.getId(), 0 + ((n.getWidth() / 2) - (IdWidth / 2)), 17);
		
		String generator = "[" + n.getMinCardinality() + "," + n.getMaxCardinality() + "]";
		int generatorWidth = SwingUtilities.computeStringWidth(fm, generator);
		
		g.drawString(generator, 0 + ((n.getWidth() / 2) - (generatorWidth / 2)), 35);
	}
}
