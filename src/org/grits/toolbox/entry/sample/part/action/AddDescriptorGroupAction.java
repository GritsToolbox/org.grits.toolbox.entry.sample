/**
 * 
 */
package org.grits.toolbox.entry.sample.part.action;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.entry.sample.config.ImageRegistry;
import org.grits.toolbox.entry.sample.config.ImageRegistry.SampleImage;
import org.grits.toolbox.entry.sample.dialog.AddDescriptorGroupDialog;
import org.grits.toolbox.entry.sample.model.Category;
import org.grits.toolbox.entry.sample.model.CategoryTemplate;
import org.grits.toolbox.entry.sample.model.DescriptorGroup;
import org.grits.toolbox.entry.sample.part.IAnalytePartTab;
import org.grits.toolbox.entry.sample.part.providers.CategoryTreeContentProvider;
import org.grits.toolbox.entry.sample.utilities.UtilityDescriptorDescriptorGroup;

/**
 * 
 *
 */
public class AddDescriptorGroupAction extends AbstractTreeTableAction
{
	@Inject private IEclipseContext eclipseContext;
	private List<DescriptorGroup> availableDescriptorGroups = null;

	private void loadAvailableGroupsForCategory()
	{
		availableDescriptorGroups  = new ArrayList<DescriptorGroup>();
		String categoryUri = ((Category) treeViewer.getInput()).getUri();
		availableDescriptorGroups = sampleOntologyApi.getCategoryDescriptorGroups(categoryUri);
	}

	@Override
	protected void setUp()
	{
		this.setText("Add Descriptor Group");
		this.setToolTipText("Add a new Descriptor Group");
		this.setImageDescriptor(ImageRegistry.getImageDescriptor(SampleImage.ADD_DESCRIPTOR_GROUP_ICON));
	}

	public void run()
	{
		loadAvailableGroupsForCategory();
		AddDescriptorGroupDialog descriptorGroupDialog = 
				ContextInjectionFactory.make(AddDescriptorGroupDialog.class, eclipseContext);
		descriptorGroupDialog.init(availableDescriptorGroups);
		if(descriptorGroupDialog.open() == Window.OK)
		{
			Category category = (Category) treeViewer.getInput();
			List<DescriptorGroup> prevDescriptorGroups = ((Category) category).getDescriptorGroups();
			DescriptorGroup newDescriptorGroup = descriptorGroupDialog.getDescriptorGroup().getACopy();
			int action = this.occurenceLessThanMax(prevDescriptorGroups, newDescriptorGroup);
			switch(action)
			{
			// add this descriptor group
			case 1 :
			{
				prevDescriptorGroups.add(newDescriptorGroup);
				treeViewer.refresh();
				dirtyable.setDirty(true);
				treeViewer.setExpandedState(newDescriptorGroup, true);
				break;
			}
			// cannot add this descriptor group as it violates maxOccurence
			case 0 :
			{
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), 
						"Add Descriptor Group Denied", 
						"You cannot add this descriptor group \"" 
								+ newDescriptorGroup.getLabel() 
								+ "\" more than its max occurence \"" 
								+ newDescriptorGroup.getMaxOccurrence() 
								+ "\"");
				break;
			}
			// remove the template and add this descriptor group
			case -1 :
			{
				prevDescriptorGroups.add(newDescriptorGroup);
				treeViewer.refresh();
				dirtyable.setDirty(true);
				treeViewer.setExpandedState(newDescriptorGroup, true);
				component.setTemplateUri(null);
				eventBroker.post(IAnalytePartTab.EVENT_TOPIC_COMPONENT_MODIFIED, component);
				break;
			}
			}
		}
	}

	private int occurenceLessThanMax(
			List<DescriptorGroup> prevDescriptorGroups,
			DescriptorGroup newDescriptorGroup)
	{
		CategoryTemplate categoryTamplate = 
				((CategoryTreeContentProvider) this.treeViewer.getContentProvider()).getCategoryTemplate();
		boolean isInTemplate = true;
		if(categoryTamplate != null)
		{
			isInTemplate = false;
			List<DescriptorGroup> dgGroups = UtilityDescriptorDescriptorGroup.getAllDescriptorGroups(categoryTamplate);
			for(DescriptorGroup dg : dgGroups)
			{
				if(dg.getUri().equals(newDescriptorGroup.getUri()))
				{
					newDescriptorGroup.setMaxOccurrence(dg.getMaxOccurrence());
					isInTemplate = true;
					break;
				}
			}
		}
		int templateValue = 0;
		if(!isInTemplate)
		{
			templateValue = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
					"Remove Template", 
					"This Descriptor Group \"" 
							+ newDescriptorGroup.getLabel() 
							+ "\" is not allowed in the template \"" 
							+ categoryTamplate.getTemplateURI() 
							+ "\". Do you want to remove the template?") ? -1 : -2;
		}
		if(templateValue >= -1)
		{
			int maxOccurrenceIsRight = 1;
			if(newDescriptorGroup.getMaxOccurrence() != null) {
				int prevOccurence = 0;
				for(DescriptorGroup prevDescriptorGroup : prevDescriptorGroups) {
					if(prevDescriptorGroup.getUri().equals(newDescriptorGroup.getUri())) {
						prevOccurence++;
					}
				}
				if(templateValue == 0)
				{
					maxOccurrenceIsRight = prevOccurence < newDescriptorGroup.getMaxOccurrence()
							? 1 : 0;
				}
				else
				{
					maxOccurrenceIsRight = prevOccurence < newDescriptorGroup.getMaxOccurrence()
							? -1 : 0;
				}
				return maxOccurrenceIsRight;
			}
			else {
				return templateValue == 0 ? 1 : -1;
			}
		}
		else
		{
			// don't remove the template
			return -2;
		}
	}
}
