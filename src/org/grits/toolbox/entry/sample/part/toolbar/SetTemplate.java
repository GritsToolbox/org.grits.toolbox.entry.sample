
package org.grits.toolbox.entry.sample.part.toolbar;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.entry.sample.dialog.setTemplate.SetTemplateDialog;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Template;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;
import org.grits.toolbox.entry.sample.part.IAnalytePartTab;

public class SetTemplate
{
	private Logger logger = Logger.getLogger(SetTemplate.class);

	@Inject IEventBroker eventBroker;
	@Inject ISampleOntologyApi sampleOntologyApi;

	@Execute
	public void execute(Shell shell, IAnalytePartTab analytePartTab)
	{
		logger.info("setting template for component");
		Component component = (Component) analytePartTab.getInput();
		logger.info("component name : " + component.getLabel());
		SetTemplateDialog dialog = 
				new SetTemplateDialog(shell, sampleOntologyApi, component);

		if(dialog.open() == SetTemplateDialog.OK)
		{
			Template template = dialog.getTemplate();
			String templateUri = template.getUri();
			logger.info("template selected : " + template.getLabel());

			try
			{
				if(templateUri == null)
				{
					logger.info("Creating new template in the ontology : " + template.getLabel());
					templateUri = sampleOntologyApi.createNewTemplate(template);
				}

				if(!templateUri.equals(component.getTemplateUri()))
				{
					logger.info("setting template for the component : " + templateUri);
					component.setTemplateUri(templateUri);
					eventBroker.post(IAnalytePartTab.EVENT_TOPIC_COMPONENT_MODIFIED, component);
				}
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
				MessageDialog.openError(shell, "Template Not Added",
						"Error setting template : " + template.getLabel()
						+ "\n\n" + e + "\n" + e.getMessage());
			}
		}
	}
}