/**
 * 
 */
package org.grits.toolbox.entry.sample.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.core.dataShare.IGritsConstants;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.core.service.IGritsDataModelService;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.property.SampleProperty;

/**
 * This class uses injected variables. Developers
 * should use {@link ContextInjectionFactory#make(Class, IEclipseContext)}
 * for instantiating this class
 * 
 */
@Singleton
public class UtilityFile
{
	private static Logger logger = Logger.getLogger(UtilityFile.class);

	@Inject @Named (IGritsConstants.WORKSPACE_LOCATION)
	private static String workspaceLocation;
	@Inject private static IGritsDataModelService gritsDataModelService;

	public static String generateFileName(String[] existingNames)
	{
		logger.info("Generating file name ");
		String fileName = "";
		int randomLength = 0;
		do 
		{
			fileName = Config.FILE_NAME_PREFIX;
			while(randomLength < Config.FILE_NAME_RANDOM_CHARACTERS_LENGTH) 
			{
				int randomcharacter = (int) (Math.random()*10);
				randomLength++;
				fileName = fileName + randomcharacter;
			}
			fileName = fileName + Config.FILE_TYPE_OF_SAMPLE;
		}
		while (Arrays.asList(existingNames).contains(fileName));
		logger.info("File name generated " + fileName);
		return fileName;
	}

	public static File getSampleGroupDirectory(String projectName)
	{
		String sampleFolderLocation = workspaceLocation 
				+ File.separator
				+ projectName
				+ File.separator
				+ Config.SAMPLES_FOLDER_NAME;
		File sampleFolder = new File(sampleFolderLocation);
		if(!sampleFolder.exists() || !sampleFolder.isDirectory()) 
		{
			sampleFolder.mkdir();
			logger.info("Sample folder created : " + sampleFolder.getAbsolutePath());
		}
		logger.info("Sample folder found : " + sampleFolder.getAbsolutePath());
		return sampleFolder;
	}


	public static File getSampleFolderForSample(Entry sampleEntry)
	{
		logger.info("Getting sample folder ");
		File sampleFile = null;
		if(sampleEntry != null)
		{
			logger.info("Sample folder for : " + sampleEntry.getDisplayName());
			Entry projectEntry = gritsDataModelService
					.findParentByType(sampleEntry, ProjectProperty.TYPE);
			sampleFile = getSampleGroupDirectory(projectEntry.getDisplayName());
		}
		return sampleFile;
	}

	public static boolean updateSampleFile(Sample sample, Entry analyteEntry)
	{
		logger.info("Updating file for analyte entry");
		boolean updated = false;
		try
		{
			try
			{
				SampleProperty sampleProperty = ((SampleProperty) analyteEntry.getProperty());
				PropertyDataFile sampleDataFile = sampleProperty.getSampleFile();
				if(sampleDataFile != null && sampleDataFile.getName() != null)
				{
					String fileLocation = UtilityFile.getSampleFolderForSample(analyteEntry) 
							+ File.separator + sampleDataFile.getName();
					writeToFile(sample, fileLocation);
					updated = true;
					logger.info("Sample updated in file : " + fileLocation);
				}
				else
				{
					logger.fatal("Sample entry does not have a sample file name.");
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							"Error Getting File", "Sample entry does not have a sample file name.");
				}
			} catch (IOException e) {
				logger.error("The changes made could not be written to the file.\n" + e.getMessage(), e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Writing File",
						"The changes made could not be written to the file.");

			} catch (JAXBException e) {
				logger.error("The changes made could not be serialized as xml.\n" + e.getMessage(), e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Parsing File",
						"The changes made could not be serialized to xml.");
			}
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Saving File",
					"The changes made could not be saved to the file. Please contact developers.");
		}
		return updated;
	}

	public static void writeToFile(Sample sample, String fileLocation) throws JAXBException, IOException
	{
		logger.info("Writing sample to file");
		if(sample == null || fileLocation == null || fileLocation.isEmpty())
			throw new IOException("Null empty value for analyte or its file."
					+ " Sample : " + sample + " Filelocation : " + fileLocation);

		logger.info("Serializing the sample object to xml");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		FileWriter fileWriter = null;
		try
		{
			JAXBContext context = JAXBContext.newInstance(Sample.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, PropertyHandler.GRITS_CHARACTER_ENCODING);
			marshaller.marshal(sample, os);

			logger.info("Writing the serialized data to the file");
			fileWriter = new FileWriter(fileLocation);
			fileWriter.write(os.toString((String) marshaller.getProperty(Marshaller.JAXB_ENCODING)));
		} finally
		{
			os.close();
			if(fileWriter != null)
				fileWriter.close();
		}
	}
}
