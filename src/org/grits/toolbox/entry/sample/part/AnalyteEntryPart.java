
package org.grits.toolbox.entry.sample.part;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.grits.toolbox.core.dataShare.IGritsConstants;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.io.ProjectFileHandler;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.core.service.IGritsDataModelService;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.entry.sample.config.Config;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.part.toolbar.AddComponentHandler;
import org.grits.toolbox.entry.sample.property.SampleProperty;
import org.grits.toolbox.entry.sample.utilities.AnalyteFactory;
import org.grits.toolbox.entry.sample.utilities.UtilityFile;

public class AnalyteEntryPart
{
	private static Logger logger = Logger.getLogger(AnalyteEntryPart.class);

	public static final String PART_ID = "org.grits.toolbox.partdescriptor.entry.analyte.default";

	private Entry analyteEntry = null;
	private File analyteFile = null;
	private Sample sample = null;

	private CTabFolder cTabFolder = null;
	private Map<CTabItem, IAnalytePartTab> cTabItemToPartTabMap =
			new HashMap<CTabItem, IAnalytePartTab>();

	@Inject private MDirtyable dirtyable = null;

	@Inject
	public AnalyteEntryPart(@Named(IServiceConstants.ACTIVE_SELECTION) Entry entry,
			IGritsDataModelService gritsDataModelService,
			@Named(IGritsConstants.WORKSPACE_LOCATION) String workspaceLocation)
	{
		try
		{
			if(entry != null && SampleProperty.TYPE.equals(entry.getProperty().getType()))
			{
				this.analyteEntry = entry;
				loadSample(gritsDataModelService, workspaceLocation);
			}
		} catch (IOException e)
		{
			logger.fatal(e.getMessage(), e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Loading Sample", 
					"Sample could not be loaded from the file.\n"
							+ e.getMessage());
		}
		catch (Exception ex)
		{
			logger.fatal(ex.getMessage(), ex);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Creating Part", 
					"Some unexpected error occurred while opening the editor. "
							+ "Please contact developers for further information/help.");
		}
	}

	@PostConstruct
	public void postConstruct(Composite parent, final MPart part)
	{
		logger.info("loading sample preference");
		part.getContext().set(Sample.class, sample);
		parent.setLayout(new FillLayout());
		logger.info("Creating tabs");
		cTabFolder = new CTabFolder(parent, SWT.NONE);
		cTabFolder.setTabPosition(SWT.BOTTOM);
		//cTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cTabFolder.setSimple(false);
		part.getContext().set(CTabFolder.class, cTabFolder);

		try
		{
			createPartTabs(part);
			cTabFolder.addSelectionListener(new SelectionListener()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					changeTab(e);
				}

				private void changeTab(SelectionEvent e)
				{
					int selectionIndex = cTabFolder.getSelectionIndex();

					logger.info("Tab changed to " + selectionIndex);
					selectionIndex = selectionIndex < 0 ? 0 : selectionIndex;
					IAnalytePartTab currentTab = cTabItemToPartTabMap.get(cTabFolder.getSelection());
					for(MToolBarElement toolBarElement : part.getToolbar().getChildren())
					{
						toolBarElement.setVisible(selectionIndex > 0 ||
								AddComponentHandler.TOOL_ITEM_ID.equals(toolBarElement.getElementId()));
					}
					part.getContext().set(IAnalytePartTab.class, currentTab);

					// an added notification for the selected tab for specialized action
					cTabFolder.getSelection().notifyListeners(SWT.Selection, new Event());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					changeTab(e);
				}
			});

			int selectionIndex = sample.getComponents().size() == 1 ? 1 : 0;
			cTabFolder.setSelection(selectionIndex);
			cTabFolder.notifyListeners(SWT.Selection, new Event());
		}
		catch (Exception e)
		{
			logger.fatal("Error while adding tabs to the analyte part.\n" + e.getMessage(), e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error Creating Page", 
					"Some unexpected error occurred while opening the editor. "
							+ "Please contact developers for further information/help.");
			logger.fatal(e.getMessage(), e);
		}

		logger.info("END   : Creating Analyte Entry Part. ");
	}

	private void createPartTabs(MPart part)
	{
		logger.info("Adding overview tabs");
		CTabItem cTabItem = new CTabItem(cTabFolder, SWT.NONE);
		cTabItem.setText(Config.OVERVIEW_PAGE_TITLE);
		cTabItem.setShowClose(false);

		OverviewTab overviewTab = ContextInjectionFactory.make(
				OverviewTab.class, part.getContext());
		cTabItemToPartTabMap.put(cTabItem, overviewTab);

		logger.info("Adding remaining component tabs");
		for(Component component : sample.getComponents()) 
		{
			addNewComponentTab(part, component);
		}
	}

	public ComponentTab addNewComponentTab(MPart part, Component component)
	{
		logger.info("Adding component tab for " + component.getLabel());
		CTabItem cTabItem = new CTabItem(cTabFolder, SWT.NONE);
		cTabItem.setText(component.getLabel());
		cTabItem.setShowClose(false);

		part.getContext().set(Component.class, component);
		part.getContext().set(CTabItem.class, cTabItem);

		ComponentTab componentTab = ContextInjectionFactory.make(
				ComponentTab.class, part.getContext());
		cTabItemToPartTabMap.put(cTabItem, componentTab);
		return componentTab;
	}

	@Optional @Inject
	public void removeComponentTab(@UIEventTopic
			(IAnalytePartTab.EVENT_TOPIC_REMOVE_COMPONENT_TAB) Component component,
			MPart part, MDirtyable dirtyable)
	{
		if(component != null && part != null && this == part.getObject())
		{
			logger.info("Removing component tab for : " + component.getLabel());
			IAnalytePartTab partTabToRemove = null;
			for(IAnalytePartTab partTab : cTabItemToPartTabMap.values())
			{
				if(component == partTab.getInput())
				{
					partTabToRemove = partTab;
					break;
				}
			}

			if(partTabToRemove != null)
			{
				CTabItem itemToRemove = null;
				for(CTabItem item : cTabItemToPartTabMap.keySet())
				{
					if(cTabItemToPartTabMap.get(item) == partTabToRemove)
					{
						itemToRemove = item;
						break;
					}
				}

				ContextInjectionFactory.uninject(component, part.getContext());
				ContextInjectionFactory.uninject(partTabToRemove, part.getContext());
				cTabItemToPartTabMap.remove(itemToRemove);
				itemToRemove.dispose();
				cTabFolder.redraw();
				dirtyable.setDirty(true);
			}
			logger.info("Component tab removed for component : " + component.getLabel());
		}
	}

	private void loadSample(IGritsDataModelService gritsDataModelService,
			String workspaceLocation) throws IOException
	{
		try
		{
			loadSampleFile(gritsDataModelService, workspaceLocation);
			FileInputStream inputStream = new FileInputStream(analyteFile.getAbsolutePath());
			InputStreamReader reader = new InputStreamReader(inputStream, 
					PropertyHandler.GRITS_CHARACTER_ENCODING);
			try
			{
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
			} catch (JAXBException e) {
				logger.error("Error parsing the xml file.\n" + e.getMessage(), e);
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			logger.error("Error loading the file from the file location. " + e.getMessage(), e);
			throw e;
		}
	}

	private void loadSampleFile(IGritsDataModelService gritsDataModelService,
			String workspaceLocation) throws FileNotFoundException
	{
		logger.info("loading sample from file");
		SampleProperty sampleProperty = ((SampleProperty) analyteEntry.getProperty());
		PropertyDataFile samplePropertyFile = sampleProperty.getSampleFile();

		if(samplePropertyFile != null && samplePropertyFile.getName() != null)
		{
			Entry projectEntry =
					gritsDataModelService.findParentByType(analyteEntry, ProjectProperty.TYPE);
			String samplesFolderLocation = workspaceLocation
					+ File.separator
					+ projectEntry.getDisplayName()
					+ File.separator
					+ Config.SAMPLES_FOLDER_NAME;
			String fileName = samplePropertyFile.getName();
			if(Config.PREVIOUS_VERSION_SAMPLE_FILE_INDICATOR.equals(fileName))
			{
				try
				{
					fileName = AnalyteFactory.createSampleFileForOldSample(
							projectEntry.getDisplayName(), 
							analyteEntry.getDisplayName(), "");
					sampleProperty.getSampleFile().setName(fileName);
					ProjectFileHandler.saveProject(projectEntry);
				} catch (IOException e)
				{
					ErrorUtils.createErrorMessageBox(Display.getCurrent().getActiveShell(), 
							"Cannot Create Entry", new Exception());
				}
			}
			if(fileName != null && !fileName.isEmpty())
			{
				String fileLocation = samplesFolderLocation
						+ File.separator + fileName;
				analyteFile = new File(fileLocation);
			}
			else
			{
				String errorMessage = "Sample file name is null or empty";
				logger.error(errorMessage);
				throw new FileNotFoundException(errorMessage);
			}
		}
	}

	@PreDestroy
	public void preDestroy(MPart part)
	{
		ContextInjectionFactory.uninject(sample, part.getContext());
		ContextInjectionFactory.uninject(cTabFolder, part.getContext());
	}

	@Focus
	public void onFocus()
	{
		cTabItemToPartTabMap.get(cTabFolder.getSelection()).setFocus();
	}

	@Persist
	public void save()
	{
		if(UtilityFile.updateSampleFile(sample, analyteEntry))
		{
			dirtyable.setDirty(false);
		}
	}

	public Entry getAnalyteEntry()
	{
		return analyteEntry;
	}
}