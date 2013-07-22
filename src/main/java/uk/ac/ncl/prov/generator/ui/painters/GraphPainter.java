package uk.ac.ncl.prov.generator.ui.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Iterator;

import org.openprovenance.prov.java.component1.Activity;
import org.openprovenance.prov.java.component1.Entity;
import org.openprovenance.prov.java.component3.Agent;

import uk.ac.ncl.prov.generator.model.PresentationGraph;
import uk.ac.ncl.prov.generator.model.PresentationNode;
import uk.ac.ncl.prov.generator.model.PresentationRelation;
import uk.ac.ncl.prov.generator.model.Ribbon;

public class GraphPainter {

	public void paint(Graphics2D g2D, PresentationGraph presentationGraph) {

		/* Helps keep lines from pixelating unpleasantly */
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		paintEdges(g2D, presentationGraph.relations());
		paintElements(g2D, presentationGraph.nodes());
		paintRibbon(g2D, presentationGraph.getRibbon());
	}

	public void paintEdges(Graphics2D g2D, Iterator<PresentationRelation> relations) {

		RelationPainter relationPainter = new RelationPainter();
		PresentationRelation e;

		while (relations.hasNext()) {

			e = relations.next();
			relationPainter.paint(g2D, e);
		}
	}

	public void paintElements(Graphics2D g2D, Iterator<PresentationNode> elements) {

		NodePainter elementPainter = null;
		PresentationNode e;

		while (elements.hasNext()) {

			e = elements.next();

			if (e.getElement() instanceof Activity) {

				elementPainter = new ActivityPainter();
			} else if (e.getElement() instanceof Agent) {

				elementPainter = new AgentPainter();
			} else if (e.getElement() instanceof Entity) {

				elementPainter = new EntityPainter();
			}

			if (elementPainter != null)
				elementPainter.paint(g2D, e);
		}
	}

	public void paintRibbon(Graphics2D g2D, Ribbon ribbon) {

		g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
		g2D.setColor(new Color(0, 0, 0));

		if (ribbon != null) {

			g2D.drawLine(ribbon.startX(), ribbon.startY(), ribbon.endX(), ribbon.endY());
		}

	}

}
