package org.grits.toolbox.entry.sample.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.io.PropertyWriter;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.core.datamodel.util.DataModelSearch;
import org.grits.toolbox.core.utilShare.DeleteUtils;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.io.SamplePropertyWriter;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.utilities.UtilityFile;

public class SampleProperty extends Property
{
	private static Logger logger = Logger.getLogger(SampleProperty.class);
	public static final String SAMPLE_FOLDER = "sample";
	public static final String TYPE = "org.grits.toolbox.property.sample";
	protected static ImageDescriptor imageDescriptor =
			ImageRegistry.getImageDescriptor(SampleImage.SAMPLE_ICON_SMALL);
	protected static PropertyWriter writer = new SamplePropertyWriter();

	public SampleProperty()
	{
		super();
	}

	public PropertyDataFile getSampleFile()
	{
		logger.info("Getting sample property data file name");
		PropertyDataFile sampleFile = null;
		for(PropertyDataFile dataFile : dataFiles)
		{
			if(PropertyDataFile.DEFAULT_TYPE.equals(dataFile.getType()))
			{
				sampleFile = dataFile;
				logger.info("Sample property data file name found with type "
						+ PropertyDataFile.DEFAULT_TYPE + " [" + sampleFile.getName()
						+ ", version - " + sampleFile.getVersion() + "]");
				break;
			}
		}
		return sampleFile;
	}

	@Override
	public String getType() {
		return SampleProperty.TYPE;
	}

	@Override
	public PropertyWriter getWriter() {
		return SampleProperty.writer;
	}

	@Override
	public ImageDescriptor getImage() {
		return SampleProperty.imageDescriptor;
	}

	@Override
	public void delete(Entry entry)
	{
		logger.info("Deleting sample entry");
		String sampleGroupFolderLocation = UtilityFile.getSampleFolderForSample(entry).getAbsolutePath();
		PropertyDataFile sampleDataFile = getSampleFile();
		if(sampleDataFile != null)
		{
			String fileLocation = sampleGroupFolderLocation 
					+ File.separator 
					+ sampleDataFile.getName();
			try
			{
				DeleteUtils.delete(new File(fileLocation));
				logger.info("Sample file deleted " + fileLocation);
			} catch (IOException e)
			{
				logger.error("Error deleting sample file.\n" + e.getMessage(), e);
				ErrorUtils.createErrorMessageBox(Display.getCurrent().getActiveShell(), "Cannot Delete Entry", e);
			}
		}
	}

	@Override
	public Object clone()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getParentProperty()
	{
		return null;
	}

	public static Sample loadAnalyte(Entry sampleEntry) throws FileNotFoundException, JAXBException, UnsupportedEncodingException
	{
		logger.info("Loading sample object for entry");
		Sample sample = null;
		if(sampleEntry != null && sampleEntry.getProperty() != null
				&& sampleEntry.getProperty() instanceof SampleProperty)
		{
			logger.info("Sample entry : " + sampleEntry.getDisplayName());
			SampleProperty sampleProperty = (SampleProperty) sampleEntry.getProperty();
			PropertyDataFile sampleDataFile = sampleProperty.getSampleFile();
			if(sampleDataFile == null || sampleDataFile.getName() == null)
			{
				logger.error("Missing file name from the sample Entry "
						+ sampleEntry.getDisplayName());
				throw new FileNotFoundException("Missing file name from the Sample Entry "
						+ sampleEntry.getDisplayName());
			}

			if(Sample.CURRENT_VERSION.equals(sampleDataFile.getVersion()))
			{
				File file = new File(UtilityFile.getSampleFolderForSample(sampleEntry)
						+ File.separator + sampleDataFile.getName());
				logger.info("Loading File : " + file.getAbsolutePath());
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
				InputStreamReader reader = new InputStreamReader(inputStream, 
						PropertyHandler.GRITS_CHARACTER_ENCODING);
				JAXBContext context = JAXBContext.newInstance(Sample.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				sample = (Sample) unmarshaller.unmarshal(reader);
				for(Component component : sample.getComponents())
				{
					if(component.getComponentId() == null)
					{
						component.setComponentId(sample.getNextAvailableComponentId());
					}
				}
				logger.info("Sample loaded from file : " + file.getAbsolutePath());
			}
		}
		return sample;
	}

	@Override
	public void makeACopy(Entry currentEntry, Entry destinationEntry)
			throws IOException
	{
		if(currentEntry == null || destinationEntry == null)
		{
			logger.fatal("Cannot copy entry for null values : " + currentEntry + " - " + destinationEntry);
			throw new IOException("Cannot copy entry for null values : " + currentEntry + " - " + destinationEntry);
		}
		try
		{
			logger.info("Copying sample " + currentEntry.getDisplayName() +
					" to " + destinationEntry.getDisplayName());
			File currentSampleFile = new File(UtilityFile
					.getSampleFolderForSample(currentEntry), getSampleFile().getName());
			if(currentSampleFile.exists())
			{
				File destinationSampleFolder = 
						UtilityFile.getSampleGroupDirectory(destinationEntry.getParent().getDisplayName());
				if(!destinationSampleFolder.exists() || !destinationSampleFolder.isDirectory()) 
				{
					logger.info("Creating Sample folder in destination entry : "
							+ destinationSampleFolder.getAbsolutePath());
					destinationSampleFolder.mkdir();
				}
				File destinationSampleFile = new File(destinationSampleFolder, 
						UtilityFile.generateFileName(destinationSampleFolder.list()));
				logger.info("Copying sample file : from [" + currentSampleFile.getAbsolutePath()
						+ "] to [" + destinationSampleFile.getAbsolutePath() + "]");
				Files.copy(currentSampleFile.toPath(), destinationSampleFile.toPath());
				List<PropertyDataFile> dataFiles = new ArrayList<PropertyDataFile>();
				PropertyDataFile currentSampleDataFile = getSampleFile();
				dataFiles.add(new PropertyDataFile(destinationSampleFile.getName(), 
						currentSampleDataFile.getVersion(), currentSampleDataFile.getType()));
				SampleProperty sampleProperty = new SampleProperty();
				sampleProperty.setDataFiles(dataFiles);
				sampleProperty.setRemoved(!exists());
				sampleProperty.setVersion(getVersion());
				sampleProperty.setViewerRank(getViewerRank());
				destinationEntry.setProperty(sampleProperty);
				logger.info("Sample copied");
			}
			else throw new FileNotFoundException("Could not find sample file for selected sample \"" 
					+ currentEntry.getDisplayName() + "\" in project \"" 
					+ DataModelSearch.findParentByType(currentEntry, ProjectProperty.TYPE).getDisplayName()
					+ "\"");
		} catch (FileNotFoundException ex)
		{
			logger.error(ex.getMessage(), ex);
			throw ex;
		} catch (IOException ex)
		{
			logger.error("Error copying sample information.\n" + ex.getMessage(), ex);
			throw new IOException("Error copying sample information.\n" + ex.getMessage(), ex);
		}
	}
}
