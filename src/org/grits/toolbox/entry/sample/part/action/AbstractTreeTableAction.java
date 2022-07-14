/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.grits.toolbox.entry.sample.model.Component;
import org.grits.toolbox.entry.sample.ontologymanager.ISampleOntologyApi;

/**
 * 
 *
 */
public abstract class AbstractTreeTableAction extends Action
{
	@Inject protected ISampleOntologyApi sampleOntologyApi;
	@Inject protected MDirtyable dirtyable;
	@Inject protected IEventBroker eventBroker;

	protected Component component = null;
	protected TreeViewer treeViewer = null;
	protected TableViewer tableViewer = null;

	public void init(Component component, TreeViewer treeViewer)
	{
		this.component = component;
		this.treeViewer = treeViewer;
		this.setUp();
	}

	public void init(TableViewer tableViewer)
	{
		this.tableViewer = tableViewer;
		this.setUp();
	}

	protected abstract void setUp();
}
