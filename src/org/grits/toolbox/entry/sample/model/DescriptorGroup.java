package org.grits.toolbox.entry.sample.model;
/**
 * 
 */


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 *
 */
@XmlRootElement(name="descriptorGroup")
@XmlType(propOrder={"label", "uri", "maxOccurrence", "description", 
		"categories", "mandatoryDescriptors", "optionalDescriptors", "guidelineURIs"})
public class DescriptorGroup implements EntityWithPosition{

	private String uri = null;
	private String label = null;
	private String description = null;
	private Integer maxOccurrence = null;
	private List<String> categories = new ArrayList<String>();
	private List<Descriptor> mandatoryDescriptors = new ArrayList<Descriptor>();
	private List<Descriptor> optionalDescriptors = new ArrayList<Descriptor>();
	private Integer position = null;
	private List<String> guidelineURIs = null;

	/**
	 * @return the uri
	 */
	@XmlAttribute(name="uri", required=true)
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
	 * @return the label
	 */
	@XmlAttribute(name="label", required=true)
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the description
	 */
	@XmlElement(name="description", required=true)
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the maxOccurrence
	 */
	@XmlAttribute(name="maxOccurrence", required=true)
	public Integer getMaxOccurrence() {
		return maxOccurrence;
	}
	/**
	 * @param maxOccurrence the maxOccurrence to set
	 */
	public void setMaxOccurrence(Integer maxOccurrence) {
		this.maxOccurrence = maxOccurrence;
	}
	/**
	 * @return the categories
	 */
	@XmlElement(name="category")
	public List<String> getCategories() {
		return categories;
	}
	/**
	 * @param categories the categories to set
	 */
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	/**
	 * 
	 * @param category the category to add
	 */
	public void addCategory(String category) {
		this.categories.add(category);
	}
	/**
	 * @return the mandatoryDescriptors
	 */
	@XmlElement(name="mandatoryDescriptor", required=false)
	public List<Descriptor> getMandatoryDescriptors() {
		return mandatoryDescriptors;
	}
	/**
	 * @param mandatoryDescriptors the mandatoryDescriptors to set
	 */
	public void setMandatoryDescriptors(List<Descriptor> mandatoryDescriptors) {
		this.mandatoryDescriptors = mandatoryDescriptors;
	}
	/**
	 * @param mandatoryDescriptors the mandatoryDescriptors to set
	 */
	public void addMandatoryDescriptor(Descriptor mandatoryDescriptor) {
		this.mandatoryDescriptors.add(mandatoryDescriptor);
	}
	/**
	 * @return the optionalDescriptors
	 */
	@XmlElement(name="optionalDescriptor", required=false)
	public List<Descriptor> getOptionalDescriptors() {
		return optionalDescriptors;
	}
	/**
	 * @param optionalDescriptors the optionalDescriptors to set
	 */
	public void setOptionalDescriptors(List<Descriptor> optionalDescriptors) {
		this.optionalDescriptors = optionalDescriptors;
	}
	/**
	 * @param mandatoryDescriptors the mandatoryDescriptors to set
	 */
	public void addOptionalDescriptor(Descriptor optionalDescriptor) {
		this.optionalDescriptors.add(optionalDescriptor);
	}
	/**
	 * @param string
	 */
	public void setColumnValue(String string) {
		this.setLabel(string);
	}
	
	@XmlAttribute
	public Integer getPosition() {
		return position;
	}
	
	public void setPosition(Integer position) {
		this.position = position;
	}

	public List<String> getGuidelineURIs() {
		return guidelineURIs;
	}
	
	public void setGuidelineURIs(List<String> guidelineURIs) {
		this.guidelineURIs = guidelineURIs;
	}
	
    public DescriptorGroup getACopy()
	{
		DescriptorGroup descriptorGroup = new DescriptorGroup();
		descriptorGroup.setLabel(getLabel());
		descriptorGroup.setUri(getUri());
		descriptorGroup.setDescription(getDescription());
		descriptorGroup.setCategories(new ArrayList<String>(getCategories()));
		descriptorGroup.setPosition(getPosition());
		if(maxOccurrence != null)
			descriptorGroup.setMaxOccurrence(maxOccurrence.intValue());
		for(Descriptor desc : mandatoryDescriptors)
		{
			descriptorGroup.addMandatoryDescriptor(desc.getACopy());
		}
		for(Descriptor desc : optionalDescriptors)
		{
			descriptorGroup.addOptionalDescriptor(desc.getACopy());
		}
		descriptorGroup.setGuidelineURIs(getGuidelineURIs());
		return descriptorGroup;
	}
}
