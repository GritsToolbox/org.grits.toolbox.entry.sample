package org.grits.toolbox.entry.sample.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */

/**
 * 
 *
 */
@XmlType(propOrder={"uri", "label", "descriptorGroups", "descriptors"})
public class Category {

	private String uri = null;
	private String label = null;
	private List<Descriptor> descriptors = new ArrayList<Descriptor>();
	private List<DescriptorGroup> descriptorGroups = new ArrayList<DescriptorGroup>();
	
	public Category()
    {
	    
    }
	public Category(String uri)
    {
        this.uri = uri;
    }
    /**
	 * @return the uri
	 */
	@XmlAttribute(name = "uri", required= true)
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * returns label of category
	 * @return label
	 */
	@XmlAttribute(name = "label", required= true)
	public String getLabel()
	{
		return label;
	}

	/**
	 * sets the label of the category
	 * @param label
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return the descriptorGroups
	 */
	@XmlElement(name="descriptorGroup", required=false)
	public List<DescriptorGroup> getDescriptorGroups() {
		return descriptorGroups;
	}
	/**
	 * @param descriptorGroups the descriptorGroups to set
	 */
	public void setDescriptorGroups(List<DescriptorGroup> descriptorGroups) {
		this.descriptorGroups = descriptorGroups;
	}
	/**
	 * @param descriptors the descriptors to set
	 */
	public void addDescriptorGroup(DescriptorGroup descriptorGroup) {
		this.descriptorGroups.add(descriptorGroup);
	}
	/**
	 * @return the descriptors
	 */
	@XmlElement(name="descriptor", required=false)
	public List<Descriptor> getDescriptors() {
		return descriptors;
	}
	/**
	 * @param descriptors the descriptors to set
	 */
	public void setDescriptors(List<Descriptor> descriptors) {
		this.descriptors = descriptors;
	}
	/**
	 * @param descriptors the descriptors to set
	 */
	public void addDescriptor(Descriptor descriptor) {
		this.descriptors.add(descriptor);
	}

	public Category getACopy()
	{
		Category category = new Category();
		category.setUri(uri);
		for(Descriptor desc : descriptors)
		{
			category.addDescriptor(desc.getACopy());
		}
		for(DescriptorGroup dg : descriptorGroups)
		{
			category.addDescriptorGroup(dg.getACopy());
		}
		return category;
	}
}
