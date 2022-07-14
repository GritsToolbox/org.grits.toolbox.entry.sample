/**
 * 
 */
package org.grits.toolbox.entry.sample.ontologymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Namespace;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.utilities.UtilityDescriptorDescriptorGroup;
import org.grits.toolbox.entry.sample.utilities.UtilityOntologyLocation;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.AlreadyExistsException;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * 
 *
 */
public class SampleOntologyApi implements ISampleOntologyApi
{
	private Logger logger = Logger.getLogger(SampleOntologyApi.class);

	protected SampleOntologyManager ontologyManager = null;
	protected File standardOntologyFile = null;
	protected File localOntologyFile = null;
	protected OntModel localOntModel = null;

	public SampleOntologyApi() throws IOException, Exception
	{
		logger.info("loading sample ontologies");
		try
		{
			logger.info("loading standard sample ontology");
			standardOntologyFile = new File(UtilityOntologyLocation.getStandardOntologyLocation());
			FileInputStream fileInputStream = new FileInputStream(standardOntologyFile);
			OntModel standardOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);
			standardOntModel.read(fileInputStream, SampleOntologyManager.baseURI);
			// add manager class that uses standard ontology by default
			// uses low-level apis from jena library for interacting with the model
			ontologyManager = new SampleOntologyManager(standardOntModel);

			logger.info("loading local sample ontology");
			localOntologyFile  = 
					new File(UtilityOntologyLocation.getLocalOntologyLocation());
			fileInputStream = new FileInputStream(localOntologyFile);
			localOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, null);
			localOntModel.read(fileInputStream, SampleOntologyManager.baseURI);

		} catch (IOException ex)
		{
			logger.error("error reading ontologies\n" + ex.getMessage(), ex);
			throw ex;
		} catch (Exception ex)
		{
			logger.fatal("something went wrong while loading ontologies\n" + ex.getMessage(), ex);
			throw ex;
		}

		logger.info("sample ontologies loaded");
	}

	protected OntModel getStandardOntModel()
	{
		return ontologyManager.getStandardOntModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Descriptor> getAllDescriptors()
	{
		logger.info("loading all descriptors");
		List<Descriptor> descriptors = new ArrayList<Descriptor>();

		logger.info("loading all descriptors from standard ontology");
		List<Individual> standardDescriptorIndivs = ontologyManager.getIndividualsForClass(
				ontologyManager.standardOntModel, SampleOntologyManager.DESCRIPTOR_CLASS_URI);
		Set<String> descriptorUris = new HashSet<String>();
		for(Individual descriptorIndiv : standardDescriptorIndivs)
		{
			try
			{
				descriptors.add(ontologyManager.getDescriptor(descriptorIndiv));
				descriptorUris.add(descriptorIndiv.getURI());
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}

		logger.info("loading all other descriptors from local ontology");
		List<Individual> localDescriptorIndivs = ontologyManager
				.getIndividualsForClass(localOntModel, SampleOntologyManager.DESCRIPTOR_CLASS_URI);
		for(Individual descriptorIndiv : localDescriptorIndivs)
		{
			if(!descriptorUris.contains(descriptorIndiv.getURI()))
			{
				try
				{
					descriptors.add(ontologyManager.getDescriptor(descriptorIndiv));
				} catch (Exception e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}

		return descriptors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Descriptor> getCategoryDescriptors(String categoryUri)
	{
		logger.info("loading all descriptors for category " + categoryUri);

		if(categoryUri == null)
			return null;

		Individual categoryIndiv = ontologyManager.standardOntModel.getIndividual(categoryUri);
		if(categoryIndiv == null)
		{
			logger.error("category not found in standard ontology : " + categoryUri);
			return null;
		}
		String indivType = getCategoryIndivType(categoryIndiv);
		if(!SampleOntologyManager.CATEGORY_CLASS_URI.equals(indivType))
		{
			logger.fatal("The given category uri \"" + categoryUri
					+ "\" in standard ontology has error verfying its type. "
					+ indivType);
			return null;
		}

		// also look for category in local ontology
		Individual localCategoryIndiv = localOntModel.getIndividual(categoryUri);
		if(localCategoryIndiv == null)
		{
			logger.error("category not found in local ontology : " + categoryUri);
			return null;
		}
		indivType = getCategoryIndivType(localCategoryIndiv);
		if(!SampleOntologyManager.CATEGORY_CLASS_URI.equals(indivType))
		{
			logger.fatal("The given category uri \"" + categoryUri
					+ "\" in local ontology has error verfying its type. "
					+ indivType);
			return null;
		}

		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		try
		{
			// get standard descriptors
			descriptors = ontologyManager.getDescriptorsForCategory(categoryIndiv);

			// create a set of unique descriptor uris
			Set<String> uriSet = new HashSet<String>();
			for(Descriptor descriptor : descriptors)
			{
				if(uriSet.contains(descriptor.getUri()))
				{
					logger.error("duplicate descriptor in standard ontology : " + descriptor.getUri());
					continue;
				}

				uriSet.add(descriptor.getUri());
			}

			// add unique descriptors from local ontology that were not in the standard ontology
			for(Descriptor descriptor : ontologyManager.getDescriptorsForCategory(localCategoryIndiv))
			{
				if(uriSet.contains(descriptor.getUri()))
				{
					logger.error("duplicate descriptor in local ontology : " + descriptor.getUri());
					continue;
				}

				descriptors.add(descriptor);
				uriSet.add(descriptor.getUri());
			}
		} catch (Exception e)
		{
			logger.fatal(e.getMessage(), e);
		}

		return descriptors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DescriptorGroup> getAllDescriptorGroups()
	{
		logger.info("loading all descriptor groups");
		List<DescriptorGroup> descriptorGroups = new ArrayList<DescriptorGroup>();

		logger.info("loading all descriptor groups from standard ontology");
		List<Individual> standardDescriptorGroupIndivs = ontologyManager.getIndividualsForClass(
				ontologyManager.standardOntModel, SampleOntologyManager.DESCRIPTOR_GROUP_CLASS_URI);
		Set<String> descriptorGroupUris = new HashSet<String>();
		for(Individual descriptorGroupIndiv : standardDescriptorGroupIndivs)
		{
			try
			{
				descriptorGroups.add(ontologyManager.getDescriptorGroup(descriptorGroupIndiv));
				descriptorGroupUris.add(descriptorGroupIndiv.getURI());
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}

		logger.info("loading all other descriptor groups from local ontology");
		List<Individual> localDescriptorGroupIndivs = ontologyManager
				.getIndividualsForClass(localOntModel, SampleOntologyManager.DESCRIPTOR_GROUP_CLASS_URI);
		for(Individual descriptorGroupIndiv : localDescriptorGroupIndivs)
		{
			if(!descriptorGroupUris.contains(descriptorGroupIndiv.getURI()))
			{
				try
				{
					descriptorGroups.add(ontologyManager.getDescriptorGroup(descriptorGroupIndiv));
				} catch (Exception e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}

		return descriptorGroups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DescriptorGroup> getCategoryDescriptorGroups(String categoryUri)
	{
		logger.info("loading all descriptor groups for category " + categoryUri);

		if(categoryUri == null)
			return null;

		Individual categoryIndiv = ontologyManager.standardOntModel.getIndividual(categoryUri);
		if(categoryIndiv == null)
		{
			logger.error("category not found in standard ontology : " + categoryUri);
			return null;
		}
		String indivType = getCategoryIndivType(categoryIndiv);
		if(!SampleOntologyManager.CATEGORY_CLASS_URI.equals(indivType))
		{
			logger.fatal("The given category uri \"" + categoryUri
					+ "\" in standard ontology has error verfying its type. "
					+ indivType);
			return null;
		}

		// also look for category in local ontology
		Individual localCategoryIndiv = localOntModel.getIndividual(categoryUri);
		if(localCategoryIndiv == null)
		{
			logger.error("category not found in local ontology : " + categoryUri);
			return null;
		}
		indivType = getCategoryIndivType(localCategoryIndiv);
		if(!SampleOntologyManager.CATEGORY_CLASS_URI.equals(indivType))
		{
			logger.fatal("The given category uri \"" + categoryUri
					+ "\" in local ontology has error verfying its type. "
					+ indivType);
			return null;
		}

		List<DescriptorGroup> descriptorGroups = new ArrayList<DescriptorGroup>();
		try
		{
			// get standard descriptor groups
			descriptorGroups = ontologyManager.getDescriptorGroupsForCategory(categoryIndiv);

			// create a set of unique descriptor group uris
			Set<String> uriSet = new HashSet<String>();
			for(DescriptorGroup descriptorGroup : descriptorGroups)
			{
				if(uriSet.contains(descriptorGroup.getUri()))
				{
					logger.error("duplicate descriptor group in standard ontology : " + descriptorGroup.getUri());
					continue;
				}

				uriSet.add(descriptorGroup.getUri());
			}

			// add unique descriptor groups from local ontology that were not in the standard ontology
			for(DescriptorGroup descriptorGroup : ontologyManager.getDescriptorGroupsForCategory(localCategoryIndiv))
			{
				if(uriSet.contains(descriptorGroup.getUri()))
				{
					logger.error("duplicate descriptor group in local ontology : " + descriptorGroup.getUri());
					continue;
				}

				descriptorGroups.add(descriptorGroup);
				uriSet.add(descriptorGroup.getUri());
			}
		} catch (Exception e)
		{
			logger.fatal(e.getMessage(), e);
		}

		return descriptorGroups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Template> getAllTemplates()
	{
		logger.info("loading all templates");
		List<Template> templates = new ArrayList<Template>();

		logger.info("loading all templates from standard ontology");
		List<Individual> standardTemplateIndivs = ontologyManager.getIndividualsForClass(
				ontologyManager.standardOntModel, SampleOntologyManager.TEMPLATE_CLASS_URI);
		Set<String> templateUris = new HashSet<String>();
		for(Individual templateIndiv : standardTemplateIndivs)
		{
			try
			{
				templates.add(ontologyManager.getTemplate(templateIndiv));
				templateUris.add(templateIndiv.getURI());
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}

		logger.info("loading all other templates from local ontology");
		List<Individual> localTemplateIndivs = ontologyManager
				.getIndividualsForClass(localOntModel, SampleOntologyManager.TEMPLATE_CLASS_URI);
		for(Individual templateIndiv : localTemplateIndivs)
		{
			try
			{
				if(!templateUris.contains(templateIndiv.getURI()))
				{
					templates.add(ontologyManager.getTemplate(templateIndiv));
				}
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}

		return templates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Category> getAllCategories()
	{
		logger.info("loading all categories");

		List<Category> categories = new ArrayList<Category>();

		logger.info("loading all categories from standard ontology");
		Category category = null;
		for(OntResource categoryResource :  ontologyManager.standardOntModel.getOntClass(
				SampleOntologyManager.CATEGORY_CLASS_URI).listInstances().toList())
		{
			try
			{
				category = getCategory(categoryResource.getURI());
				if(category == null)
				{
					logger.error("error getting category : " + categoryResource.getURI());
				}
				else
				{
					categories.add(category);
				}
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}

		return categories;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Namespace> getAllNamespaces()
	{
		logger.info("loading all namespaces");

		List<Namespace> namespaces = new ArrayList<Namespace>();

		logger.info("loading all namespaces from standard ontology");
		;
		List<Resource> standardNamespaceIndivs = ontologyManager.standardOntModel.listResourcesWithProperty(RDF.type,
				ontologyManager.standardOntModel.getOntClass(SampleOntologyManager.NAMESPACE_CLASS_URI)).toList();
		Namespace namespace = null;
		for(Resource namespaceIndiv : standardNamespaceIndivs)
		{
			try
			{
				namespace = getNamespace(namespaceIndiv.getURI());
				if(namespace == null)
				{
					logger.error("error getting namespace : " + namespaceIndiv.getURI());
				}
				else
				{
					namespaces.add(namespace);
				}
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}

		return namespaces;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Descriptor getDescriptor(String descriptorUri)
	{
		logger.info("loading descriptor : " + descriptorUri);

		if(descriptorUri == null)
			return null;

		Individual descriptorIndiv =
				ontologyManager.standardOntModel.getIndividual(descriptorUri);

		// if not found and not looked into local ontology then look there
		if(descriptorIndiv == null)
		{
			logger.info("loading descriptor from local ontology : " + descriptorUri);
			descriptorIndiv = localOntModel.getIndividual(descriptorUri);
		}

		try
		{
			if(descriptorIndiv != null)
			{
				return ontologyManager.getDescriptor(descriptorIndiv);
			}
			else
			{
				logger.error("descriptor not found : " + descriptorUri);
			}
		} catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DescriptorGroup getDescriptorGroup(String descriptorGroupUri)
	{
		logger.info("loading descriptor group : " + descriptorGroupUri);

		if(descriptorGroupUri == null)
			return null;

		Individual descriptorGroupIndiv =
				ontologyManager.standardOntModel.getIndividual(descriptorGroupUri);

		// if not found and not looked into local ontology then look there
		if(descriptorGroupIndiv == null)
		{
			logger.info("loading descriptor group from local ontology : " + descriptorGroupUri);
			descriptorGroupIndiv = localOntModel.getIndividual(descriptorGroupUri);
		}

		try
		{
			if(descriptorGroupIndiv != null)
			{
				return ontologyManager.getDescriptorGroup(descriptorGroupIndiv);
			}
			else
			{
				logger.error("descriptor group not found : " + descriptorGroupUri);
			}
		} catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Template getTemplate(String templateUri)
	{
		logger.info("loading template : " + templateUri);

		if(templateUri == null)
			return null;

		Individual templateIndiv =
				ontologyManager.standardOntModel.getIndividual(templateUri);

		// if not found and not looked into local ontology then look there
		if(templateIndiv == null)
		{
			logger.info("loading template from local ontology : " + templateUri);
			templateIndiv = localOntModel.getIndividual(templateUri);
		}

		try
		{
			if(templateIndiv != null)
			{
				return ontologyManager.getTemplate(templateIndiv);
			}
			else
			{
				logger.error("template not found : " + templateUri);
			}
		} catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Category getCategory(String categoryUri)
	{
		logger.info("loading category " + categoryUri);

		if(categoryUri == null)
			return null;

		// get category individual from standard ontology
		Individual categoryIndiv = ontologyManager.standardOntModel.getIndividual(categoryUri);
		if(categoryIndiv == null)
		{
			logger.fatal("category missing in standard ontology : " + categoryUri);
			return null;
		}

		String indivType = getCategoryIndivType(categoryIndiv);
		if(!SampleOntologyManager.CATEGORY_CLASS_URI.equals(indivType))
		{
			logger.fatal("The given category uri \"" + categoryIndiv.getURI()
					+ "\" in standard ontology has error verfying its type. "
					+ indivType);
			return null;
		}

		// also get this category individual from local ontology
		Individual localCategoryIndiv = localOntModel.getIndividual(categoryUri);
		if(localCategoryIndiv == null)
		{
			logger.fatal("category missing in local ontology : " + categoryUri);
			return null;
		}

		indivType = getCategoryIndivType(localCategoryIndiv);
		if(!SampleOntologyManager.CATEGORY_CLASS_URI.equals(indivType))
		{
			logger.fatal("The given category uri \"" + categoryIndiv.getURI()
					+ "\" in local ontology has error verfying its type. "
					+ indivType);
			return null;
		}

		try
		{
			Category category = new Category(categoryUri);
			// local ontology should have the same label as the standard ontology
			if(!categoryIndiv.getLabel(null).equals(localCategoryIndiv.getLabel(null)))
			{
				logger.error("This category \"" + categoryUri + "\" has a different label in "
						+ " local ontology : " + localCategoryIndiv.getLabel(null));
			}
			category.setLabel(categoryIndiv.getLabel(null));

			// add all standard descriptors
			logger.info("loading descriptors from standard ontology for this category : " + categoryUri);
			List<Descriptor> standardDescriptors = ontologyManager.getDescriptorsForCategory(categoryIndiv);
			category.getDescriptors().addAll(standardDescriptors);

			// add all standard descriptor groups
			logger.info("loading descriptor groups from standard ontology for this category : " + categoryUri);
			List<DescriptorGroup> standardDescriptorGroups = ontologyManager.getDescriptorGroupsForCategory(categoryIndiv);
			category.getDescriptorGroups().addAll(standardDescriptorGroups);

			List<String> standardDescriptorUris = UtilityDescriptorDescriptorGroup
					.getDescriptorURIs(standardDescriptors);
			// load remaining descriptors from local ontology
			logger.info("loading descriptors from local ontology for this category : " + categoryUri);
			for(Descriptor descriptor :
				ontologyManager.getDescriptorsForCategory(localCategoryIndiv))
			{
				if(!standardDescriptorUris.contains(descriptor.getUri()))
					category.getDescriptors().add(descriptor);
			}

			List<String> standardDescriptorGroupUris = UtilityDescriptorDescriptorGroup
					.getDescriptorGroupURIs(standardDescriptorGroups);
			// load remaining descriptor groups from local ontology
			logger.info("loading descriptor groups from local ontology for this category : " + categoryUri);
			for(DescriptorGroup descriptorGroup :
				ontologyManager.getDescriptorGroupsForCategory(localCategoryIndiv))
			{
				if(!standardDescriptorGroupUris.contains(descriptorGroup.getUri()))
					category.getDescriptorGroups().add(descriptorGroup);
			}

			return category;

		} catch (Exception ex)
		{
			logger.fatal(ex.getMessage(), ex);
		}

		return null;
	}

	private String getCategoryIndivType(Individual categoryIndiv)
	{
		List<OntClass> ontClasses = categoryIndiv.listOntClasses(true).filterDrop(new Filter<OntClass>()
		{
			@Override
			public boolean accept(OntClass ontClass)
			{
				return ontClass.getURI().equals("http://www.w3.org/2002/07/owl#NamedIndividual");
			}
		}).toList();

		if(ontClasses.isEmpty())
			return null;

		return ontClasses.size() == 1
				&& SampleOntologyManager.CATEGORY_CLASS_URI.equals(ontClasses.iterator().next().getURI())
				? SampleOntologyManager.CATEGORY_CLASS_URI
						: "more than 1 type (number of types : "
						+ ontClasses.size()
						+ ") or not category type (type : "
						+ ontClasses.iterator().next().getURI() + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Namespace getNamespace(String namespaceUri)
	{
		logger.info("loading namespace " + namespaceUri);

		if(namespaceUri == null)
			return null;

		logger.info("loading namespace " + namespaceUri);
		try
		{
			return ontologyManager.getNamespace(ontologyManager
					.standardOntModel.getOntResource(namespaceUri));
		} catch (Exception ex)
		{
			logger.fatal(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createNewTemplate(Template template) throws AlreadyExistsException
	{
		logger.info("creating template in local ontology");
		String templateUri = ontologyManager.createTemplate(localOntModel, template);
		if(templateUri != null)
		{
			saveLocalOntology();
		}
		return templateUri;
	}

	protected void saveLocalOntology()
	{
		try
		{
			FileWriter fileWriter = new FileWriter(localOntologyFile);
			try
			{
				localOntModel.write(fileWriter, null);
			}
			finally
			{
				fileWriter.close();
			}
		} catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}
}
