package org.grits.toolbox.entry.sample.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.io.PropertyReader;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.jdom.Attribute;
import org.jdom.Element;

/**
 * 
 * 
 *
 */
public class ReaderVersion0
{
	public static Property read(Element propertyElement, SampleProperty property) throws IOException, UnsupportedVersionException
	{
		Element sampleElement = propertyElement.getChild("sample");
		if(sampleElement == null)
		{
			throw new IOException("Sample property misses element \"sample\".");
		}

		Attribute fileNameElement = sampleElement.getAttribute("filename");
		if(fileNameElement != null)
		{
			List<PropertyDataFile> dataFiles = new ArrayList<PropertyDataFile>();
			PropertyDataFile dataFile = new PropertyDataFile(fileNameElement.getValue(), 
					Sample.CURRENT_VERSION, 
					PropertyDataFile.DEFAULT_TYPE);

			dataFiles.add(dataFile);
			property.setDataFiles(dataFiles);
			PropertyReader.UPDATE_PROJECT_XML = true;
			return property;
		}
		else 
			throw new UnsupportedVersionException("This version is not supported "
				+ "(version with no sample file).", Config.PREVIOUS_VERSION_SAMPLE_FILE_INDICATOR);
	}
}
