package org.grits.toolbox.entry.sample.model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */

/**
 * 
 *
 */
@XmlRootElement(name = "sample")
@XmlType(propOrder={"name", "description", "version", "components"})
public class Sample {

	public static final String CURRENT_VERSION = "1.0";
	private String name = null;
	private String description = null;
	private Double version = null;
	private List<Component> components = new ArrayList<Component>();

	public Sample()
	{
		this.version = Double.valueOf(CURRENT_VERSION);
	}
	/**
	 * @return the name
	 */
	@XmlAttribute(name = "name", required= true)
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the version
	 */
	@XmlAttribute(name = "version", required= true)
	public Double getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Double version) {
		this.version = version;
	}
	/**
	 * @return the components
	 */
	@XmlElement(name="component")
	public List<Component> getComponents()
	{
		return components;
	}
	/**
	 * @param components the components to set
	 */
	public void setComponents(List<Component> components)
	{
		this.components = components;
	}

	public void addComponent(Component comp) {
		this.components.add(comp);
	}

	/**
	 * @return the nextAvailableComponentId
	 */
	public Integer getNextAvailableComponentId()
	{
		Integer lastMaxId = 0;
		for(Component comp : components)
		{
			if(comp.getComponentId()!= null 
					&& comp.getComponentId()>lastMaxId)
			{
				lastMaxId = comp.getComponentId();
			}
		}
		return lastMaxId+1;
	}

	/**
	 * returns all labels of components of the sample
	 * @param sample whose component labels are to be returned
	 * @return set of labels
	 */
	public Set<String> getAllComponentLabels()
	{
		Set<String> allLabels = new HashSet<String>();
		for(Component component : components)
		{
			allLabels.add(component.getLabel());
		}
		return allLabels;
	}
}
