package org.grits.toolbox.entry.sample.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;


/**
 * 
 */

/**
 * 
 *
 */
@XmlRootElement(name = "component")
@XmlType(propOrder={"label", "uri", "description", 
		"sampleInformation", "amount", "purityQC", "tracking"})
public class Component {

    private Integer componentId = null;
	private String uri = null;
	private String label = null;
	private String templateUri = null;
	private String description = null;
	private Category sampleInformation = null;
	private Category amount = null;
	private Category purityQC = null;
	private Category tracking = null;

	public Component() {
		sampleInformation = new Category();
		sampleInformation.setUri(SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI);
		tracking = new Category();
		tracking.setUri(SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI);
		amount = new Category();
		amount.setUri(SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI);
		purityQC = new Category();
		purityQC.setUri(SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI);
	}

    public Component(Integer componentId) {
        this();
        this.componentId = componentId;
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
	 * @return the label
	 */
	@XmlAttribute(name = "label", required= true)
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
     * @return the templateUri
     */
    @XmlAttribute(name = "templateUri", required= false)
    public String getTemplateUri()
    {
        return templateUri;
    }

    /**
     * @param templateUri the templateUri to set
     */
    public void setTemplateUri(String templateUri)
    {
        this.templateUri = templateUri;
    }

	/**
	 * @return the description
	 */
	@XmlElement(name = "description", required= false)
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
	 * @return the sampleInformation
	 */
	@XmlElement(name = "sampleInformation", required= true)
	public Category getSampleInformation() {
		return sampleInformation;
	}

	/**
	 * @param sampleInformation the sampleInformation to set
	 */
	public void setSampleInformation(Category sampleInformation) {
		this.sampleInformation = sampleInformation;
	}

	/**
	 * @return the amount
	 */
	@XmlElement(name = "amount", required= true)
	public Category getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Category amount) {
		this.amount = amount;
	}

	/**
	 * @return the purityQC
	 */
	@XmlElement(name = "purityQC", required= true)
	public Category getPurityQC() {
		return purityQC;
	}

	/**
	 * @param purityQC the purityQC to set
	 */
	public void setPurityQC(Category purityQC) {
		this.purityQC = purityQC;
	}

	/**
	 * @return the tracking
	 */
	@XmlElement(name = "tracking", required= true)
	public Category getTracking() {
		return tracking;
	}
	/**
	 * @param tracking the tracking to set
	 */
	public void setTracking(Category tracking) {
		this.tracking = tracking;
	}

    /**
     * @return the componentId
     */
	@XmlAttribute(name = "componentId", required= true)
    public Integer getComponentId()
    {
        return componentId;
    }
    /**
     * @param componentId the componentId to set
     */
    public void setComponentId(Integer componentId)
    {
        this.componentId = componentId;
    }

    @XmlTransient
    public Component getACopy()
    {
    	Component component = new Component();
    	component.setLabel(getLabel());
    	component.setComponentId(getComponentId());
    	component.setUri(getUri());
    	component.setDescription(getDescription());
    	component.setTemplateUri(getTemplateUri());
    	component.setSampleInformation(sampleInformation.getACopy());
    	component.setTracking(tracking.getACopy());
    	component.setAmount(amount.getACopy());
    	component.setPurityQC(purityQC.getACopy());
    	return component;
    }
}
