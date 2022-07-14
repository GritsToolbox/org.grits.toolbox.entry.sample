package org.grits.toolbox.entry.sample.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

//import org.grits.toolbox.entry.sample.utilities.UtilityNamespaceIdFile;

/**
 * 
 */


/**
 * 
 *
 */
@XmlRootElement(name="descriptor")
@XmlType(propOrder={"label", "uri", "description", "categories", "namespaces", 
		"valueTriple", "selectedMeasurementUnit", "validUnits", "guidelineURIs"})
public class Descriptor implements EntityWithPosition{

	private String uri = null;
	private String label = null;
	private String description = null;
	private Integer maxOccurrence = null;
	private String defaultMeasurementUnit = null;
	private String selectedMeasurementUnit = null;
	private ValueTriple valueTriple = new ValueTriple();
	private List<String> categories = new ArrayList<String>();
	private List<Namespace> namespaces = new ArrayList<Namespace>();
	private List<MeasurementUnit> validUnits = new ArrayList<MeasurementUnit>();
	private HashMap<String, String> unitUriToLabelMap = new HashMap<String, String>();;
	private HashMap<String, String> unitLabelToUriMap = new HashMap<String, String>();
	private Integer position=null;
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
	@XmlElement(name="description", required=false)
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
	 * @return the namespaces
	 */
	//  @XmlElementWrapper(name="namespaces", required=true)
	@XmlElement(name="namespace")
	public List<Namespace> getNamespaces() {
		return namespaces;
	}
	/**
	 * @param namespaces the namespaces to set
	 */
	public void setNamespaces(List<Namespace> namespaces) {
		this.namespaces = namespaces;
	}
	/**
	 * 
	 * @param namespace the namespace to add
	 */
	public void addNamespace(Namespace namespace) {
		this.namespaces.add(namespace);
	}

	@XmlElement(name = "valueTriple", required=true)
	public ValueTriple getValueTriple()
	{
		return valueTriple;
	}

	public void setValueTriple(ValueTriple valueTriple)
	{
		this.valueTriple = valueTriple;
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

	/**
	 * @return the value
	 */
	@XmlTransient
	public String getValue() 
	{
		return valueTriple.getValue();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		if(namespaces.iterator().hasNext())
		{
			Namespace ns = namespaces.iterator().next();
			valueTriple.setNamespaceURI(ns.getUri());
			valueTriple.setValue(value);
		/*	if(ns.getNamespaceIdFile() != null)
			{
				if(value == null)
					valueTriple.setId(null);
				else
					valueTriple.setId(UtilityNamespaceIdFile.getId(ns.getNamespaceIdFile(), value));
			}*/
		}
	}

	/**
	 * @return the selectedMeasurementUnit
	 */
	@XmlAttribute(name="selectedUnit", required=false)
	public String getSelectedMeasurementUnit()
	{
		if(selectedMeasurementUnit == null && defaultMeasurementUnit != null)
		{
			selectedMeasurementUnit = defaultMeasurementUnit;
		}
		return selectedMeasurementUnit;
	}
	/**
	 * @param selectedMeasurementUnit the selectedMeasurementUnit to set
	 */
	public void setSelectedMeasurementUnit(String selectedMeasurementUnit) {
		this.selectedMeasurementUnit = selectedMeasurementUnit;
	}


	/**
	 * @return the selectedMeasurementUnit
	 */
	@XmlAttribute(name="defaultUnit", required=false)
	public String getDefaultMeasurementUnit()
	{
		return defaultMeasurementUnit;
	}
	/**
	 * @param selectedMeasurementUnit the selectedMeasurementUnit to set
	 */
	public void setDefaultMeasurementUnit(String defaultMeasurementUnit)
	{
		this.defaultMeasurementUnit = defaultMeasurementUnit;
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
	 * @return the validMeasurementUnits
	 */
	//	@XmlElementWrapper(name="validUnits", required=false)
	@XmlElement(name="unit")
	public List<MeasurementUnit> getValidUnits() {
		return validUnits;
	}
	/**
	 * @param validMeasurementUnits the validMeasurementUnits to set
	 */
	public void setValidUnits(List<MeasurementUnit> validUnits) {
		this.validUnits = validUnits;
		fillUnitLabelMaps();
	}

	private void fillUnitLabelMaps()
	{
		fillUnitLabelToUriMap();
		fillUnitUriToLabelMap();
	}
	/**
	 * @param validMeasurementUnits the validMeasurementUnits to set
	 */
	public void addValidUnit(MeasurementUnit validUnit)
	{
		this.validUnits.add(validUnit);
		if(!unitLabelToUriMap.containsKey(validUnit.getLabel())) {
			unitLabelToUriMap.put(validUnit.getLabel(), validUnit.getUri());
		}
		if(!unitUriToLabelMap.containsKey(validUnit.getUri())) {
			unitUriToLabelMap.put(validUnit.getUri(), validUnit.getLabel());
		}
	}

	public String getUnitLabelFromUri(String uri)
	{
		if(validUnits.size() != unitUriToLabelMap.keySet().size())
		{
			fillUnitUriToLabelMap();
		}
		return unitUriToLabelMap.get(uri);
	}

	private void fillUnitUriToLabelMap()
	{
		unitUriToLabelMap = new HashMap<String, String>();
		for(MeasurementUnit munit : validUnits) 
		{
			unitUriToLabelMap.put(munit.getUri(), munit.getLabel());
		}
	}

	public String getUnitUriFromLabel(String label)
	{
		if(validUnits.size() != unitLabelToUriMap.keySet().size())
		{
			fillUnitLabelToUriMap();
		}
		return unitLabelToUriMap.get(label);
	}

	private void fillUnitLabelToUriMap()
	{
		unitLabelToUriMap = new HashMap<String, String>();
		for(MeasurementUnit munit : validUnits) 
		{
			unitLabelToUriMap.put(munit.getLabel(), munit.getUri());
		}
	}

	/**
	 * a helper method for getting a copy of this object
	 * @return
	 */
	public Descriptor getACopy()
	{
		Descriptor descriptor = new Descriptor();
		descriptor.setUri(uri);
		descriptor.setLabel(label);
		descriptor.setDescription(description);
		descriptor.setCategories(new ArrayList<String>(getCategories()));
		if(maxOccurrence != null)
			descriptor.setMaxOccurrence(maxOccurrence.intValue());
		if (position != null)
			descriptor.setPosition(position);
		for(Namespace ns : getNamespaces())
		{
			descriptor.addNamespace(ns.getACopy());
		}
		for(MeasurementUnit validUnit : getValidUnits())
		{
			descriptor.addValidUnit(validUnit.getACopy());
		}
		descriptor.setDefaultMeasurementUnit(getDefaultMeasurementUnit());
		descriptor.setSelectedMeasurementUnit(getSelectedMeasurementUnit());
		descriptor.setValue(getValue());
		descriptor.setValueTriple(getValueTriple().getACopy());
		descriptor.setGuidelineURIs(getGuidelineURIs());
		return descriptor;
	}
}
