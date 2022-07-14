package org.grits.toolbox.entry.sample.io;

import java.io.IOException;

import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.io.PropertyReader;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.jdom.Element;

/**
 * Reader for sample entry. Should check for empty values
 * @author kitaemyoung
 *
 */
public class SamplePropertyReader extends PropertyReader
{
	@Override
	public Property read(Element propertyElement) throws IOException, UnsupportedVersionException
	{
		SampleProperty property = new SampleProperty();

		PropertyReader.addGenericInfo(propertyElement, property);

		if(property.getVersion() == null)
		{
			return ReaderVersion0.read(propertyElement, property);
		}
		else if(property.getVersion().equals("1.0"))
		{
			return ReaderVersion1.read(propertyElement, property);
		}
		else 
			throw new UnsupportedVersionException("This version is currently not supported.",
					property.getVersion());
	}
}
