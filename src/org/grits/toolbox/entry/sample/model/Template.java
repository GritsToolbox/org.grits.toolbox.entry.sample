package org.grits.toolbox.entry.sample.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.grits.toolbox.entry.sample.ontologymanager.SampleOntologyManager;

/**
 * 
 */

/**
 * 
 *
 */
@XmlRootElement(name = "template")
@XmlType(propOrder={"label", "uri", "description", 
		"sampleInformationTemplate", "amountTemplate", 
		"purityQCTemplate", "trackingTemplate"})
public class Template {
	
	private String uri = null;
	private String label = null;
	private String description = null;
	private CategoryTemplate sampleInformationTemplate = null;
	private CategoryTemplate amountTemplate = null;
	private CategoryTemplate purityQCTemplate = null;
	private CategoryTemplate trackingTemplate = null;
    private Integer glycovaultId = null;
	
	public Template()
    {
	    sampleInformationTemplate = new CategoryTemplate(SampleOntologyManager.CATEGORY_SAMPLE_INFO_CLASS_URI);
	    amountTemplate = new CategoryTemplate(SampleOntologyManager.CATEGORY_AMOUNT_CLASS_URI);
	    purityQCTemplate = new CategoryTemplate(SampleOntologyManager.CATEGORY_PURITY_QC_CLASS_URI);
	    trackingTemplate = new CategoryTemplate(SampleOntologyManager.CATEGORY_TRACKING_INFO_CLASS_URI);
    }
	
	/**
	 * @return the uri
	 */
	@XmlAttribute(name = "uri", required=true)
	public String getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
		sampleInformationTemplate.setTemplateURI(uri);
		amountTemplate.setTemplateURI(uri);
		purityQCTemplate.setTemplateURI(uri);
		trackingTemplate.setTemplateURI(uri);
	}
	/**
	 * @return the label
	 */
	@XmlAttribute(name = "label", required=true)
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
	@XmlElement(name = "description", required=true)
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
	 * @return the sampleInformationTemplate
	 */
	@XmlElement(name = "sampleInformation", required=true)
	public CategoryTemplate getSampleInformationTemplate() {
		return sampleInformationTemplate;
	}
	/**
	 * @param sampleInformationTemplate the sampleInformationTemplate to set
	 */
	public void setSampleInformationTemplate(
			CategoryTemplate sampleInformationTemplate) {
		this.sampleInformationTemplate = sampleInformationTemplate;
	}
	/**
	 * @return the amountTemplate
	 */
	@XmlElement(name = "amount", required=true)
	public CategoryTemplate getAmountTemplate() {
		return amountTemplate;
	}
	/**
	 * @param amountTemplate the amountTemplate to set
	 */
	public void setAmountTemplate(CategoryTemplate amountTemplate) {
		this.amountTemplate = amountTemplate;
	}
	/**
	 * @return the purityQCTemplate
	 */
	@XmlElement(name = "purityQC", required=true)
	public CategoryTemplate getPurityQCTemplate() {
		return purityQCTemplate;
	}
	/**
	 * @param purityQCTemplate the purityQCTemplate to set
	 */
	public void setPurityQCTemplate(CategoryTemplate purityQCTemplate) {
		this.purityQCTemplate = purityQCTemplate;
	}
	/**
	 * @return the trackingTemplate
	 */
	@XmlElement(name = "tracking", required=true)
	public CategoryTemplate getTrackingTemplate() {
		return trackingTemplate;
	}
	/**
	 * @param trackingTemplate the trackingTemplate to set
	 */
	public void setTrackingTemplate(CategoryTemplate trackingTemplate) {
		this.trackingTemplate = trackingTemplate;
	}

    /**
     * @return the glycovaultId
     */
    @XmlAttribute(name = "glycovaultId", required= false)
    public Integer getGlycovaultId()
    {
        return glycovaultId;
    }

    /**
     * @param glycovaultId the glycovaultId to set
     */
    public void setGlycovaultId(Integer glycovaultId)
    {
        this.glycovaultId  = glycovaultId;
    }
}
