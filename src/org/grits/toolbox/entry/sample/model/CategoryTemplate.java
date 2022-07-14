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
@XmlType(propOrder={"uri", "mandatoryDescriptorGroups", 
		"mandatoryDescriptors", "optionalDescriptorGroups", 
		"optionalDescriptors"})
public class CategoryTemplate {

	private String uri = null;
	private String templateURI = null;
	private List<DescriptorGroup> mandatoryDescriptorGroups = new ArrayList<DescriptorGroup>();
	private List<DescriptorGroup> optionalDescriptorGroups = new ArrayList<DescriptorGroup>();
	private List<Descriptor> mandatoryDescriptors = new ArrayList<Descriptor>();
	private List<Descriptor> optionalDescriptors = new ArrayList<Descriptor>();
	
	public CategoryTemplate()
    {
        
    }
	
	public CategoryTemplate(String categoryClassUri)
    {
        this.uri = categoryClassUri;
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
	 * @return the mandatoryDescriptorGroups
	 */
	@XmlElement(name="mandatoryDescriptorGroup", required=false)
	public List<DescriptorGroup> getMandatoryDescriptorGroups() {
		return mandatoryDescriptorGroups;
	}
	/**
	 * @param mandatoryDescriptorGroups the mandatoryDescriptorGroups to set
	 */
	public void setMandatoryDescriptorGroups(
			List<DescriptorGroup> mandatoryDescriptorGroups) {
		this.mandatoryDescriptorGroups = mandatoryDescriptorGroups;
	}
	/**
	 * @param optionalDescriptors the optionalDescriptors to set
	 */
	public void addMandatoryDescriptorGroup(DescriptorGroup mandatoryDescriptorGroup) {
		this.mandatoryDescriptorGroups.add(mandatoryDescriptorGroup);
	}
	/**
	 * @return the optionalDescriptorGroups
	 */
	@XmlElement(name="optionalDescriptorGroup", required=false)
	public List<DescriptorGroup> getOptionalDescriptorGroups() {
		return optionalDescriptorGroups;
	}
	/**
	 * @param optionalDescriptorGroups the optionalDescriptorGroups to set
	 */
	public void setOptionalDescriptorGroups(
			List<DescriptorGroup> optionalDescriptorGroups) {
		this.optionalDescriptorGroups = optionalDescriptorGroups;
	}
	/**
	 * @param optionalDescriptors the optionalDescriptors to set
	 */
	public void addOptionalDescriptorGroup(DescriptorGroup optionalDescriptorGroup) {
		this.optionalDescriptorGroups.add(optionalDescriptorGroup);
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
	 * @param optionalDescriptors the optionalDescriptors to set
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
	 * @param optionalDescriptors the optionalDescriptors to set
	 */
	public void addOptionalDescriptor(Descriptor optionalDescriptor) {
		this.optionalDescriptors.add(optionalDescriptor);
	}
    /**
     * @return the templateURI
     */
    public String getTemplateURI()
    {
        return templateURI;
    }
    /**
     * @param templateURI the templateURI to set
     */
    public void setTemplateURI(String templateURI)
    {
        this.templateURI = templateURI;
    }
}
