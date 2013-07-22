package uk.ac.ncl.prov.gen.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.openprovenance.prov.java.Construct;
import org.openprovenance.prov.java.Element;
import org.openprovenance.prov.java.HasLocation;
import org.openprovenance.prov.java.HasRole;
import org.openprovenance.prov.java.NonAlternateRelation;
import org.openprovenance.prov.java.Relation;
import org.openprovenance.prov.java.component1.Activity;
import org.openprovenance.prov.java.component1.Entity;
import org.openprovenance.prov.java.component1.Used;
import org.openprovenance.prov.java.component1.WasEndedBy;
import org.openprovenance.prov.java.component1.WasGeneratedBy;
import org.openprovenance.prov.java.component1.WasInformedBy;
import org.openprovenance.prov.java.component1.WasInvalidatedBy;
import org.openprovenance.prov.java.component1.WasStartedBy;
import org.openprovenance.prov.java.component2.WasDerivedFrom;
import org.openprovenance.prov.java.component3.ActedOnBehalfOf;
import org.openprovenance.prov.java.component3.Agent;
import org.openprovenance.prov.java.component3.WasAssociatedWith;
import org.openprovenance.prov.java.component3.WasAttributedTo;
import org.openprovenance.prov.java.component3.WasInfluencedBy;
import org.openprovenance.prov.java.component4.Bundle;
import org.openprovenance.prov.java.component4.Records;
import org.openprovenance.prov.java.component5.AlternateOf;
import org.openprovenance.prov.java.component5.MentionOf;
import org.openprovenance.prov.java.component5.SpecializationOf;

public class Generator {

	private Bundle bundle;

	// Used as a lookup from old elements to new super elements
	private Map<String, SuperElement> superElements = new HashMap<String, SuperElement>();

	public Generator(Bundle bundle) {
		this.bundle = bundle;
	}

	public void seed(boolean reseed) {

		// Find root elements
		List<Element> rootElements = findRootElements(bundle);

		// From root elements, perform a Breadth-First Traversal, seeding each
		// record
		Queue<Element> queue = new LinkedList<Element>();
		Set<Element> visited = new HashSet<Element>();

		visited.addAll(rootElements);
		queue.addAll(rootElements);

		while (!queue.isEmpty()) {

			Element currentElement = queue.poll();

			if (reseed || currentElement.getValue("gen:concreteCardinality") == null) {
				currentElement = addConcreteCardinality(currentElement);
			}

			for (Relation relation : currentElement.getRelations()) {

				// Annoyingly, PROV-N does not support attributes on alternate
				// relations.
				// WHY?
				if ((reseed || currentElement.getValue("gen:concreteCardinality") == null) && relation instanceof NonAlternateRelation) {
					relation = addConcreteSaturation((NonAlternateRelation) relation);
				}

				for (Element neighbour : relation.getEndElements()) {

					if (!visited.contains(neighbour)) {
						visited.add(neighbour);
						queue.add(neighbour);
					}
				}
			}
		}
		
		System.out.println(bundle);
	}

	private List<Element> findRootElements(Bundle bundle) {

		List<Element> elements = getAllElements(bundle);
		List<Relation> relations = getAllRelations(bundle);

		// Go through all the elements and remove any that have incoming edges
		// because they are not root (total ordered as latest).
		for (Relation relation : relations) {

			for (Element element : relation.getEndElements()) {

				elements.remove(element);
			}
		}

		if (elements.size() == 0) {

			elements = getCyclicalRootElements();
		}

		return elements;
	}

	// If we have a cycle it can be slightly more complicated to find root
	// element
	// However, only some relations create cycles so we can find those.
	private List<Element> getCyclicalRootElements() {

		List<Element> elements = getAllElements(bundle);
		List<Relation> relations = getAllRelations(bundle);

		for (Relation relation : relations) {

			for (Element element : relation.getEndElements()) {

				if (!(relation instanceof WasStartedBy)) {
					elements.remove(element);
				}
			}
		}

		return elements;
	}

	// Recurse through bundles to get all the elements
	private List<Element> getAllElements(Bundle bundle) {

		List<Element> bundleElements  = bundle.getRecords().getElements();
		List<Element> nonLiveElements = new ArrayList<Element>();

		nonLiveElements.addAll(bundleElements);

		for (Bundle b : bundle.getBundles()) {
			nonLiveElements.addAll(getAllElements(b));
		}

		return nonLiveElements;
	}

	// Recurse through bundles to get all the relations
	private List<Relation> getAllRelations(Bundle bundle) {

		List<Relation> bundleRelations = bundle.getRecords().getRelations();
		List<Relation> nonLiveRelations = new ArrayList<Relation>();

		nonLiveRelations.addAll(bundleRelations);

		for (Bundle b : bundle.getBundles()) {
			nonLiveRelations.addAll(getAllRelations(b));
		}

		return nonLiveRelations;
	}

	private Element addConcreteCardinality(Element element) {

		// Generate a concrete cardinality for nodes
		int minCardinality = 1;
		int maxCardinality = 1;

		if (element.getValue("gen:minCardinality") != null
				&& element.getValue("gen:maxCardinality") != null) {
			minCardinality = Integer.parseInt(element
					.getValue("gen:minCardinality"));
			maxCardinality = Integer.parseInt(element
					.getValue("gen:maxCardinality"));
		}

		int concreteCardinality = generateRandomBetween(minCardinality,
				maxCardinality);

		element.addValue("gen:concreteCardinality",
				String.valueOf(concreteCardinality));

		return element;
	}

	private Relation addConcreteSaturation(NonAlternateRelation relation) {

		// Generate a concrete cardinality for nodes
		double minSaturation = 1.0;
		double maxSaturation = 1.0;

		if (relation.getValue("gen:minSaturation") != null
				&& relation.getValue("gen:maxSaturation") != null) {
			minSaturation = Double.parseDouble(relation
					.getValue("gen:minSaturation"));
			maxSaturation = Double.parseDouble(relation
					.getValue("gen:maxSaturation"));
		}

		double concreteSaturation = generateRandomBetween(minSaturation,
				maxSaturation);

		relation.addValue("gen:concreteSaturation",
				String.valueOf(concreteSaturation));

		return relation;
	}

	private int generateRandomBetween(int min, int max) {

		Random random = new Random();
		int concrete = random.nextInt(max - min + 1) + min;

		return concrete;
	}

	private double generateRandomBetween(double min, double max) {

		double concrete = 0.0;

		while (concrete < min || concrete > max) {
			concrete = Math.random();
			concrete += 0.1;
			concrete = (double) Math.round(concrete * 10) / 10;
		}

		return concrete;
	}

	public Bundle expand() {

		seed(false);
		Bundle newBundle = new Bundle();
		newBundle = expandElements(newBundle);
		newBundle = expandRelations(newBundle);
		newBundle = removeSuperElements(newBundle);
		
		int n=0, e=0;
		Records rec = newBundle.getRecords();
			
		for (Element el : rec.getElements()) { if (el != null) n++; }
		for (Relation rel : rec.getRelations()) { if (rel != null) e++; }

		System.out.println("** before orphan removal: "+n+" nodes and "+e+" edges");
		
		newBundle = removeOrphans(newBundle);

		return newBundle;
	}

	// Traverse graph once and expand elements into super elements
	private Bundle expandElements(Bundle newBundle) {

		// Find root elements
		List<Element> rootElements = findRootElements(bundle);

		// From root elements, perform a Breadth-First Traversal, seeding each
		// record
		Queue<Element> queue = new LinkedList<Element>();
		Set<Element> visited = new HashSet<Element>();

		visited.addAll(rootElements);
		queue.addAll(rootElements);

		while (!queue.isEmpty()) {

			Element currentElement = queue.poll();

			SuperElement superCurrentElement = new SuperElement(
					expandElement(currentElement));
			superCurrentElement.setId(currentElement.getId());
			// Add to lookup map for relation expansion later
			superElements.put(currentElement.getId(), superCurrentElement);
			newBundle.addRecord(superCurrentElement);

			for (Relation relation : currentElement.getRelations()) {

				for (Element neighbour : relation.getEndElements()) {

					if (!visited.contains(neighbour)) {
						visited.add(neighbour);
						queue.add(neighbour);
					}
				}
			}
		}

		return newBundle;
	}

	// Traverse the graph again and transfer relations from old graph to new
	private Bundle expandRelations(Bundle newBundle) {

		// Find root elements
		List<Element> rootElements = findRootElements(bundle);

		// From root elements, perform a Breadth-First Traversal, seeding each
		// record
		Queue<Element> queue = new LinkedList<Element>();
		Set<Element> visited = new HashSet<Element>();

		visited.addAll(rootElements);
		queue.addAll(rootElements);

		while (!queue.isEmpty()) {

			Element currentElement = queue.poll();

			for (Relation relation : currentElement.getRelations()) {

				newBundle = expandAndAttachRelations(newBundle, relation);

				for (Element neighbour : relation.getEndElements()) {

					if (!visited.contains(neighbour)) {
						visited.add(neighbour);
						queue.add(neighbour);
					}
				}
			}
		}
		
		return newBundle;
	}

	// Expand a single element into a List of identical (bar ids) elements
	private List<Element> expandElement(Element element) {

		List<Element> elements = new ArrayList<Element>();

		for (int i = 0; i < Integer.valueOf(element
				.getValue("gen:concreteCardinality")); i++) {

			elements.add(duplicateElement(element, i));
		}

		return elements;
	}

	private Element duplicateElement(Element element, int id) {

		if (element instanceof Entity) {
			return duplicateEntity((Entity) element, id);
		} else if (element instanceof Activity) {
			return duplicateActivity((Activity) element, id);
		} else if (element instanceof Agent) {
			return duplicateAgent((Agent) element, id);
		}

		return null;
	}

	private Entity duplicateEntity(Entity entity, int id) {

		Entity newEntity = new Entity();
		
		newEntity.setId(entity.getId() + id);
		
		newEntity = (Entity) duplicateConstructAttributes(entity, newEntity);
		newEntity = (Entity) duplicateLocationAttributes(entity, newEntity);

		return newEntity;
	}

	private Activity duplicateActivity(Activity activity, int id) {

		Activity newActivity = new Activity();
		newActivity.setId(activity.getId() + id);
		newActivity.setStartTime(activity.getStartTime());
		newActivity.setEndTime(activity.getEndTime());
		newActivity = (Activity) duplicateConstructAttributes(activity, newActivity);
		newActivity = (Activity) duplicateLocationAttributes(activity, newActivity);

		return newActivity;
	}

	private Agent duplicateAgent(Agent agent, int id) {

		Agent newAgent = new Agent();
		newAgent.setId(agent.getId() + id);
		newAgent = (Agent) duplicateConstructAttributes(agent, newAgent);
		newAgent = (Agent) duplicateLocationAttributes(agent, newAgent);

		return newAgent;
	}
	
	private Construct duplicateConstructAttributes(Construct construct, Construct newConstruct) {
		
		for (String type : construct.getTypes()) {
			newConstruct.addType(type);
		}
		
		for (String label : construct.getLabels()) {
			newConstruct.addLabel(label);
		}

		for (Map.Entry<String, String> entry : construct.getValues().entrySet()) {
			if (!entry.getKey().equals("gen:concreteCardinality") && !entry.getKey().equals("gen:concreteSaturation")) {
				newConstruct.addValue(entry.getKey(), entry.getValue());
			}
		}
		
		return newConstruct;
	}
	
	private Bundle expandAndAttachRelations(Bundle newBundle, Relation relation) {
		
		final String WGBY_REL = "WasGeneratedBy";
		
		int numberOfRelations = 1;
		
		if (relation.getClass().getSimpleName().equals(WGBY_REL)) {
			System.out.println("forcing 1 instance of "+relation.getClass().getSimpleName());
		}  else {
			numberOfRelations = calculateNumberOfRelations(relation);						
		}
		
		newBundle = duplicateRelation(newBundle, relation, numberOfRelations);
		
		return newBundle;
	}

	private int calculateNumberOfRelations(Relation relation) {

		// TODO: Relations with multiple end points
		// Probably choose end point with smallest cardinality
		Element startElement = relation.getStartElement();
		List<Element> endElements = relation.getEndElements();
		int startCardinality = Integer.parseInt(startElement
				.getValue("gen:concreteCardinality"));
		
		int endCardinality;
		if (endElements.size() > 0) {
			endCardinality = Integer.parseInt(endElements.get(0).getValue(
				"gen:concreteCardinality"));
		} else {
			endCardinality = 1;
		}

		int numberOfEdges = (int) Math.min(startCardinality, endCardinality);
		
		if (relation instanceof NonAlternateRelation) {
			double maxSaturation = startCardinality * endCardinality;
			numberOfEdges = (int) Math.ceil(maxSaturation * Double.valueOf(((NonAlternateRelation) relation).getValue("gen:concreteSaturation")));			
		} 
		
		return numberOfEdges;
	}

	private Bundle duplicateRelation(Bundle newBundle, Relation relation, int numberOfRelations) {
		
		// Find start and end points of relation
		Element startElement = relation.getStartElement();
		SuperElement startSuperElement = superElements.get(startElement.getId());

		Element endElement = null;
		SuperElement endSuperElement = null;
		
		if (relation.getEndElements().size() > 0) {
			endElement = relation.getEndElements().get(0);
			// Find the start and end points in the new bundle
			endSuperElement = superElements.get(endElement.getId());
		}


		if (relation instanceof Used) {
			newBundle = duplicateUsed(newBundle, (Used) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasGeneratedBy) {
			newBundle = duplicateWasGeneratedBy(newBundle, (WasGeneratedBy) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasStartedBy) {
			newBundle = duplicateWasStartedBy(newBundle, (WasStartedBy) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasEndedBy) {
			newBundle = duplicateWasEndedBy(newBundle, (WasEndedBy) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasInformedBy) {
			newBundle = duplicateWasInformedBy(newBundle, (WasInformedBy) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasInvalidatedBy) {
			newBundle = duplicateWasInvalidatedBy(newBundle, (WasInvalidatedBy) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasDerivedFrom) {
			newBundle = duplicateWasDerivedFrom(newBundle, (WasDerivedFrom) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof ActedOnBehalfOf) {
			newBundle = duplicateActedOnBehalfOf(newBundle, (ActedOnBehalfOf) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasAttributedTo) {
			newBundle = duplicateWasAttributedTo(newBundle, (WasAttributedTo) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasAssociatedWith) {
			newBundle = duplicateWasAssociatedWith(newBundle, (WasAssociatedWith) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasInfluencedBy) {
			newBundle = duplicateWasInfluencedBy(newBundle, (WasInfluencedBy) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof AlternateOf) {
			newBundle = duplicateAlternateOf(newBundle, (AlternateOf) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof SpecializationOf) {
			newBundle = duplicateSpecializationOf(newBundle, (SpecializationOf) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else if (relation instanceof WasAssociatedWith) {
			newBundle = duplicateMentionOf(newBundle, (MentionOf) relation, startSuperElement, endSuperElement, numberOfRelations);
		} else {
			return null;
		}
		
		return newBundle;
	}
	
	// Duplicating Relation Methods. Could do with a lot less redundancy but not sure how
	
	// Component 1

	private Bundle duplicateUsed(Bundle newBundle, Used used, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				Used newUsed = new Used();
				
				// PM edited to avoid "nullX" Ids
				if (used.getId() != null)	newUsed.setId(used.getId() + numberAttached);	
				else newUsed.setId(null);

				newUsed.setTime(used.getTime());
				
				newUsed.setActivity((Activity) element);
				newUsed.setEntity((Entity) endElement.getElement(i));
				newUsed = (Used) duplicateConstructAttributes(used, newUsed);
				newUsed = (Used) duplicateRoleAttributes(used, newUsed);
				element.addRelation(newUsed);
				newBundle.addRecord(newUsed);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasGeneratedBy(Bundle newBundle, WasGeneratedBy wasGeneratedBy, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasGeneratedBy newWasGeneratedBy = new WasGeneratedBy();
				
				// PM edited to avoid "nullX" Ids
				if (newWasGeneratedBy.getId() != null)	newWasGeneratedBy.setId(wasGeneratedBy.getId() + numberAttached);	
				else newWasGeneratedBy.setId(null);

				newWasGeneratedBy.setTime(wasGeneratedBy.getTime());

				newWasGeneratedBy.setEntity((Entity) element);
				newWasGeneratedBy.setActivity((Activity) endElement.getElement(i));
				newWasGeneratedBy = (WasGeneratedBy) duplicateConstructAttributes(wasGeneratedBy, newWasGeneratedBy);
				newWasGeneratedBy = (WasGeneratedBy) duplicateRoleAttributes(wasGeneratedBy, newWasGeneratedBy);
				element.addRelation(newWasGeneratedBy);
				newBundle.addRecord(newWasGeneratedBy);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasStartedBy(Bundle newBundle, WasStartedBy wasStartedBy, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasStartedBy newWasStartedBy = new WasStartedBy();
				newWasStartedBy.setId(wasStartedBy.getId() + numberAttached);
				newWasStartedBy.setActivity((Activity) element);
				newWasStartedBy.setTrigger((Entity) endElement.getElement(i));
				newWasStartedBy.setTime(wasStartedBy.getTime());
				newWasStartedBy = (WasStartedBy) duplicateConstructAttributes(wasStartedBy, newWasStartedBy);
				element.addRelation(newWasStartedBy);
				newBundle.addRecord(newWasStartedBy);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasEndedBy(Bundle newBundle, WasEndedBy wasEndedBy, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasEndedBy newWasEndedBy = new WasEndedBy();
				newWasEndedBy.setId(wasEndedBy.getId() + numberAttached);
				newWasEndedBy.setActivity((Activity) element);
				newWasEndedBy.setTrigger((Entity) endElement.getElement(i));
				newWasEndedBy.setTime(wasEndedBy.getTime());
				newWasEndedBy = (WasEndedBy) duplicateConstructAttributes(wasEndedBy, newWasEndedBy);
				element.addRelation(newWasEndedBy);
				newBundle.addRecord(newWasEndedBy);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasInformedBy(Bundle newBundle, WasInformedBy wasInformedBy, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasInformedBy newWasInformedBy = new WasInformedBy();
				newWasInformedBy.setId(wasInformedBy.getId() + numberAttached);
				newWasInformedBy.setEffect((Activity) element);
				newWasInformedBy.setCause((Activity) endElement.getElement(i));
				newWasInformedBy = (WasInformedBy) duplicateConstructAttributes(wasInformedBy, newWasInformedBy);
				element.addRelation(newWasInformedBy);
				newBundle.addRecord(newWasInformedBy);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasInvalidatedBy(Bundle newBundle, WasInvalidatedBy wasInvalidatedBy, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasInvalidatedBy newWasInvalidatedBy = new WasInvalidatedBy();
				newWasInvalidatedBy.setId(wasInvalidatedBy.getId() + numberAttached);
				newWasInvalidatedBy.setEntity((Entity) element);
				newWasInvalidatedBy.setActivity((Activity) endElement.getElement(i));
				newWasInvalidatedBy.setTime(wasInvalidatedBy.getTime());
				newWasInvalidatedBy = (WasInvalidatedBy) duplicateConstructAttributes(wasInvalidatedBy, newWasInvalidatedBy);
				element.addRelation(newWasInvalidatedBy);
				newBundle.addRecord(newWasInvalidatedBy);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	// Component 2
	
	private Bundle duplicateWasDerivedFrom(Bundle newBundle, WasDerivedFrom wasDerivedFrom, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasDerivedFrom newWasDerivedFrom = new WasDerivedFrom();
				newWasDerivedFrom.setId(wasDerivedFrom.getId() + numberAttached);
				newWasDerivedFrom.setGeneratedEntity((Entity) element);
				newWasDerivedFrom.setUsedEntity((Entity) endElement.getElement(i));
				newWasDerivedFrom = (WasDerivedFrom) duplicateConstructAttributes(wasDerivedFrom, newWasDerivedFrom);
				element.addRelation(newWasDerivedFrom);
				newBundle.addRecord(newWasDerivedFrom);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	// Component 3
	
	private Bundle duplicateWasAttributedTo(Bundle newBundle, WasAttributedTo wasAttributedTo, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasAttributedTo newWasAttributedTo = new WasAttributedTo();
				newWasAttributedTo.setId(wasAttributedTo.getId() + numberAttached);
				newWasAttributedTo.setEntity((Entity) element);
				newWasAttributedTo.setAgent((Agent) endElement.getElement(i));
				newWasAttributedTo = (WasAttributedTo) duplicateConstructAttributes(wasAttributedTo, newWasAttributedTo);
				element.addRelation(newWasAttributedTo);
				newBundle.addRecord(newWasAttributedTo);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasAssociatedWith(Bundle newBundle, WasAssociatedWith wasAssociatedWith, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasAssociatedWith newWasAssociatedWith = new WasAssociatedWith();
				newWasAssociatedWith.setId(wasAssociatedWith.getId() + numberAttached);
				newWasAssociatedWith.setActivity((Activity) element);
				newWasAssociatedWith.setAgent((Agent) endElement.getElement(i));
				newWasAssociatedWith = (WasAssociatedWith) duplicateConstructAttributes(wasAssociatedWith, newWasAssociatedWith);
				newWasAssociatedWith = (WasAssociatedWith) duplicateRoleAttributes(wasAssociatedWith, newWasAssociatedWith);
				element.addRelation(newWasAssociatedWith);
				newBundle.addRecord(newWasAssociatedWith);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateWasInfluencedBy(Bundle newBundle, WasInfluencedBy wasInfluencedBy, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				WasInfluencedBy newWasInfluencedBy = new WasInfluencedBy();
				newWasInfluencedBy.setId(wasInfluencedBy.getId() + numberAttached);
				newWasInfluencedBy.setInfluencee((Agent) element);
				newWasInfluencedBy.setInfluencer((Agent) endElement.getElement(i));
				newWasInfluencedBy = (WasInfluencedBy) duplicateConstructAttributes(wasInfluencedBy, newWasInfluencedBy);
				element.addRelation(newWasInfluencedBy);
				newBundle.addRecord(newWasInfluencedBy);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateActedOnBehalfOf(Bundle newBundle, ActedOnBehalfOf actedOnBehalfOf, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				ActedOnBehalfOf newActedOnBehalfOf = new ActedOnBehalfOf();
				newActedOnBehalfOf.setId(actedOnBehalfOf.getId() + numberAttached);
				newActedOnBehalfOf.setSubordinate((Agent) element);
				newActedOnBehalfOf.setResponsible((Agent) endElement.getElement(i));
				newActedOnBehalfOf = (ActedOnBehalfOf) duplicateConstructAttributes(actedOnBehalfOf, newActedOnBehalfOf);
				element.addRelation(newActedOnBehalfOf);
				newBundle.addRecord(newActedOnBehalfOf);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	// Component 5
	
	private Bundle duplicateSpecializationOf(Bundle newBundle, SpecializationOf specializationOf, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				SpecializationOf newSpecializationOf = new SpecializationOf();
				newSpecializationOf.setSpecializedEntity((Entity) element);
				newSpecializationOf.setGeneralEntity((Entity) endElement.getElement(i));
				element.addRelation(newSpecializationOf);
				newBundle.addRecord(newSpecializationOf);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateAlternateOf(Bundle newBundle, AlternateOf alternateOf, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				AlternateOf newAlternateOf = new AlternateOf();
				newAlternateOf.setAlternate1((Entity) element);
				newAlternateOf.setAlternate2((Entity) endElement.getElement(i));
				element.addRelation(newAlternateOf);
				newBundle.addRecord(newAlternateOf);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle duplicateMentionOf(Bundle newBundle, MentionOf mentionOf, SuperElement startElement, SuperElement endElement, int numberOfRelations) {

		int startElementSize = startElement.getElements().size();
		int endElementSize = endElement == null ? 0 : endElement.getElements().size();
		int numberAttached = 0;
		int lhs = 0;
		
		while(numberAttached < numberOfRelations && lhs < startElementSize) {
			
			// Get an element and connect it to all elements on the other side first
			Element element = startElement.getElement(lhs);
			
			for (int i = 0; i < endElementSize; i++) {
				
				MentionOf newMentionOf = new MentionOf();
				newMentionOf.setSpecializedEntity((Entity) element);
				newMentionOf.setGeneralEntity((Entity) endElement.getElement(i));
				element.addRelation(newMentionOf);
				newBundle.addRecord(newMentionOf);
				
				numberAttached++;
			}
			
			lhs++;
		}
		
		return newBundle;
	}
	
	private Bundle removeSuperElements(Bundle newBundle) {
		
		for (Map.Entry<String, SuperElement> superElement : superElements.entrySet()) {
			
			for (Element element : superElement.getValue().getElements()) {
				newBundle.addRecord(element);
			}
			
			newBundle.removeRecord(superElement.getValue());
		}
		
		return newBundle;
	}
	
	private HasLocation duplicateLocationAttributes(HasLocation record, HasLocation newRecord) {
		
		for (String location : record.getLocations()) {
			newRecord.addLocation(location);
		}
		
		return newRecord;
	}
	
	private HasRole duplicateRoleAttributes(HasRole record, HasRole newRecord) {
		
		for (String role : record.getRoles()) {
			newRecord.addRole(role);
		}
		
		return newRecord;
	}
	
	private Bundle removeOrphans(Bundle bundle) {
		
		List<Element> allElements = getAllElements(bundle);
		List<Element> rootElements = findRootElements(bundle);
		
		for (Element element : allElements) {
			
			if (element.getRelations().size() == 0 && rootElements.contains(element)) {
				bundle.removeRecord(element);
			}
		}
		
		return bundle;
	}
}
