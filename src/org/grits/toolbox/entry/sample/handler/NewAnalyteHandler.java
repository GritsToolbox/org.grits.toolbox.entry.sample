
package org.grits.toolbox.entry.sample.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.io.ProjectFileHandler;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.core.service.IGritsDataModelService;
import org.grits.toolbox.core.service.IGritsUIService;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.grits.toolbox.entry.sample.utilities.UtilityFile;
import org.grits.toolbox.entry.sample.wizard.analyte.NewAnalyteWizard;

public class NewAnalyteHandler
{
	private static final Logger logger = Logger.getLogger(NewAnalyteHandler.class);

	@Inject private static IGritsDataModelService gritsDataModelService = null;
	@Inject static IGritsUIService gritsUIService = null;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) Object object,
			IEventBroker eventBroker, @Named (IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		logger.info("- START COMMAND : Create a new Analyte. ");
		try
		{
			Entry selectedEntry = null;
			if(object instanceof Entry)
			{
				selectedEntry = (Entry) object;
			}
			else if (object instanceof StructuredSelection)
			{
				if(((StructuredSelection) object).getFirstElement() instanceof Entry)
				{
					selectedEntry = (Entry) ((StructuredSelection) object).getFirstElement();
				}
			}

			// try getting the last selection from the data model
			if(selectedEntry == null
					&& gritsDataModelService.getLastSelection() != null
					&& gritsDataModelService.getLastSelection().getFirstElement() instanceof Entry)
			{
				selectedEntry = (Entry) gritsDataModelService.getLastSelection().getFirstElement();
			}

			if(selectedEntry != null)
			{
				if(selectedEntry.getProperty() == null 
						|| !ProjectProperty.TYPE.equals(selectedEntry.getProperty().getType()))
				{
					selectedEntry = null;
				}
			}
			Entry newEntry = createNewAnalyte(shell, selectedEntry);
			if(newEntry != null)
			{
				eventBroker.post(IGritsDataModelService.EVENT_SELECT_ENTRY, newEntry);
				gritsUIService.openEntryInPart(newEntry);
			}
		} catch (Exception ex)
		{
			logger.fatal("Error creating a new Analyte : " + ex.getMessage(), ex);
			ErrorUtils.createErrorMessageBox(shell, "Unable to create an Analyte. ");
		}
		logger.info("- END   COMMAND : Create a new Analyte. ");
	}

	private Entry createNewAnalyte(Shell shell, Entry projectEntry)
	{
		logger.info("Opening Analyte Creation Wizard");
		Entry analyteEntry = null;

		NewAnalyteWizard wizard = new NewAnalyteWizard();
		wizard.setWindowTitle("New Analyte Wizard");
		wizard.setProjectEntry(projectEntry);
		WizardDialog dialog = new WizardDialog(PropertyHandler.getModalDialog(shell), wizard);

		if (dialog.open() == Window.OK)
		{
			logger.info("Creating Sample entry file");
			projectEntry = wizard.getProjectEntry();
			File sampleFolder = 
					UtilityFile.getSampleGroupDirectory(projectEntry.getDisplayName());
			String newSampleFileName =
					UtilityFile.generateFileName(sampleFolder.list());
			Sample sample = wizard.getSample();

			try
			{
				UtilityFile.writeToFile(sample,
						sampleFolder.getAbsolutePath() 
						+ File.separator + newSampleFileName);

				logger.info("Create analyte entry \"" + sample.getName() +
						"\" and add it to the project entry \"" 
						+ projectEntry.getDisplayName() + "\"");
				analyteEntry = createEntry(sample.getName(), newSampleFileName);
				gritsDataModelService.addEntry(projectEntry, analyteEntry);
				try
				{
					ProjectFileHandler.saveProject(projectEntry);
				} catch (IOException e)
				{
					logger.error("Something went wrong while saving project entry \n" + e.getMessage(),e);
					logger.fatal("Closing project entry \""
							+ projectEntry.getDisplayName() + "\"");
					gritsDataModelService.closeProject(projectEntry);
					throw e;
				}
			} catch (IOException | JAXBException e)
			{
				logger.error(e.getMessage(), e);
				MessageDialog.openError(shell, "Error Creating Analyte",
						"Something went wrong while creating analyte entry.\n" + e.getMessage());
			}
		}
		return analyteEntry;
	}

	public static Entry createEntry(String newEntryName, String newSampleFileName)
	{
		logger.info("Creating new sample entry");
		Entry newEntry = new Entry();
		newEntry.setDisplayName(newEntryName);
		SampleProperty property = new SampleProperty();
		List<PropertyDataFile> dataFiles = new ArrayList<PropertyDataFile>();
		PropertyDataFile samplePropertyFile =
				new PropertyDataFile(newSampleFileName,
						Sample.CURRENT_VERSION, PropertyDataFile.DEFAULT_TYPE);
		dataFiles.add(samplePropertyFile);
		property.setDataFiles(dataFiles);
		newEntry.setProperty(property);
		logger.info("New sample entry created");
		return newEntry;
	}
}