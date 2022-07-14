
package org.grits.toolbox.entry.sample.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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
import org.grits.toolbox.core.service.IGritsDataModelService;
import org.grits.toolbox.core.service.IGritsUIService;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.utilities.UtilityFile;
import org.grits.toolbox.entry.sample.wizard.batchofanalytes.BatchOfAnalytesWizard;

public class NewBatchOfAnalytesHandler
{
	private static final Logger logger = Logger.getLogger(NewBatchOfAnalytesHandler.class);

	@Inject static IGritsDataModelService gritsDataModelService = null;
	@Inject static IGritsUIService gritsUIService = null;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) Object object,
			IEventBroker eventBroker, @Named (IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		logger.info("- START   COMMAND : Create batch of new Analytes. ");
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

			List<Entry> analyteEntries = createNewAnalytes(shell, selectedEntry);
			if(!analyteEntries.isEmpty())
			{
				Entry firstEntry = analyteEntries.iterator().next();
				logger.info("Opening one of the created new Analytes : " + firstEntry.getDisplayName());
				eventBroker.post(IGritsDataModelService.EVENT_SELECT_ENTRY, firstEntry);
				gritsUIService.openEntryInPart(firstEntry);
			}
		} catch (Exception ex)
		{
			logger.fatal("Error creating batch of new Analytes : " + ex.getMessage(), ex);
			MessageDialog.openError(shell, "Error Creating Analytes",
					"Unable to create batch of new Analytes.\n" + ex.getMessage());
		}
		logger.info("- END   COMMAND : Create batch of new Analytes. ");
	}

	private List<Entry> createNewAnalytes(Shell shell, Entry projectEntry)
	{
		logger.info("Opening wizard for creating batch of analytes");
		List<Entry> analyteEntries = new ArrayList<Entry>();
		BatchOfAnalytesWizard wizard = new BatchOfAnalytesWizard();
		wizard.setWindowTitle("Analytes Wizard");
		wizard.setProjectEntry(projectEntry);
		WizardDialog dialog = new WizardDialog(PropertyHandler.getModalDialog(shell), wizard);
		if (dialog.open() == Window.OK) 
		{
			Entry selectedProjectEntry = wizard.getProjectEntry();
			logger.info("Retrieving sample folder for the selected project \""
					+ selectedProjectEntry.getDisplayName() + "\"");
			File sampleFolder = 
					UtilityFile.getSampleGroupDirectory(selectedProjectEntry.getDisplayName());
			try
			{
				Entry newAnalyteEntry = null;
				String newSampleFileName = null;
				for(Sample sample : wizard.getSamples())
				{
					try
					{
						newAnalyteEntry = null;
						newSampleFileName =
								UtilityFile.generateFileName(sampleFolder.list());
						logger.info("Creating sample file \"" + newSampleFileName 
								+ "\" for sample \"" + sample.getName() + "\"");
						UtilityFile.writeToFile(sample,
								sampleFolder.getAbsolutePath() 
								+ File.separator + newSampleFileName);

						logger.info("Create analyte entry \"" + sample.getName() +
								"and add it to the project entry \""
								+ selectedProjectEntry.getDisplayName() + "\"");
						newAnalyteEntry = NewAnalyteHandler.createEntry(sample.getName(), newSampleFileName);
						gritsDataModelService.addEntry(selectedProjectEntry, newAnalyteEntry);
						analyteEntries.add(newAnalyteEntry);

					} catch (Exception e)
					{
						logger.error(e.getMessage(), e);
						if(newAnalyteEntry != null)
						{
							logger.error("Something went wrong creating this analyte \""
									+ newAnalyteEntry.getDisplayName() + "\" in this project \""
									+ selectedProjectEntry.getDisplayName() + "\". Removing this entry from project.");
							gritsDataModelService.removeEntry(selectedProjectEntry, newAnalyteEntry);
						}
					}
				}
				try
				{
					ProjectFileHandler.saveProject(selectedProjectEntry);
				} catch (IOException e)
				{
					logger.fatal("Something went wrong while saving project entry \""
							+ selectedProjectEntry.getDisplayName()
							+ "\" Removing all new analyte entries from the project.\n"
							+ e.getMessage(), e);
					for(Entry analyteEntry : analyteEntries)
					{
						gritsDataModelService.removeEntry(selectedProjectEntry, analyteEntry);
					}
					logger.fatal("Closing project entry \""
							+ selectedProjectEntry.getDisplayName() + "\"");
					gritsDataModelService.closeProject(selectedProjectEntry);
					analyteEntries.clear();
					throw e;
				}
			} catch (IOException e)
			{
				logger.error(e.getMessage(),e);
				MessageDialog.openError(shell, "Error Creating Analytes",
						"Something went wrong while creating new analyte entries.\n" + e.getMessage());
			}
		}
		return analyteEntries;
	}
}