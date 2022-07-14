
package org.grits.toolbox.entry.sample.part.toolbar;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.part.ComponentTab;
import org.grits.toolbox.entry.sample.part.IAnalytePartTab;
import org.grits.toolbox.entry.sample.utilities.ComponentNameValidator;

public class Rename
{
	@Inject IEventBroker eventBroker;

	@Execute
	public void execute(Shell shell,
			IAnalytePartTab analytePartTab, Sample sample)
	{
		if(analytePartTab instanceof ComponentTab)
		{
			Component component = ((ComponentTab) analytePartTab).getInput();
			InputDialog editNameDialog = new InputDialog(shell,
					"Rename Component", "Give a unique name to this component",
					component.getLabel(), new ComponentNameValidator(sample, component.getLabel()));
			if(editNameDialog.open() == Window.OK)
			{
				String newLabel = editNameDialog.getValue();
				if(!component.getLabel().equals(newLabel))
				{
					component.setLabel(newLabel);
					eventBroker.post(IAnalytePartTab.EVENT_TOPIC_COMPONENT_MODIFIED, component);
				}
			}
		}
	}
}