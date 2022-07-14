package org.grits.toolbox.entry.sample.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.core.datamodel.io.PropertyWriter;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.jdom.Attribute;
import org.jdom.Element;

public class SamplePropertyWriter implements PropertyWriter
{
	@Override
	public void write(Property property, Element propertyElement) throws IOException
	{
		if(property instanceof SampleProperty)
		{
			SampleProperty sampleProperty = (SampleProperty) property;
			PropertyDataFile sampleFile = sampleProperty.getSampleFile();
			if(sampleFile != null && sampleFile.getName() != null)
			{
				Element fileElement = new Element("file");
				List<Attribute> attributes = new ArrayList<Attribute>();
				attributes.add(new Attribute("name", sampleFile.getName()));
				String version = sampleFile.getVersion() == null ? 
						Sample.CURRENT_VERSION : sampleFile.getVersion();
				attributes.add(new Attribute("version", version));
				attributes.add(new Attribute("type", PropertyDataFile.DEFAULT_TYPE));
				fileElement.setAttributes(attributes);
				propertyElement.setContent(fileElement);
			}
			else
				throw new IOException("Property could not be added as its sample file (or name) is null.");
		}
		else
		{
			throw new IOException("This property is not a Sample Property");
		}
	}

}
