
package org.grits.toolbox.entry.sample.part.toolbar;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.entry.sample.dialog.AddComponentDialog;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.part.AnalyteEntryPart;
import org.grits.toolbox.entry.sample.part.IAnalytePartTab;
import org.grits.toolbox.entry.sample.utilities.AnalyteFactory;

public class AddComponentHandler
{
	public static final String TOOL_ITEM_ID =
			"org.grits.toolbox.entry.sample.handledtoolitem.addcomponent";
	public static final String COMMAND_ID =
			"org.grits.toolbox.entry.sample.command.addcomponent";

	@Inject IEventBroker eventBroker;
	@Inject ISampleOntologyApi sampleOntologyApi;

	@Execute
	public void execute(Shell shell, MPart part, Sample sample)
	{

		if(part != null  && part.getObject() instanceof AnalyteEntryPart)
		{
			AnalyteEntryPart analyteEntryPart = (AnalyteEntryPart) part.getObject();
			AddComponentDialog addComponentDialog = new AddComponentDialog(shell,
					sample, sampleOntologyApi);
			if(addComponentDialog.open() == Window.OK)
			{
				Component component = getComponent(addComponentDialog);
				component.setComponentId(sample.getNextAvailableComponentId());
				sample.addComponent(component);
				analyteEntryPart.addNewComponentTab(part, component);
				eventBroker.post(IAnalytePartTab.EVENT_TOPIC_ANALYTE_MODIFIED, sample);
				eventBroker.post(IAnalytePartTab.EVENT_TOPIC_OPEN_COMPONENT_TAB, component);
			}
		}
	}

	private Component getComponent(AddComponentDialog addComponentDialog)
	{
		Component component = null;

		switch(addComponentDialog.selectedOption)
		{
		case AddComponentDialog.COPY_FROM_SAMPLE :
			component = addComponentDialog.getComponentToBeCopied();
			String description = "Copy of another component : "
					+ component.getLabel();
			description += component.getDescription() == null ?
					"" : "\n(  " + component.getDescription() + "  )";
			component.setDescription(description);
			break;

		case AddComponentDialog.COPY_FROM_TEMPLATE :
			component = AnalyteFactory.createComponentFromTemplate(
					addComponentDialog.getSelectedTemplate());
			break;

		default :
			component = new Component();
			break;
		}

		component.setLabel(addComponentDialog.getComponentName());
		return component;
	}
}