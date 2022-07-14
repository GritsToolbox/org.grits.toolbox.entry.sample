package org.grits.toolbox.entry.sample.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 */
import javax.xml.bind.annotation.XmlType;

/**
 * 
 *
 */
@XmlRootElement(name = "measurementUnit")
@XmlType(propOrder={"label", "uri"})
public class MeasurementUnit {

	private String uri = null;
	private String label = null;
	
	public MeasurementUnit() {
		
	}
	
	public MeasurementUnit(String uri, String label) {
		this.uri = uri;
		this.label = label;
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

	public MeasurementUnit getACopy()
	{
		MeasurementUnit measurementUnit = new MeasurementUnit();
		measurementUnit.setLabel(label);
		measurementUnit.setUri(uri);
		return measurementUnit;
	}
}
