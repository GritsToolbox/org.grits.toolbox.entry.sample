/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.grits.toolbox.core.img.ImageShare;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.model.Sample;
import org.grits.toolbox.entry.sample.part.IAnalytePartTab;

/**
 * 
 *
 */
public class DeleteComponentAction extends Action
{
	private IEventBroker eventBroker;
	private TableViewer tableViewer;

	public DeleteComponentAction(IEventBroker eventBroker, TableViewer tableViewer)
	{
		setup();
		this.eventBroker = eventBroker;
		this.tableViewer = tableViewer;
	}

	private void setup()
	{
		this.setToolTipText("Delete a Component");
		this.setImageDescriptor(ImageShare.DELETE_ICON);
	}

	public void run()
	{
		StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
		if(!selection.isEmpty())
		{
			Component component = (Component) selection.getFirstElement();
			Sample sample = (Sample) tableViewer.getInput();
			sample.getComponents().remove(component);
			tableViewer.refresh();
			eventBroker.post(IAnalytePartTab.EVENT_TOPIC_REMOVE_COMPONENT_TAB, component);
		}
	}
}
