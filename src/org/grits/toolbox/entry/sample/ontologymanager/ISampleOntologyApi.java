/**
 * 
 */
package org.grits.toolbox.entry.sample.ontologymanager;

import java.util.List;

import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.model.Namespace;
import org.grits.toolbox.entry.sample.model.Template;

import com.hp.hpl.jena.shared.AlreadyExistsException;

/**
 * 
 *
 */
public interface ISampleOntologyApi
{
	/**
	 * returns all descriptors from the sample ontology
	 * (uses both local and standard)
	 * @return list of descriptors
	 */
	public List<Descriptor> getAllDescriptors();

	/**
	 * returns all descriptors for this category from the sample ontology
	 * @param categoryUri
	 * @return
	 */
	public List<Descriptor> getCategoryDescriptors(String categoryUri);

	/**
	 * returns all descriptor groups from the sample ontology
	 * (uses both local and standard)
	 * @return list of descriptor groups
	 */
	public List<DescriptorGroup> getAllDescriptorGroups();

	/**
	 * returns all descriptors for this category from the sample ontology
	 * @param categoryUri
	 * @return
	 */
	public List<DescriptorGroup> getCategoryDescriptorGroups(String categoryUri);

	/**
	 * returns all component templates from the sample ontology
	 * (uses both local and standard)
	 * @return
	 */
	public List<Template> getAllTemplates();

	/**
	 * returns all sample information category from the sample ontology
	 * (uses both local and standard)
	 * @return
	 */
	public List<Category> getAllCategories();

	/**
	 * returns all namespaces from the sample ontology
	 * (only checks standard ontology)
	 * @return
	 */
	public List<Namespace> getAllNamespaces();

	/**
	 * returns descriptor with the given uri or <b>null</b>
	 * if not found in either ontology
	 * @param descriptorUri uri of descriptor
	 * @return descriptor or null
	 */
	public Descriptor getDescriptor(String descriptorUri);

	/**
	 * returns descriptor group with the given uri or <b>null</b>
	 * if not found in either ontology
	 * @param descriptorGroupUri uri of descriptor group
	 * @return descriptor group or null
	 */
	public DescriptorGroup getDescriptorGroup(String descriptorGroupUri);

	/**
	 * returns template with the given uri or <b>null</b>
	 * if not found in either ontology
	 * @param templateUri uri of template
	 * @return template or null
	 */
	public Template getTemplate(String templateUri);

	/**
	 * returns category with the given uri or <b>null</b>
	 * if not found in either ontology
	 * @param categoryUri uri of category
	 * @return category or null
	 */
	public Category getCategory(String categoryUri);

	/**
	 * returns namespace with the given uri or <b>null</b>
	 * if not found in standard ontology
	 * @param namespaceUri uri of namespace
	 * @return namespace or null (if not found in standard ontology)
	 */
	public Namespace getNamespace(String namespaceUri);

	/**
	 * creates new template in local sample ontology
	 * @param template the object to be created in the ontology
	 * @return the uri of the new object
	 * @throws AlreadyExistsException if the uri already exists in the ontology
	 */
	public String createNewTemplate(Template template) throws AlreadyExistsException;
}
