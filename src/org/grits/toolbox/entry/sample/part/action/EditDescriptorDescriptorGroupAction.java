/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.dialog.AddDescriptorDialog;
import org.grits.toolbox.entry.sample.dialog.AddDescriptorGroupDialog;
import org.grits.toolbox.entry.sample.model.Descriptor;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;

/**
 * 
 *
 */
public class EditDescriptorDescriptorGroupAction extends AbstractTreeTableAction
{
	@Inject private IEclipseContext eclipseContext;

	@Override
	protected void setUp()
	{
		this.setToolTipText("Edit");
		this.setImageDescriptor(ImageRegistry
				.getImageDescriptor(SampleImage.EDIT_DESCRIPTOR_ICON));
	}

	public void run()
	{
		if(treeViewer != null)
		{
			if(!treeViewer.getSelection().isEmpty())
			{
				Object firstSelectedItem = 
						((StructuredSelection) treeViewer.getSelection()).getFirstElement();
				if(firstSelectedItem instanceof Descriptor)
				{
					AddDescriptorDialog descriptorDialog = new AddDescriptorDialog(
							Display.getCurrent().getActiveShell(), (Descriptor) firstSelectedItem);
					if(descriptorDialog.open() == Window.OK)
					{
						treeViewer.refresh();
						dirtyable.setDirty(true);
					}
				}
				else if(firstSelectedItem instanceof DescriptorGroup)
				{
					AddDescriptorGroupDialog descriptorGroupDialog = 
							ContextInjectionFactory.make(AddDescriptorGroupDialog.class, eclipseContext);
					descriptorGroupDialog.init((DescriptorGroup) firstSelectedItem);
					if(descriptorGroupDialog.open() == Window.OK)
					{
						treeViewer.refresh();
						dirtyable.setDirty(true);
					}
				}
			}
			else
			{
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
						"No Descriptor / Descriptor Group selected", 
						"Please select a descriptor or descriptor group to edit.");
			}
		}
		else if(tableViewer != null)
		{
			if(!tableViewer.getSelection().isEmpty())
			{
				Object firstSelectedItem =
						((StructuredSelection) tableViewer.getSelection()).getFirstElement();
				if(firstSelectedItem instanceof Descriptor)
				{
					AddDescriptorDialog descriptorDialog = new AddDescriptorDialog(
							Display.getCurrent().getActiveShell(), (Descriptor) firstSelectedItem);
					if(descriptorDialog.open() == Window.OK)
					{
						tableViewer.refresh();
					}
				}
			}
			else
			{
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
						"No Descriptor selected", 
						"Please select a descriptor to edit.");
			}
		}
	}
}

