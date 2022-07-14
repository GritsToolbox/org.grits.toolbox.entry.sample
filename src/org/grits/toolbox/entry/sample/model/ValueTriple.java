/**
 * 
 */
package org.grits.toolbox.entry.sample.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 *
 */
@XmlRootElement(name = "valueTriple")
@XmlType(propOrder={"namespaceURI", "value", "id"})
public class ValueTriple
{
    private String namespaceURI = null;
    private String value = null;
    private String id = null;

    public ValueTriple()
    {
        // TODO Auto-generated constructor stub
    }

    public ValueTriple(String namespaceURI)
    {
        this.namespaceURI = namespaceURI;
    }
    /**
     * @return the namespaceURI
     */
    @XmlAttribute(name="namespaceURI", required=true)
    public String getNamespaceURI()
    {
        return namespaceURI;
    }
    /**
     * @param namespaceURI the namespaceURI to set
     */
    public void setNamespaceURI(String namespaceURI)
    {
        this.namespaceURI = namespaceURI;
    }
    /**
     * @return the value
     */
    @XmlAttribute(name="value", required=true)
    public String getValue()
    {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    /**
     * @return the id
     */
    @XmlAttribute(name="id", required=false)
    public String getId()
    {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * a helper method for getting a copy of this object
     * @return
     */
    public ValueTriple getACopy()
    {
    	ValueTriple valueTriple = new ValueTriple();
    	valueTriple.setId(getId());
    	valueTriple.setNamespaceURI(getNamespaceURI());
    	valueTriple.setValue(getValue());
    	return valueTriple;
    }
}
