package org.grits.toolbox.entry.sample.io;

import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.jdom.Element;

/**
 * 
 * 
 *
 */
public class ReaderVersion1
{
	public static Property read(Element propertyElement, SampleProperty property)
	{
		return property;
	}
}
