/**
 * 
 */
package org.grits.toolbox.entry.sample.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 *
 */
@XmlRootElement(name = "namespace")
public class Namespace {
	
	private String uri = null;
	private String label = null;
	private String namespaceFile = null;
    private String namespaceIdFile = null;
	
	public Namespace() {
		
	}

	public Namespace(String uri, String label, String namespaceFile, String namespaceIdFile) {
		this.uri = uri;
		this.label = label;
		this.namespaceFile = namespaceFile;
		this.namespaceIdFile = namespaceIdFile;
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
    public String getLabel()
    {
        if(this.label == null)
        {
            this.label = this.uri;
        }
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * @return the namespaceFile
     */
    @XmlAttribute(name = "namespaceFile", required= false)
    public String getNamespaceFile()
    {
        return namespaceFile;
    }

    public void setNamespaceFile(String namespaceFile)
    {
        this.namespaceFile = namespaceFile;
    }

    /**
     * @return the namespaceFile
     */
    @XmlAttribute(name = "namespaceIdFile", required= false)
    public String getNamespaceIdFile()
    {
        return namespaceIdFile;
    }

    public void setNamespaceIdFile(String namespaceIdFile)
    {
        this.namespaceIdFile = namespaceIdFile;
    }

    public Namespace getACopy()
    {
    	Namespace namespace = new Namespace();
    	namespace.setLabel(getLabel());
    	namespace.setUri(getUri());
    	namespace.setNamespaceFile(getNamespaceFile());
    	namespace.setNamespaceIdFile(getNamespaceIdFile());
		return namespace;
    }
}
