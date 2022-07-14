/**
 * 
 */
package org.grits.toolbox.entry.sample.ontologymanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.MeasurementUnit;
import org.grits.toolbox.entry.sample.model.Namespace;
import org.grits.toolbox.entry.sample.model.Template;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.OntologyException;
import com.hp.hpl.jena.rdf.model.EmptyListException;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceRequiredException;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.shared.AlreadyExistsException;
import com.hp.hpl.jena.shared.InvalidPropertyURIException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 *
 */
public class SampleOntologyManager
{
	private Logger logger = Logger.getLogger(SampleOntologyManager.class);

	public static final String baseURI = "http://www.grits-toolbox.org/ontology/sample#";

	public static final String CATEGORY_SAMPLE_INFO_CLASS_URI = baseURI + "category_sample";
	public static final String CATEGORY_TRACKING_INFO_CLASS_URI = baseURI + "category_sample_tracking";
	public static final String CATEGORY_AMOUNT_CLASS_URI = baseURI + "category_amount";
	public static final String CATEGORY_PURITY_QC_CLASS_URI = baseURI + "category_qc";

	public static final String DESCRIPTOR_CLASS_URI = baseURI + "descriptor";
	public static final String DESCRIPTOR_GROUP_CLASS_URI = baseURI + "descriptor_group";
	public static final String TEMPLATE_CLASS_URI = baseURI + "template";
	public static final String UNIT_CLASS_URI = baseURI + "unit";

	public static final String CATEGORY_CLASS_URI = baseURI + "category";
	public static final String TEMPLATE_CONTEXT_CLASS_URI = baseURI + "template_context";
	public static final String MEASUREMENT_UNIT_CLASS_URI = baseURI + "unit";
	public static final String NAMESPACE_CLASS_URI = baseURI + "namespace";
	public static final String GUIDELINE_CLASS_URI = baseURI + "StandardGuideline";

	protected OntModel standardOntModel = null;

	protected SampleOntologyManager(OntModel standardOntModel)
	{
		this.standardOntModel = standardOntModel;
	}

	protected OntModel getStandardOntModel()
	{
		return standardOntModel;
	}

	protected List<Individual> getIndividualsForClass(OntModel ontModel, String classUri)
	{
		// does not accept null or empty parameters
		if(ontModel == null || (classUri == null || classUri.isEmpty()))
			return null;

		OntClass ontClass = ontModel.getOntClass(classUri);
		if(ontClass == null)
			return null;

		return ontModel.listIndividuals(ontClass).toList();
	}

	protected Descriptor getDescriptor(RDFNode descriptorNode)
			throws ResourceRequiredException, NotFoundException, DatatypeFormatException
	{
		if (descriptorNode == null)
			return null;

		if(!descriptorNode.isResource())
			throw new ResourceRequiredException(descriptorNode);

		String resourceUri = descriptorNode.asResource().getURI();

		OntModel nodeModel = (OntModel) descriptorNode.getModel();

		Individual descriptorIndiv = null;
		// its model should contain a statement for its
		// class for it to be a descriptor defined in this model
		// or else it could be a simple node used in some statement(s)
		if(nodeModel.isInBaseModel(new StatementImpl(descriptorNode.asResource(),
				RDF.type, nodeModel.getOntClass(DESCRIPTOR_CLASS_URI))))
		{
			descriptorIndiv = nodeModel.getIndividual(resourceUri);
		}
		else if(nodeModel != standardOntModel)	// if definition not found 
			// and not looked into standard ontology then look there
		{
			descriptorIndiv = standardOntModel.getIndividual(resourceUri);
		}

		if(descriptorIndiv == null)
			throw new NotFoundException(resourceUri);

		if(!DESCRIPTOR_CLASS_URI.equals(descriptorIndiv.getOntClass().getURI()))
			throw new DatatypeFormatException("\"" + resourceUri
					+ "\" is not a descriptor but is \""
					+ descriptorIndiv.getOntClass().getURI() + "\"");

		return getDescriptor(descriptorIndiv);
	}

	protected DescriptorGroup getDescriptorGroup(RDFNode descriptorGroupNode)
			throws ResourceRequiredException, NotFoundException, DatatypeFormatException
	{
		if (descriptorGroupNode == null)
			return null;

		if(!descriptorGroupNode.isResource())
			throw new ResourceRequiredException(descriptorGroupNode);

		String resourceUri = descriptorGroupNode.asResource().getURI();

		OntModel nodeModel = (OntModel) descriptorGroupNode.getModel();

		Individual descriptorGroupIndiv = null;
		// its model should contain a statement for its
		// class for it to be a descriptor group defined in this model
		// or else it could be a simple node used in some statement(s)
		if(nodeModel.isInBaseModel(new StatementImpl(descriptorGroupNode.asResource(),
				RDF.type, nodeModel.getOntClass(DESCRIPTOR_GROUP_CLASS_URI))))
		{
			descriptorGroupIndiv = nodeModel.getIndividual(resourceUri);
		}
		else if(nodeModel != standardOntModel)	// if definition not found 
			// and not looked into standard ontology then look there
		{
			descriptorGroupIndiv = standardOntModel.getIndividual(resourceUri);
		}

		if(descriptorGroupIndiv == null)
			throw new NotFoundException(resourceUri);

		if(!DESCRIPTOR_GROUP_CLASS_URI.equals(descriptorGroupIndiv.getOntClass().getURI()))
			throw new DatatypeFormatException("\"" + resourceUri
					+ "\" is not a descriptor group but is \""
					+ descriptorGroupIndiv.getOntClass().getURI() + "\"");

		return getDescriptorGroup(descriptorGroupIndiv);
	}

	// method for backward compatiblity of cases where 
	// descriptors or group were in an ontology without its type
	private Resource getRDFType(RDFNode rdfNode) throws NotFoundException
	{
		logger.info("get class for this rdf node : " + rdfNode);
		if(rdfNode == null)
			return null;

		if(!rdfNode.isResource())
			throw new ResourceRequiredException(rdfNode);

		OntResource ontResource = (OntResource) rdfNode.asResource();
		Resource rdfType = ontResource.getRDFType();

		if(rdfType == null || rdfType.getURI() == null)
		{
			logger.error("resource type not found in its current ontology");
			OntResource standardOntResource = standardOntModel.getOntResource(ontResource.getURI());

			if(standardOntResource == null)
				throw new NotFoundException(ontResource.getURI());

			rdfType = standardOntResource.getRDFType();

			if(rdfType == null || rdfType.getURI() == null)
			{
				logger.fatal("resource type not found in its standard ontology either");
				throw new NotFoundException(ontResource.getURI());
			}

			logger.info("class type  is : " + rdfType.getURI());
			return standardOntResource.getRDFType();
		}

		logger.info("class type is : " + rdfType.getURI());
		return rdfType;
	}

	protected MeasurementUnit getMeasurementUnit(RDFNode rdfNode)
			throws ResourceRequiredException, NotFoundException, DatatypeFormatException
	{
		if (rdfNode == null)
			return null;

		if(!rdfNode.isResource())
			throw new ResourceRequiredException(rdfNode);

		String resourceUri = rdfNode.asResource().getURI();

		OntModel nodeModel = (OntModel) rdfNode.getModel();

		Individual unitIndiv = null;
		// its model should contain a statement for its
		// class for it to be a measurement unit defined in this model
		// or else it could be a simple node used in some statement(s)
		if(nodeModel.isInBaseModel(new StatementImpl(rdfNode.asResource(),
				RDF.type, nodeModel.getOntClass(MEASUREMENT_UNIT_CLASS_URI))))
		{
			unitIndiv = nodeModel.getIndividual(resourceUri);
		}
		else if(nodeModel != standardOntModel)	// if definition not found 
			// and not looked into standard ontology then look there
		{
			unitIndiv = standardOntModel.getIndividual(resourceUri);
		}

		if(unitIndiv == null)
			throw new NotFoundException(resourceUri);

		if(!MEASUREMENT_UNIT_CLASS_URI.equals(unitIndiv.getOntClass().getURI()))
			throw new DatatypeFormatException("\"" + resourceUri
					+ "\" is not a measurement unit but is \""
					+ unitIndiv.getOntClass().getURI() + "\"");

		return new MeasurementUnit(unitIndiv.getURI(), unitIndiv.getLabel(null));
	}

	// only looks in the standard ontology
	protected Namespace getNamespace(RDFNode rdfNode)
			throws NotFoundException, DatatypeFormatException
	{
		if (rdfNode == null)
			return null;

		String resourceUri = rdfNode.asResource().getURI();

		// handling of namespace resource
		// it can belong to multiple classes
		// only standard ontologies are concerned
		logger.info(resourceUri + " resource could have multiple rdf types");
		OntResource namespaceResource = standardOntModel.getOntResource(resourceUri);
		if(namespaceResource == null)
		{
			throw new NotFoundException(resourceUri);
		}

		if(!namespaceResource.hasRDFType(SampleOntologyManager.NAMESPACE_CLASS_URI))
			throw new DatatypeFormatException("\"" + resourceUri
					+ "\" is not a namespace but of type : "
					+ namespaceResource.getRDFType().getURI());

		String fileName = null;
		String idFileName = null;
		Property property = standardOntModel.getProperty((baseURI + "has_namespace_file"));
		RDFNode node = namespaceResource.getPropertyValue(property);
		if(node!= null && node.isLiteral())
		{
			fileName = node.asLiteral().getString();
			property = standardOntModel.getProperty((baseURI + "has_namespace_id_file"));
			node = namespaceResource.getPropertyValue(property);
			if(node!= null && node.isLiteral())
			{
				idFileName = node.asLiteral().getString();
			}
		}

		return new Namespace(namespaceResource.getURI(),
				namespaceResource.getLabel(null), fileName, idFileName);
	}

	protected DescriptorGroup getDescriptorGroup(Individual descriptorGroupIndiv) throws NotFoundException
	{
		logger.info("loading descriptor group");

		if(descriptorGroupIndiv == null)
			return null;

		String descriptorGroupUri = descriptorGroupIndiv.getURI();
		logger.info("uri : " + descriptorGroupUri);
		if(DESCRIPTOR_GROUP_CLASS_URI.equals(descriptorGroupIndiv.getOntClass().getURI()))
		{
			DescriptorGroup descriptorGroup = new DescriptorGroup();
			descriptorGroup.setUri(descriptorGroupUri);
			descriptorGroup.setLabel(descriptorGroupIndiv.getLabel(null));
			descriptorGroup.setDescription(descriptorGroupIndiv.getComment(null));
			Literal maxOccurrence = this.getLiteralValue(descriptorGroupIndiv, "has_abbundance");
			if(maxOccurrence != null)
				descriptorGroup.setMaxOccurrence(maxOccurrence.getInt());
			
			Literal position = getLiteralValue(descriptorGroupIndiv, "has_position");
			int pos = 0;
			if (position != null) {
				pos = position.getInt();
				descriptorGroup.setPosition(pos);
			}
			
			List<RDFNode> guidelinesFromContext = getAllObjectNodes(descriptorGroupIndiv, "guideline_info");
			if (guidelinesFromContext != null) {
				List<String> guidelines = new ArrayList<>();
				for(RDFNode guideline : guidelinesFromContext) {
					Individual guidelineIndiv =
							standardOntModel.getIndividual(guideline.asResource().getURI());
					if(guidelineIndiv != null) {
						guidelines.add(guidelineIndiv.getLabel(null));
					}
				}
				descriptorGroup.setGuidelineURIs(guidelines);
			}

			for(RDFNode category : getAllObjectNodes(descriptorGroupIndiv, "has_category"))
			{
				descriptorGroup.addCategory(category.asResource().getURI());
			}

			RDFNode descriptorNode = null;
			Individual groupContextIndiv = null;
			Descriptor descriptor = null;
			Literal mandatoryLiteral = null;
			for(RDFNode groupContextNode : getAllObjectNodes(
					descriptorGroupIndiv, "has_descriptor_group_context"))
			{
				groupContextIndiv = descriptorGroupIndiv.getOntModel()
						.getIndividual(groupContextNode.asResource().getURI());

				// group context exists in the same ontology
				if(groupContextIndiv == null)
					throw new NotFoundException("no descriptor group context for "
							+ groupContextNode.asResource().getURI());

				// group context should have a descriptor node
				descriptorNode = getObjectNode(groupContextIndiv, "has_descriptor");
				if(descriptorNode == null)
					throw new NotFoundException("no descriptor for " + groupContextIndiv.getURI());

				// get descriptor from descriptor node
				descriptor = getDescriptor(descriptorNode);

				// context over-writes the abundance of the descriptor
				maxOccurrence = getLiteralValue(groupContextIndiv, "has_abbundance");
				if(maxOccurrence != null)
					descriptor.setMaxOccurrence(maxOccurrence.getInt());
				else // indicating infinite abundance in context
					descriptor.setMaxOccurrence(null);

				mandatoryLiteral = getLiteralValue(groupContextIndiv, "is_mandatory");
				if(mandatoryLiteral.getBoolean())
					descriptorGroup.addMandatoryDescriptor(descriptor);
				else
					descriptorGroup.addOptionalDescriptor(descriptor);
			}
			return descriptorGroup;
		}
		else
		{
			String errorMessage  ="The given descriptor group uri \"" + descriptorGroupUri
					+ "\" is not of type Descriptor Group but is of type \""
					+ descriptorGroupIndiv.getOntClass().getURI() + "\"";
			logger.error(errorMessage);
			throw new DatatypeFormatException(errorMessage);
		}
	}

	protected Descriptor getDescriptor(Individual descriptorIndiv) throws DatatypeFormatException
	{
		logger.info("loading descriptor");

		if(descriptorIndiv == null)
			return null;

		String descriptorUri = descriptorIndiv.getURI();
		logger.info("uri : " + descriptorUri);
		if(DESCRIPTOR_CLASS_URI.equals(descriptorIndiv.getOntClass().getURI()))
		{
			Descriptor descriptor = new Descriptor();
			descriptor.setUri(descriptorUri);
			descriptor.setLabel(descriptorIndiv.getLabel(null));
			descriptor.setDescription(descriptorIndiv.getComment(null));

			Literal maxOccurrence = getLiteralValue(descriptorIndiv, "has_abbundance");
			if(maxOccurrence != null)
				descriptor.setMaxOccurrence(maxOccurrence.getInt());
			
			Literal position = getLiteralValue(descriptorIndiv, "has_position");
			int pos = 0;
			if (position != null) {
				pos = position.getInt();
				descriptor.setPosition(pos);
			}
			
			List<RDFNode> guidelinesFromContext = getAllObjectNodes(descriptorIndiv, "guideline_info");
			if (guidelinesFromContext != null) {
				List<String> guidelines = new ArrayList<>();
				for(RDFNode guideline : guidelinesFromContext) {
					Individual guidelineIndiv =
							standardOntModel.getIndividual(guideline.asResource().getURI());
					if(guidelineIndiv != null) {
						guidelines.add(guidelineIndiv.getLabel(null));
					}
				}
				descriptor.setGuidelineURIs(guidelines);
			}

			for(RDFNode namespaceNode : getAllObjectNodes(descriptorIndiv, "has_namespace"))
			{
				descriptor.addNamespace(getNamespace(namespaceNode));
			}

			RDFNode defaultUnitNode = getObjectNode(descriptorIndiv, "has_default_unit_of_measurement");
			if(defaultUnitNode != null)
				descriptor.setDefaultMeasurementUnit(getMeasurementUnit(defaultUnitNode).getUri());

			for(RDFNode unit : getAllObjectNodes(descriptorIndiv, "has_unit_of_measurement"))
			{
				descriptor.addValidUnit(getMeasurementUnit(unit));
			}

			for(RDFNode category : getAllObjectNodes(descriptorIndiv, "has_category"))
			{
				descriptor.addCategory(category.asResource().getURI());
			}

			return descriptor;
		}
		else
		{
			String errorMessage  ="The given descriptor uri \"" + descriptorUri
					+ "\" is not of type Descriptor but is of type \""
					+ descriptorIndiv.getOntClass().getURI() + "\"";
			logger.error(errorMessage);
			throw new DatatypeFormatException(errorMessage);
		}
	}

	protected Template getTemplate(Individual templateIndiv)
			throws DatatypeFormatException, EmptyListException, NotFoundException
	{
		logger.info("loading template");

		if(templateIndiv == null)
			return null;

		String templateUri = templateIndiv.getURI();
		logger.info("uri : " + templateUri);
		if(!TEMPLATE_CLASS_URI.equals(templateIndiv.getOntClass().getURI()))
		{
			String errorMessage  ="The given template uri \"" + templateUri
					+ "\" is not a Template but is of type \""
					+ templateIndiv.getOntClass().getURI() + "\"";
			logger.error(errorMessage);
			throw new DatatypeFormatException(errorMessage);
		}

		Template template =  new Template();
		template.setUri(templateUri);
		template.setLabel(templateIndiv.getLabel(null));
		template.setDescription(templateIndiv.getComment(null));

		Individual templateContextIndiv;
		List<RDFNode> categoryNodes;
		List<String> categoryUris;
		RDFNode descriptorNode;
		String descriptorNodeClassUri;
		boolean isManadatory;
		Literal maxOccurrenceLiteral;
		Integer maxOccurrence;
		DescriptorGroup descriptorGroup;
		Descriptor descriptor;
		for(RDFNode templateContextNode : getAllObjectNodes(templateIndiv, "has_template_context"))
		{
			// template context exists in the same ontology
			templateContextIndiv = templateIndiv.getOntModel()
					.getIndividual(templateContextNode.asResource().getURI());

			categoryNodes = getAllObjectNodes(templateContextIndiv, "has_category");

			if(categoryNodes.isEmpty())
				throw new EmptyListException("no category found for template context "
						+ templateContextIndiv.getURI());

			categoryUris = new ArrayList<String>();
			for(RDFNode category : categoryNodes) 
			{
				categoryUris.add(category.asResource().getURI());
			}

			// descriptor (or descriptor group) node from template context
			// also exists in the same ontology as the template 
			descriptorNode = getObjectNode(templateContextIndiv, "has_template_descriptor");
			descriptorNodeClassUri = getRDFType(descriptorNode).getURI();

			// these properties are for each template context
			isManadatory = getLiteralValue(templateContextIndiv, "is_mandatory").getBoolean();
			maxOccurrenceLiteral = getLiteralValue(templateContextIndiv, "has_abbundance");
			maxOccurrence = maxOccurrenceLiteral == null ? null : maxOccurrenceLiteral.getInt();

			// add a descriptor or descriptor group as per 
			// the descriptor node class
			switch (descriptorNodeClassUri)
			{
				case DESCRIPTOR_GROUP_CLASS_URI:

					descriptorGroup = getDescriptorGroup(descriptorNode);
					// template context overwrites the maxOccurrence of the descriptor group
					descriptorGroup.setMaxOccurrence(maxOccurrence);
					//template context changes default categories
					descriptorGroup.setCategories(categoryUris);
					for(String categoryUri : categoryUris)
					{
						if(isManadatory)
							getCategoryTemplate(template, categoryUri).addMandatoryDescriptorGroup(
									descriptorGroup.getACopy());
						else
							getCategoryTemplate(template, categoryUri).addOptionalDescriptorGroup(
									descriptorGroup.getACopy());
					}
					break;

				case DESCRIPTOR_CLASS_URI:

					descriptor = getDescriptor(descriptorNode);
					// template context overwrites the maxOccurrence of the descriptor
					descriptor.setMaxOccurrence(maxOccurrence);
					//template context changes default categories
					descriptor.setCategories(categoryUris);
					for(String categoryUri : categoryUris)
					{
						if(isManadatory)
							getCategoryTemplate(template, categoryUri).addMandatoryDescriptor(
									descriptor.getACopy());
						else
							getCategoryTemplate(template, categoryUri).addOptionalDescriptor(
									descriptor.getACopy());
					}
					break;

				default:
					throw new DatatypeFormatException(
							"unknown node type for a template context : " + descriptorNodeClassUri);
			}
		}

		return template;
	}

	protected List<Descriptor> getDescriptorsForCategory(Individual categoryIndiv)
	{
		logger.info("loading descriptors for category");

		if(categoryIndiv == null)
			return null;

		logger.info("category uri : " + categoryIndiv.getURI());
		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		for(Individual descriptorIndiv : getIndivsForCategory(
				categoryIndiv, DESCRIPTOR_CLASS_URI))
		{
			descriptors.add(getDescriptor(descriptorIndiv));
		}
		return descriptors;
	}

	protected List<DescriptorGroup> getDescriptorGroupsForCategory(Individual categoryIndiv)
	{
		logger.info("loading descriptor groups for category");

		if(categoryIndiv == null)
			return null;

		logger.info("category uri : " + categoryIndiv.getURI());
		List<DescriptorGroup> descriptorGroups = new ArrayList<DescriptorGroup>();
		for(Individual descriptorGroupIndiv : getIndivsForCategory(
				categoryIndiv, DESCRIPTOR_GROUP_CLASS_URI))
		{
			descriptorGroups.add(getDescriptorGroup(descriptorGroupIndiv));
		}
		return descriptorGroups;
	}

	private List<Individual> getIndivsForCategory(Individual categoryIndiv,
			String individualClass) throws OntologyException
	{
		OntModel model = categoryIndiv.getOntModel();

		Property hasCategoryProperty = model.getProperty(baseURI + "has_category");
		if(hasCategoryProperty == null)
			throw new OntologyException("Property \"" + baseURI + "has_category" +
					"\" not found in the model");

		// get individuals of a particular class and then filter those that have the given category
		ExtendedIterator<Individual> extendedDescriptorIterator =
				model.listIndividuals(model.getOntClass(individualClass)).filterKeep(
						new Filter<Individual>()
						{
							@Override
							public boolean accept(Individual individual)
							{
								logger.debug("matching \"" + individual.getURI() +
										"\" in category \"" + categoryIndiv.getURI() + "\"");
								return model.contains(individual,
										hasCategoryProperty, categoryIndiv);
							}
						});
		return extendedDescriptorIterator.toList();
	}

	private Literal getLiteralValue(Individual indiv, String propertyName) throws InvalidPropertyURIException
	{
		if(indiv == null || propertyName == null || propertyName.isEmpty())
			return null;

		Property property = indiv.getOntModel().getProperty((baseURI + propertyName));
		if(property == null)
			throw new InvalidPropertyURIException("Property \"" + propertyName +
					"\" not found in the model ");

		RDFNode node = indiv.getPropertyValue(property);
		if(node!= null && node.isLiteral())
		{
			return node.asLiteral();
		}

		return null;
	}

	private RDFNode getObjectNode(Individual subjectIndiv, String propertyName) throws InvalidPropertyURIException
	{
		if(subjectIndiv == null || propertyName == null || propertyName.isEmpty())
			return null;

		Property property = subjectIndiv.getOntModel().getProperty((baseURI + propertyName));
		if(property == null)
			throw new InvalidPropertyURIException("Property \"" + propertyName +
					"\" not found in the model ");

		return subjectIndiv.getPropertyValue(property);
	}

	private List<RDFNode> getAllObjectNodes(Individual subjectIndiv, String propertyName) throws InvalidPropertyURIException
	{
		if(subjectIndiv == null || propertyName == null || propertyName.isEmpty())
			return null;

		Property property = subjectIndiv.getOntModel().getProperty((baseURI + propertyName));
		if(property == null)
			throw new InvalidPropertyURIException("Property \"" + propertyName +
					"\" not found in the model ");

		return subjectIndiv.getOntModel().listObjectsOfProperty(subjectIndiv, property).toList();
	}

	protected String createTemplate(OntModel ontModel, Template template) throws AlreadyExistsException
	{
		logger.info("creating template in the ontology");

		if(template == null)
			return null;

		logger.info("template name : " + template.getLabel());

		String templateUri = template.getUri();
		// creates template uri if it is null
		if(templateUri == null)
		{
			// label is unique for each template
			templateUri = getUriFromUniqueLabel("template " + template.getLabel());
			if(ontModel.getOntResource(templateUri) != null)
			{
				logger.error("some template with this label already exists"
						+ " in this ontology : " + template.getLabel());
				throw new AlreadyExistsException("template label : " + template.getLabel());
			}

			template.setUri(templateUri);
		}
		else if(ontModel.getOntResource(template.getUri()) != null)
		{
			logger.error("some resource with this uri already exists"
					+ " in this ontology : " + template.getUri());
			throw new AlreadyExistsException(template.getUri());
		}

		Individual templateIndiv = ontModel.createIndividual(template.getUri(),
				ontModel.getOntClass(TEMPLATE_CLASS_URI));
		templateIndiv.setLabel(template.getLabel(), null);
		if(template.getDescription() != null)
			templateIndiv.setComment(template.getDescription(), null);

		addTemplateMembers(templateIndiv, template.getSampleInformationTemplate());
		addTemplateMembers(templateIndiv, template.getTrackingTemplate());
		addTemplateMembers(templateIndiv, template.getAmountTemplate());
		addTemplateMembers(templateIndiv, template.getPurityQCTemplate());
		return templateUri;
	}

	private void addTemplateMembers(Individual templateIndiv, CategoryTemplate categoryTemplate)
	{
		// add all mandatory descriptors
		for(Descriptor descriptor : categoryTemplate.getMandatoryDescriptors())
		{
			addDescriptorOrGroupToCategoryTemplate(templateIndiv, categoryTemplate.getUri(),
					DESCRIPTOR_CLASS_URI, descriptor.getUri(), descriptor.getLabel(),
					descriptor.getMaxOccurrence(), true);
		}

		// add all optional descriptors
		for(Descriptor descriptor : categoryTemplate.getOptionalDescriptors())
		{
			addDescriptorOrGroupToCategoryTemplate(templateIndiv, categoryTemplate.getUri(),
					DESCRIPTOR_CLASS_URI, descriptor.getUri(), descriptor.getLabel(),
					descriptor.getMaxOccurrence(), false);
		}

		// add all mandatory descriptor groups
		for(DescriptorGroup descriptorGroup : categoryTemplate.getMandatoryDescriptorGroups())
		{
			addDescriptorOrGroupToCategoryTemplate(templateIndiv, categoryTemplate.getUri(),
					DESCRIPTOR_GROUP_CLASS_URI, descriptorGroup.getUri(), descriptorGroup.getLabel(),
					descriptorGroup.getMaxOccurrence(), true);
		}

		// add all optional descriptor groups
		for(DescriptorGroup descriptorGroup : categoryTemplate.getOptionalDescriptorGroups())
		{
			addDescriptorOrGroupToCategoryTemplate(templateIndiv, categoryTemplate.getUri(),
					DESCRIPTOR_GROUP_CLASS_URI, descriptorGroup.getUri(), descriptorGroup.getLabel(),
					descriptorGroup.getMaxOccurrence(), false);
		}
	}

	private void addDescriptorOrGroupToCategoryTemplate(Individual templateIndiv,
			String categoryURI, String descriptorClassURI, String descriptorURI, String descriptorLabel,
			Integer maxOcurrence, boolean mandatory) throws AlreadyExistsException
	{

		logger.debug("adding descriptor (or group) " + descriptorURI 
				+ " to template " + templateIndiv.getURI()
				+ " in category " + categoryURI + " in the ontology");

		OntModel ontModel = templateIndiv.getOntModel();
		OntResource categoryResource = ontModel.getOntResource(categoryURI);
		Property hasTemplateContext = ontModel.getProperty(baseURI + "has_template_context");
		Property hasTemplateDescriptor = ontModel.getProperty(baseURI + "has_template_descriptor");
		Property hasCategory = ontModel.getProperty(baseURI + "has_category");
		Property isMandatory = ontModel.getProperty(baseURI + "is_mandatory");
		Property hasAbundance = ontModel.getProperty(baseURI + "has_abbundance");

		OntResource descriptorResource = ontModel.getOntResource(descriptorURI);
		if(descriptorResource == null)
		{
			// add minimum information for this descriptor in this ontology
			logger.info("creating descriptor resource in the ontology " + descriptorLabel);
			descriptorResource = ontModel.createOntResource(descriptorURI);
//			descriptorResource.setRDFType(ontModel.getOntClass(descriptorClassURI));
//			descriptorResource.setLabel(descriptorLabel, null);
		}

		Individual templateContextIndiv = null;
		List<RDFNode> templateContexts = ontModel.listObjectsOfProperty(templateIndiv, hasTemplateContext)
				.filterKeep(new Filter<RDFNode>()
				{
					@Override
					public boolean accept(RDFNode templateContextNode)
					{
						return ontModel.contains(templateContextNode.asResource(),
								hasTemplateDescriptor, descriptorURI);
					}
				})
				.filterKeep(new Filter<RDFNode>()
				{
					@Override
					public boolean accept(RDFNode templateContextNode)
					{
						return ontModel.contains(templateContextNode.asResource(),
								hasCategory, categoryResource);
					}
				})
				.toList();

		if(templateContexts.size() == 1)
		{
			// there exists a template context for a descriptor in a template in a category
			templateContextIndiv = ontModel.getIndividual(
					templateContexts.iterator().next().asResource().getURI());

			if(templateContextIndiv == null)
				throw new NotFoundException("template context is already there for descriptor \""
						+ descriptorLabel + "\" in template\"" + templateIndiv.getLabel(null) + "\""
						+ "\" in category\"" + categoryResource.getLabel(null) + "\"");

			templateContextIndiv.removeAll(isMandatory);
			templateContextIndiv.removeAll(hasAbundance);
		}
		else if(templateContexts.size() > 1)
		{
			throw new AlreadyExistsException("more than 1 template context is there for descriptor \""
					+ descriptorLabel + "\" in template\"" + templateIndiv.getLabel(null) + "\""
					+ "\" in category\"" + categoryResource.getLabel(null) + "\"");
		}

		if(templateContextIndiv == null)
		{
			// need to create a template context for descriptor in a template
			String templateContextLabel = "template_context_" + categoryResource.getLabel(null)
			+ "_" + descriptorLabel + "_in_" + templateIndiv.getLabel(null);
			String contextUri = getUriFromUniqueLabel(templateContextLabel);
			if(ontModel.getOntResource(contextUri) != null)
			{
				logger.error("Error generating uri for tamplate context."
						+ "Template context uri already exist : " + contextUri);
				throw new AlreadyExistsException("Template context uri already exist : " + contextUri);
			}
			templateContextIndiv = ontModel.createIndividual(contextUri,
					ontModel.getOntClass(SampleOntologyManager.TEMPLATE_CONTEXT_CLASS_URI));
			templateContextIndiv.setLabel(templateContextLabel, null);
			templateIndiv.addProperty(hasTemplateContext, templateContextIndiv);
			templateContextIndiv.addProperty(hasCategory, categoryResource);
			templateContextIndiv.addProperty(hasTemplateDescriptor, descriptorResource);
		}

		// add or update these two properties
		templateContextIndiv.addLiteral(isMandatory, mandatory);
		if(maxOcurrence != null)
		{
			templateContextIndiv.addLiteral(hasAbundance, maxOcurrence.intValue());
		}
		else
		{
			templateContextIndiv.removeAll(hasAbundance);
		}
	}

	private String getUriFromUniqueLabel(String givenLabel)
	{
		String uri = baseURI + givenLabel.toLowerCase().trim()
				.replaceAll(" ", "_").replaceAll("/", "_").replaceAll("%", "_");
		return uri;
	}
	
	public static CategoryTemplate getCategoryTemplate(Template template, String categoryUri)
	{
		CategoryTemplate categoryTemplate = null;
		switch (categoryUri)
		{
			case CATEGORY_SAMPLE_INFO_CLASS_URI:
				categoryTemplate = template.getSampleInformationTemplate();
				break;
			case CATEGORY_TRACKING_INFO_CLASS_URI:
				categoryTemplate = template.getTrackingTemplate();
				break;
			case CATEGORY_AMOUNT_CLASS_URI:
				categoryTemplate = template.getAmountTemplate();
				break;
			case CATEGORY_PURITY_QC_CLASS_URI:
				categoryTemplate = template.getPurityQCTemplate();
				break;
			default :
				break;
		}
		return categoryTemplate;
	}

}
